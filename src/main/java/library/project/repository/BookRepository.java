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

}
