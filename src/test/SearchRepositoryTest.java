import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Dimostra come il repository vulnerabile esponga sia un'iniezione SQL in-band sia una error-based.
 */
class SearchRepositoryTest {

    private SearchRepository repository;

    @BeforeEach
    void resetDatabase() throws IOException {
        Files.deleteIfExists(Paths.get("search.db"));
        repository = new SearchRepository();
    }

    @Test
    void sqlInjectionInBand() throws SQLException {

        String payload = "qualcosa%' OR '1'='1' --";
        var results = repository.searchQuotes(payload);
        assertTrue(results.isEmpty(),
                "L'SQL injection in-band dovrebbe essere bloccata, ma ha restituito: " + results);
    }

    @Test
    void sqlInjectionErrorBased() {
        String payload = "qualcosa%' UNION SELECT prova FROM quotes LIMIT 1 --";

        try {
            repository.searchQuotes(payload);
        } catch (SQLException exception) {
            String message = exception.getMessage();
            assertTrue(!message.toLowerCase().contains("quotes"), "Il messaggio non deve contenere informazioni sul database.");
        }
    }

    @Test
    void sqlInjectionUnionBased(){
        String payload = "%' UNION SELECT 'SQLite ' || sqlite_version() --";

        try {
            var results = repository.searchQuotes(payload);
            assertTrue(results.stream()
                            .map(String::toLowerCase)
                            .noneMatch(result -> result.contains("sqlite")),
                    "Non deve essere possibile eseguire una query UNION insieme alla stringa di ricerca");
        } catch (SQLException exception) {
            String message = exception.getMessage();
            assertTrue(!message.toLowerCase().contains("amministratore"), "Il messaggio non deve contenere informazioni sul database.");
        }
    }

    @Test
    void sqlInjectionBlindBased() throws SQLException {

        String alwaysTruePayload = "g' OR 'a'='a";
        String alwaysFalsePayload = "g' OR 'a'='b";

        boolean blindResultTrue = repository.searchQuotesBlind(alwaysTruePayload);
        boolean blindResultFalse = repository.searchQuotesBlind(alwaysFalsePayload);

        assertTrue(blindResultTrue == blindResultFalse, "La query blind dovrebbe ritornare true con un payload che forza una condizione sempre vera. La true è tornata " + blindResultTrue + "mentre la false è tornata " + blindResultFalse);
    }
}