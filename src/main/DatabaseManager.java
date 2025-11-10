import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.sqlite.SQLiteConfig;

/**
 * Configura il database SQLite alla prima esecuzione dell'applicazione.
 */
public final class DatabaseManager {
    private static final String DB_FILE = "search.db";
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_FILE;

    private DatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        initializeDatabase();
        return createConfiguredConnection();
    }

    private static void initializeDatabase() throws SQLException {
        Path dbPath = Path.of(DB_FILE);
        boolean needsSeed = Files.notExists(dbPath);
        try (Connection connection = createConfiguredConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS quotes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "text TEXT NOT NULL UNIQUE"
                    + ")");
            if (needsSeed) {
                seedDatabase(statement);
            }
        }
    }

    private static Connection createConfiguredConnection() throws SQLException {
        SQLiteConfig config = new SQLiteConfig();
        config.enableLoadExtension(true);
        return DriverManager.getConnection(JDBC_URL, config.toProperties());
    }

    private static void seedDatabase(Statement statement) throws SQLException {
        String[] sampleQuotes = {
                "La semplicità è la sofisticazione suprema.",
                "Impara dalle regole come un professionista, in modo da poterle rompere come un artista.",
                "La logica ti porterà da A a B. L'immaginazione ti porterà dappertutto.",
                "Non aspettare il momento giusto, crealo.",
                "Un viaggio di mille miglia comincia sempre con il primo passo."
        };
        for (String quote : sampleQuotes) {
            statement.executeUpdate("INSERT OR IGNORE INTO quotes(text) VALUES('" + quote.replace("'", "''") + "')");
        }
    }
}