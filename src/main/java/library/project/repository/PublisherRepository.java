package library.project.repository;

import library.project.conn.ConnectionFactory;
import library.project.domain.Publisher;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class PublisherRepository {

    public static void insertPublisher(Publisher publisher) {
        log.info("Inserting new publisher '{}'", publisher);
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = preparedStatementInsert(conn, publisher)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error while trying to add publisher '{}'", publisher.getName());
        }

    }

    private static PreparedStatement preparedStatementInsert(Connection conn, Publisher publisher) throws SQLException {
        String sql = "INSERT INTO `library`.`publisher` (`publisherName`) VALUES (?);";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, publisher.getName());
        return ps;

    }

    public static void insertPublisherTransaction(List<Publisher> publisherList) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            preparedStatementInsertTransaction(conn, publisherList);
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            log.error("Error while trying to add publishers", e);
        }

    }

    private static void preparedStatementInsertTransaction(Connection conn, List<Publisher> publisherList) {
        String sql = "INSERT INTO `library`.`publisher` (`publisherName`) VALUES (?);";
        for (Publisher p : publisherList) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                log.info("Inserting Publisher '{}'", p.getName());
                ps.setString(1, p.getName());
                ps.execute();
            } catch (SQLException e) {
                log.error("Error while trying to add publishers", e);
            }
        }
    }

    public static List<Publisher> showPublishers() {
        log.info("Retrieving publishers");
        List<Publisher> publisherList = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = preparedStatementShow(conn);
             ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                Publisher publisher = Publisher.builder()
                        .id(rs.getInt("Id"))
                        .name(rs.getString("publisherName"))
                        .build();
                publisherList.add(publisher);
            }
        } catch (SQLException e) {
            log.error("Error while trying to retrieve publishers", e);
        }
        return publisherList;
    }

    private static PreparedStatement preparedStatementShow(Connection conn) throws SQLException {
        String sql = "SELECT * FROM library.publisher;";
        return conn.prepareStatement(sql);

    }

    public static void deletePublisher(int id) {
        log.info("Deleting publisher '{}'", id);
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = preparedStatementDelete(conn, id)) {
            ps.execute();
        } catch (SQLException e) {
            log.error("Error while trying to delete publisher", e);
        }

    }

    private static PreparedStatement preparedStatementDelete(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM `library`.`publisher` WHERE (`Id` = ? );";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }

    public static void updatePublisher(Publisher publisher) {
        log.info("Updating publisher '{}'", publisher);
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = preparedStatementUpdate(conn, publisher)) {
            ps.execute();
        } catch (SQLException e) {
            log.error("Error while trying to update publisher '{}' ", publisher);
        }
    }

    private static PreparedStatement preparedStatementUpdate(Connection conn, Publisher publisher) throws SQLException {
        String sql = "UPDATE `library`.`publisher` SET `publisherName` = ? WHERE (`Id` = ?);";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, publisher.getName());
        ps.setInt(2, publisher.getId());
        return ps;
    }

    public static Optional<Publisher> findPublisherById(int id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = preparedStatementFindById(conn, id)) {
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return Optional.empty();
            return Optional.of(Publisher.builder()
                    .id(rs.getInt("Id"))
                    .name(rs.getString("publisherName")).build());
        } catch (SQLException e) {
            log.error("Error while trying to find publisher by id", e);
        }
        return Optional.empty();
    }

    private static PreparedStatement preparedStatementFindById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM library.publisher WHERE id = ? ;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }
}
