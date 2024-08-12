package library.project.repository;

import library.project.conn.ConnectionFactory;
import library.project.domain.Book;
import library.project.domain.Publisher;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class BookRepository {

    public static void InsertBook(Book book) {
        log.info("Inserting new book '{}'", book.getName());
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = preparedStatementInsert(conn, book)) {
            ps.executeUpdate();
        } catch (SQLException | ParseException e) {
            log.error("Error while trying to add book", e);
        }

    }

    private static PreparedStatement preparedStatementInsert(Connection conn, Book book) throws SQLException, ParseException {
        String sql = "INSERT INTO `library`.`book` (`bookName`, `pubDate`, `price`, `publisherId`) VALUES (?, ?, ?, ?);";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, book.getName());
        ps.setInt(2, book.getPubDate());
        ps.setDouble(3, book.getPrice());
        ps.setInt(4, book.getPublisher().getId());
        return ps;

    }

    public static void InsertBookTransaction(List<Book> bookList) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            preparedStatementSaveInsertTransaction(conn, bookList);
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            log.error("Error while trying to add books", e);
        }


    }

    private static void preparedStatementSaveInsertTransaction(Connection conn, List<Book> bookList) {
        String sql = "INSERT INTO `library`.`book` (`bookName`, `pubDate`, `price`, `publisherId`) VALUES (?, ?, ?, ?);";
        for (Book b : bookList) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                log.info("Inserting book '{}' ", b.getName());
                ps.setString(1, b.getName());
                ps.setInt(2, b.getPubDate());
                ps.setDouble(3, b.getPrice());
                ps.setInt(4, b.getPublisher().getId());
                ps.execute();
            } catch (SQLException e) {
                log.error("Error while trying to insert new books", e);
            }
        }

    }

    public static List<Book> showBooks() {
        log.info("Retrieving books");
        List<Book> bookList = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = preparedStatementShow(conn);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Publisher publisher = Publisher.builder()
                        .name(rs.getString("Publisher"))
                        .id(rs.getInt("publisherId"))
                        .build();
                Book book = Book.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("bookName"))
                        .pubDate(rs.getInt("pubDate"))
                        .price(rs.getDouble("price"))
                        .publisher(publisher)
                        .build();
                bookList.add(book);
            }

        } catch (SQLException e) {
            log.error("Error while tying to retrieve books", e);

        }
        return bookList;
    }

    private static PreparedStatement preparedStatementShow(Connection conn) throws SQLException {
        String sql = """
                SELECT b.id, b.bookName, b.pubDate, b.price, p.publisherName AS Publisher , b.publisherId FROM library.book b
                INNER JOIN library.publisher p
                ON b.publisherId = p.Id;
                """;
        return conn.prepareStatement(sql);

    }

    public static List<Book> showBookByName(String name) {
        log.info("Retrieving book");
        List<Book> bookList = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = preparedStatementShowBookByName(conn, name);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Publisher publisher = Publisher.builder()
                        .name(rs.getString("Publisher"))
                        .id(rs.getInt("publisherId"))
                        .build();
                Book book = Book.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("bookName"))
                        .pubDate(rs.getInt("pubDate"))
                        .price(rs.getDouble("price"))
                        .publisher(publisher)
                        .build();
                bookList.add(book);
            }

        } catch (SQLException e) {
            log.error("Error while tying to retrieve books", e);

        }
        return bookList;
    }

    private static PreparedStatement preparedStatementShowBookByName(Connection conn, String name) throws SQLException {
        String sql = """
                SELECT b.id, b.bookName, b.pubDate, b.price, p.publisherName AS Publisher , b.publisherId FROM library.book b
                INNER JOIN library.publisher p
                ON b.publisherId = p.Id
                WHERE b.bookName LIKE ?;
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, String.format("%s%%", name));
        return ps;


    }

    public static void updateBook(Book book) {
        log.info("Updating book '{}'", book.getName());
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = preparedStatementUpdateBook(conn, book)) {
            ps.execute();
        } catch (SQLException e) {
            log.error("Error while trying to update book", e);
        }

    }

    private static PreparedStatement preparedStatementUpdateBook(Connection connection, Book book) throws SQLException {
        String sql = """
                UPDATE `library`.`book` SET `bookName` = ?,
                 `pubDate` = ?, `price` = ?, `publisherId` = ? WHERE (`Id` = ?);
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, book.getName());
        ps.setInt(2, book.getPubDate());
        ps.setDouble(3, book.getPrice());
        ps.setInt(4, book.getPublisher().getId());
        ps.setInt(5, book.getId());
        return ps;
    }

    public static Optional<Book> findBookById(int id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = preparedStatementFindById(conn, id)) {
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return Optional.empty();
            Publisher publisher = Publisher.builder()
                    .name(rs.getString("Publisher"))
                    .id(rs.getInt("publisherId"))
                    .build();
            Book book = Book.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("bookName"))
                    .pubDate(rs.getInt("pubDate"))
                    .price(rs.getDouble("price"))
                    .publisher(publisher)
                    .build();
            return Optional.of(book);
        } catch (SQLException e) {
            log.error("Error while trying to find publisher by id", e);
        }
        return Optional.empty();
    }

    private static PreparedStatement preparedStatementFindById(Connection conn, int id) throws SQLException {
        String sql = """
                SELECT b.id, b.bookName, b.pubDate, b.price, p.publisherName AS Publisher , b.publisherId FROM library.book b
                INNER JOIN library.publisher p
                ON b.publisherId = p.Id
                WHERE b.id = ?;
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }

    public static void deleteBook(int id) {
        log.info("Deleting book '{}'", id);
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = preparedStatementDelete(conn, id)) {
            ps.execute();
        } catch (SQLException e) {
            log.error("Error while trying to delete book", e);
        }
    }

    private static PreparedStatement preparedStatementDelete(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM `library`.`book` WHERE (`Id` = ? );";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;

    }


}
