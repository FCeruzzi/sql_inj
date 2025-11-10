import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.List;

/**
 * Applicazione Swing minimale con un campo di ricerca sul database SQLite locale.
 */
public final class SearchApp {

    private final SearchRepository repository = new SearchRepository();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SearchApp().createAndShowGui());
    }

    private void createAndShowGui() {
        JFrame frame = new JFrame("Ricerca frasi");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setMinimumSize(new Dimension(500, 300));

        JPanel searchPanel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Cerca:");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Cerca");
        searchPanel.add(label);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JList<String> resultsList = new JList<>();
        JScrollPane scrollPane = new JScrollPane(resultsList);

        frame.add(searchPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        Runnable searchAction = () -> {
            String query = searchField.getText();
            try {
                List<String> results = repository.searchQuotes(query);
                resultsList.setListData(results.toArray(String[]::new));
            } catch (SQLException e) {
                resultsList.setListData(new String[]{"Errore nel contattare il database: " + e.getMessage()});
            }
        };

        searchButton.addActionListener(e -> searchAction.run());
        searchField.addActionListener(e -> searchAction.run());

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}