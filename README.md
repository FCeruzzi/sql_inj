# Simple Java Search App

Questa è una piccola applicazione Java Swing che mostra come cercare frasi dentro un database SQLite locale. Il progetto usa Maven per la gestione delle dipendenze ed è pensato per essere importato ed eseguito facilmente anche da IntelliJ IDEA.

## Requisiti

- Java 17 o successivo
- Maven 3.8+

## Avvio rapido da riga di comando

```bash
mvn clean compile exec:java # Esecuzione applicazione
```

Al primo avvio verrà creato il file `search.db` nella cartella del progetto con alcuni dati di esempio. La finestra Swing mostrerà un campo di ricerca e la lista dei risultati.

## Struttura del codice

- `DatabaseManager`: crea il file SQLite la prima volta e popola la tabella con alcune frasi di esempio.
- `SearchRepository`: versione intenzionalmente **vulnerabile** che concatena l'input alla query SQL ed espone sia un attacco in-band sia uno error-based capaci di rivelare il nome della tabella `quotes`.
- `SearchApp`: costruisce l'interfaccia Swing con barra di ricerca e lista dei risultati usando la versione vulnerabile per scopi dimostrativi.

## Esecuzione dei test dimostrativi

Il progetto contiene una suite di test JUnit per mettere a confronto i due repository su entrambi i vettori d'attacco:

- `SearchRepositoryTest` prova una serie di diverse SQL injection su un codice progettato per **fallire**: gli attacchi restituiscono la lista dei valori nel db, rivelano il nome delle tabella usate, dimostrando l'esfiltrazione di metadati.

```bash
mvn -Dtest=SearchRepositoryTest test   # mostrerà due fallimenti attesi sulla versione vulnerabile
```

### Messaggi di log attesi

Durante l'esecuzione dei test è normale vedere:

- Avvisi SLF4J come `Failed to load class "org.slf4j.impl.StaticLoggerBinder"`: il driver SQLite include SLF4J ma non fornisce un backend di logging. Se vuoi un logger reale, aggiungi una dipendenza SLF4J (es. `slf4j-simple`), altrimenti l'avviso è innocuo.
- Avvisi su `System::load` e `--enable-native-access`: il driver SQLite carica librerie native. Con Java 17 appaiono solo come warning; per sopprimerli puoi avviare Maven con `--enable-native-access=ALL-UNNAMED`, ma non influiscono sull'esito dei test.

## Note

- Il database `search.db` può essere cancellato in qualsiasi momento: al successivo avvio verrà ricreato e ripopolato.
