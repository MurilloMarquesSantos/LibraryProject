package library.project.repository;

import library.project.conn.ConnectionFactory;
import library.project.domain.Producer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProducerRepository {

    public static void insertProducer(Producer producer) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = preparedStatementInsert(conn, producer)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static PreparedStatement preparedStatementInsert(Connection conn, Producer producer) throws SQLException {
        String sql = "INSERT INTO `library`.`producer` (`producerName`) VALUES (?);";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, producer.getName());
        return ps;

    }
}
