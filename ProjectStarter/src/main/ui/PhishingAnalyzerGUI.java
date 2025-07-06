package ui;

import model.Email;
import model.EmailAnalysis;
import model.Event;
import model.EventLog;
import persistence.PersistenceManager;
import org.json.JSONObject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/*
 * PhishingAnalyzerGUI
 *
 * This class provides a Swing-based GUI for the Phishing Email Analyzer.
 * It integrates with the existing Email, EmailAnalysis, and PersistenceManager classes.
 *
 * Functionalities:
 *  - Add emails to an EmailAnalysis collection
 *  - View all emails and flagged emails
 *  - Display a summary report with a visual pie chart showing the distribution of flagged emails by major indicator
 *  - Save and load the application state
 */

public class PhishingAnalyzerGUI extends JFrame {

    // Data
    private EmailAnalysis emails;
    private PersistenceManager pm;
    private final String stateFilePath = "./data/appState.json";

    // GUI components
    private JTabbedPane tabbedPane;
    
    // "Add Email"
    private JTextField senderField;
    private JTextField subjectField;
    private JTextArea bodyArea;
    private JTextField urlField;
    private JButton addEmailButton;
    private JLabel addEmailStatusLabel;
    
    // "View All Emails"
    private DefaultListModel<Email> emailListModel;
    private JList<Email> emailList;
    private JButton viewEmailButton;
    
    // "View Flagged Emails"
    private DefaultListModel<Email> flaggedEmailListModel;
    private JList<Email> flaggedEmailList;
    private JButton viewFlaggedEmailButton;
    
    // "Summary Report"
    private JTextArea summaryReportArea;
    private SummaryGraphPanel summaryGraphPanel; // Custom panel for pie chart
    private JButton refreshReportButton;

    /*
     * Modifies: Initializes the EmailAnalysis and PersistenceManager, and sets up GUI components.
     * Effects: Creates the main GUI window.
     */

