

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Metodo semplice per cercare testo nel database locale.
 */
public class SearchRepository {

    public List<String> searchQuotes(String query) throws SQLException {
        List<String> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }

        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {
            String sql = "SELECT text FROM quotes WHERE text LIKE '%" + query.trim() + "%' ORDER BY text";
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    results.add(resultSet.getString("text"));
                }
            }
        } catch (SQLException exception) {
            throw new SQLException("Errore durante l'esecuzione della query sulla tabella 'quotes': "
                    + exception.getMessage(), exception);
        }
        return results;
    }

    public boolean searchQuotesBlind(String query) throws SQLException {
        if (query == null || query.trim().isEmpty()) {
            return false;
        }

        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {
            String sql = "SELECT COUNT(*) AS total FROM quotes WHERE text = '" + query.trim() + "'";
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                if (resultSet.next()) {
                    return resultSet.getInt("total") > 0;
                }
            } catch (SQLException exception) {
                throw new SQLException("Errore durante l'esecuzione della query sulla tabella 'quotes': " + exception.getMessage(), exception);
            }
            return false;
        }
    }
}