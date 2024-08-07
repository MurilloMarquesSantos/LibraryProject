package library.project.repository;

import library.project.conn.ConnectionFactory;
import library.project.domain.Producer;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class ProducerRepository {

    public static void insertProducer(Producer producer) {
        log.info("Inserting new producer '{}'", producer.getName());
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = preparedStatementInsert(conn, producer)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error while trying to add producer '{}'", producer.getName());
        }

    }

    private static PreparedStatement preparedStatementInsert(Connection conn, Producer producer) throws SQLException {
        String sql = "INSERT INTO `library`.`producer` (`producerName`) VALUES (?);";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, producer.getName());
        return ps;

    }

    public static List<Producer> showProducers() {
        log.info("Retrieving producers");
        List<Producer> producerList = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = preparedStatementShow(conn);
             ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                Producer producer = Producer.builder()
                        .id(rs.getInt("Id"))
                        .name(rs.getString("producerName"))
                        .build();
                producerList.add(producer);
            }
        } catch (SQLException e) {
            log.error("Error while trying to retrieve producers", e);
        }
        return producerList;
    }

    private static PreparedStatement preparedStatementShow(Connection conn) throws SQLException {
        String sql = "SELECT * FROM library.producer;";
        return conn.prepareStatement(sql);

    }

    public static void deleteProducer(int id) {
        log.info("Deleting producer '{}'", id);
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = preparedStatementDelete(conn, id)) {
            ps.execute();
        } catch (SQLException e) {
            log.error("Error while trying to delete producer", e);
        }

    }

    private static PreparedStatement preparedStatementDelete(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM `library`.`producer` WHERE (`Id` = ? );";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }

    public static void updateProducer(Producer producer) {
        log.info("Updating producer '{}'", producer);
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = preparedStatementUpdate(conn, producer)) {
            ps.execute();
        } catch (SQLException e) {
            log.error("Error while trying to update producer '{}' ", producer);
        }
    }

    private static PreparedStatement preparedStatementUpdate(Connection conn, Producer producer) throws SQLException {
        String sql = "UPDATE `library`.`producer` SET `producerName` = ?  WHERE (`Id` = ? );";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, producer.getName());
        ps.setInt(2, producer.getId());
        return ps;
    }

    public static Optional<Producer> findById(int id) {

        log.info("Finding producer by Id '{}'", id);
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = preparedStatementFindById(conn, id)) {
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return Optional.empty();
            return Optional.of(Producer.builder()
                    .id(rs.getInt("Id"))
                    .name(rs.getString("producerName")).build());
        } catch (SQLException e) {
            log.error("Error while trying to find producer by id", e);
        }
        return Optional.empty();
    }

    private static PreparedStatement preparedStatementFindById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM library.producer WHERE (´id´ = ? );";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }
}