    public PhishingAnalyzerGUI() {
        emails = new EmailAnalysis();
        pm = new PersistenceManager();
        initComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /*
     * Initializes all GUI components and layouts.
     *
     * Requires: Called from constructor after emails and pm are initialized.
     * Modifies: Constructs and lays out menu bar, tabbed pane, and panels.
     * Effects: Sets up the complete GUI structure.
     */

    @SuppressWarnings("methodlength") //not a misuse
    private void initComponents() {

        //colour constants 
        Color backgroundColor = new Color(210, 235, 255); // Light blue
        Color panelColor = new Color(200, 225, 240);      // Slightly darker
        Color textColor = new Color(0, 50, 70);           // Dark teal for text
        Color buttonColor = new Color(170, 210, 220);     // Soft blue-green

        getContentPane().setBackground(backgroundColor);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(panelColor);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(textColor);

        JMenuItem saveItem = new JMenuItem("Save State");
        saveItem.setBackground(buttonColor);
        saveItem.setForeground(Color.BLACK);
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveApplicationState();
            }
        });
        
        JMenuItem loadItem = new JMenuItem("Load State");
        saveItem.setBackground(buttonColor);
        saveItem.setForeground(Color.BLACK);
        loadItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadApplicationState();
                updateEmailLists();
                updateSummaryReport();
            }
        });
        
        JMenuItem exitItem = new JMenuItem("Exit");
        saveItem.setBackground(buttonColor);
        saveItem.setForeground(Color.BLACK);
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Event Log:");
                for (Event event : EventLog.getInstance()) {
                    System.out.println(event);
                }
                System.exit(0);
            }
        });
        
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        add(menuBar, BorderLayout.SOUTH);
        
        // Create the tabbed pane.
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(panelColor);
        tabbedPane.setForeground(textColor);
        
        // "Add Email" Tab.
        JPanel addEmailPanel = new JPanel(new BorderLayout());
        addEmailPanel.setBackground(panelColor);

        JPanel addFormPanel = new JPanel(new GridBagLayout());
        addFormPanel.setBackground(panelColor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Sender field.
        gbc.gridx = 0; 
        gbc.gridy = 0;
        JLabel senderLabel = new JLabel("Sender:");
        senderLabel.setForeground(textColor);
        addFormPanel.add(senderLabel, gbc);

        gbc.gridx = 1;
        senderField = new JTextField(20);
        addFormPanel.add(senderField, gbc);
        
        // Subject field.
        gbc.gridx = 0; 
        gbc.gridy = 1;
        JLabel subjectLabel = new JLabel("Subject:");
        subjectLabel.setForeground(textColor);
        addFormPanel.add(subjectLabel, gbc);

        gbc.gridx = 1;
        subjectField = new JTextField(20);
        addFormPanel.add(subjectField, gbc);
        
        // Body area.
        gbc.gridx = 0; 
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        JLabel bodyLabel = new JLabel("Body:");
        bodyLabel.setForeground(textColor);
        addFormPanel.add(bodyLabel, gbc);

        gbc.gridx = 1;
        bodyArea = new JTextArea(5, 20);
        JScrollPane bodyScroll = new JScrollPane(bodyArea);
        addFormPanel.add(bodyScroll, gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // URL field.
        gbc.gridx = 0; 
        gbc.gridy = 3;
        JLabel urlLabel = new JLabel("URL:");
        urlLabel.setForeground(textColor);
        addFormPanel.add(urlLabel, gbc);
        
        gbc.gridx = 1;
        urlField = new JTextField(20);
        addFormPanel.add(urlField, gbc);
        
        // Add Email Button.
        gbc.gridx = 0; 
        gbc.gridy = 4; 
        gbc.gridwidth = 2;
        addEmailButton = new JButton("Add Email");
        addEmailButton.setBackground(buttonColor);
        addEmailButton.setForeground(Color.BLACK);
        addFormPanel.add(addEmailButton, gbc);
        
        // Status Label.
        gbc.gridy = 5;
        addEmailStatusLabel = new JLabel("");
        addEmailStatusLabel.setForeground(textColor);
        addFormPanel.add(addEmailStatusLabel, gbc);
        
        addEmailPanel.add(addFormPanel, BorderLayout.CENTER);

        // Add to tabbed pane
        tabbedPane.addTab("Add Email", addEmailPanel);
        
        // Action listener
        addEmailButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addEmail();
            }
        });
        
        // "View All Emails" Tab.
        JPanel viewAllPanel = new JPanel(new BorderLayout());
        viewAllPanel.setBackground(panelColor);

        emailListModel = new DefaultListModel<>();
        emailList = new JList<>(emailListModel);
        emailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            //renderer to show email subject instead of default
        emailList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Email) {
                    Email email = (Email) value;
                    setText(email.getSubject());
                }
                return this;
            }
        });

        JScrollPane allScroll = new JScrollPane(emailList);
        viewAllPanel.add(allScroll, BorderLayout.CENTER);

        viewEmailButton = new JButton("View Email Report");
        viewEmailButton.setBackground(buttonColor);
        viewEmailButton.setForeground(Color.BLACK);

        viewEmailButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewSelectedEmail(emailList);
            }
        });
        viewAllPanel.add(viewEmailButton, BorderLayout.SOUTH);

        tabbedPane.addTab("View All Emails", viewAllPanel);
        
        // "View Flagged Emails" Tab.
        JPanel viewFlaggedPanel = new JPanel(new BorderLayout());
        viewFlaggedPanel.setBackground(panelColor);

        flaggedEmailListModel = new DefaultListModel<>();
        flaggedEmailList = new JList<>(flaggedEmailListModel);
        flaggedEmailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            //renderer to show email subject instead of default
        flaggedEmailList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Email) {
                    Email email = (Email) value;
                    setText(email.getSubject());
                }
                return this;
            }
        });

        JScrollPane flaggedScroll = new JScrollPane(flaggedEmailList);
        viewFlaggedPanel.add(flaggedScroll, BorderLayout.CENTER);

        viewFlaggedEmailButton = new JButton("View Email Report");
        viewFlaggedEmailButton.setBackground(buttonColor);
        viewFlaggedEmailButton.setForeground(Color.BLACK);

        viewFlaggedEmailButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewSelectedEmail(flaggedEmailList);
            }
        });
        viewFlaggedPanel.add(viewFlaggedEmailButton, BorderLayout.SOUTH);

        tabbedPane.addTab("View Flagged Emails", viewFlaggedPanel);
        
        // "Summary Report" Tab.
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(panelColor);

        summaryReportArea = new JTextArea(5,30);
        summaryReportArea.setEditable(false);
        JScrollPane summaryScroll = new JScrollPane(summaryReportArea);
        
        summaryGraphPanel = new SummaryGraphPanel();
        summaryGraphPanel.setPreferredSize(new Dimension(400,300));
        
        // Use a split pane to display the text summary and the pie chart.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, summaryScroll, summaryGraphPanel);
        splitPane.setDividerLocation(150);
        summaryPanel.add(splitPane, BorderLayout.CENTER);
        
        refreshReportButton = new JButton("Refresh Report");
        refreshReportButton.setBackground(buttonColor);
        refreshReportButton.setForeground(Color.BLACK);

        refreshReportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateSummaryReport();
            }
        });
        summaryPanel.add(refreshReportButton, BorderLayout.SOUTH);

        tabbedPane.addTab("Summary Report", summaryPanel);
        
        // Add the tabbed pane to the frame.
        add(tabbedPane, BorderLayout.CENTER);

   
    }

    /*
     * Adds an email based on user input.
     *
     * Requires: Input fields (senderField, subjectField, bodyArea, urlField) contain valid strings.
     * Modifies: The EmailAnalysis collection and UI components.
     * Effects: Creates a new Email, computes its risk score, adds it to emails, clears inputs, and 
     * updates lists and summary.
     */
    private void addEmail() {
        String sender = senderField.getText().trim();
        String subject = subjectField.getText().trim();
        String body = bodyArea.getText().trim();
        String url = urlField.getText().trim();
        
        if (sender.isEmpty()) {
            addEmailStatusLabel.setText("Please fill in the sender id.");
            return;
        }
        
        Email email = new Email(sender, subject, body, url);
        email.calculateRiskScore();
        emails.addEmail(email);
        addEmailStatusLabel.setText("Email added successfully!");
        
        // Clear input fields.
        senderField.setText("");
        subjectField.setText("");
        bodyArea.setText("");
        urlField.setText("");
        
        updateEmailLists();
        updateSummaryReport();
    }

    /*
     * Updates the JList models for all emails and flagged emails.
     *
     * 
     * Requires: emails is properly populated.
     * Modifies: emailListModel and flaggedEmailListModel.
     * Effects: Refreshes the displayed lists for all emails and flagged emails.
     */
    private void updateEmailLists() {
        emailListModel.clear();
        for (Email email : emails.getAllEmails()) {
            emailListModel.addElement(email);
        }
        // Update Flagged Emails list.
        flaggedEmailListModel.clear();
        for (Email email : emails.getAllFlaggedEmails()) {
            flaggedEmailListModel.addElement(email);
        }
    }

    /*
     * Displays the detailed report for the selected email in a dialog.
     *
     * Requires: A valid Email is selected in the provided JList.
     * Effects: Displays a JOptionPane dialog with email details and the generated phishing report.
     */
    private void viewSelectedEmail(JList<Email> list) {
        Email selected = list.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "No email selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String details = "Sender: " + selected.getSender() + "\n"
                         + "Subject: " + selected.getSubject() + "\n" 
                         + "Body: " + selected.getBody() + "\n" 
                         + "URL: " + selected.getUrl() + "\n\n" 
                         + "Report:\n" + selected.generatePhishingReport();
        JOptionPane.showMessageDialog(this, details, "Email Report", JOptionPane.INFORMATION_MESSAGE);
    }

    /*
     * Updates the summary report: textual summary and pie chart.
     *
     * Requires: emails is populated.
     * Modifies: summaryReportArea and summaryGraphPanel.
     * Effects: Displays the most common indicator and flagged percentage in the text area;
     *          updates the pie chart based on computed indicator percentages.
     */
    private void updateSummaryReport() {
        StringBuilder summary = new StringBuilder();
        summary.append("Most common indicator:\n")
               .append(emails.getMostCommonIndicator())
               .append("\n\nFlagged email percentage:\n")
                .append(emails.getFlaggedPercentage());
        summaryReportArea.setText(summary.toString());
        
        Map<String, Double> data = computeIndicatorPercentages();
        summaryGraphPanel.setData(data);
    }

    /*
     * Computes the percentage distribution of flagged emails by major indicator.
     *
     * Requires: There is at least one flagged email (if not, returns empty map).
     * Effects: Iterates through flagged emails, counts each major indicator,
     *          computes percentages, and returns a Map (indicator -> percentage).
     *
     * returns Map with indicator names as keys and percentage values as values.
     */
    private Map<String, Double> computeIndicatorPercentages() {
        Map<String, Integer> counts = new HashMap<>();
        List<Email> flagged = emails.getAllFlaggedEmails();
        int total = flagged.size();
        Map<String, Double> percentages = new HashMap<>();
        
        if (total == 0) {
            return percentages;
        }
        
        // Count each indicator.
        for (Email email : flagged) {
            String indicator = email.getMajorIndicator();
            counts.put(indicator, counts.getOrDefault(indicator, 0) + 1);
        }
        // Compute percentages.
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            percentages.put(entry.getKey(), (entry.getValue() * 100.0) / total);
        }
        return percentages;
    }

    /*
     * Saves the current application state to a JSON file.
     *
     * Requires: emails is non-null.
     * Modifies: Writes data to the file specified by stateFilePath.
     * Effects: Converts emails to JSONObject via PersistenceManager, saves state to file,
     *          and notifies the user.
     */
    private void saveApplicationState() {
        JSONObject state = pm.convertEmailAnalysisToJson(emails);
        pm.saveState(stateFilePath, state);
        JOptionPane.showMessageDialog(this, "Application state saved to " + stateFilePath);
    }

    /*
     * Loads the application state from a JSON file.
     *
     * Requires: The state file exists and contains valid data.
     * Modifies: Updates emails with the loaded state.
     * Effects: Loads the JSONObject from file using PersistenceManager, converts it to EmailAnalysis,
     *          updates email lists and summary report, and notifies the user.
     */
    private void loadApplicationState() {
        JSONObject state = pm.loadState(stateFilePath);
        if (state.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No saved state found.", "Load State", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        emails = pm.convertJsonToEmailAnalysis(state);
        updateEmailLists();
        updateSummaryReport();
        JOptionPane.showMessageDialog(this, "Application state loaded from " + stateFilePath);
    }

}