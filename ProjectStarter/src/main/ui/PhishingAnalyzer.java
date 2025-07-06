package ui;

import model.Email;
import model.EmailAnalysis;

import persistence.PersistenceManager;
import org.json.JSONObject;

import java.util.List;
import java.util.Scanner;

/*
 * The PhishingAnalyzer class is a console-based user interface for the Phishing Email Analyzer.
 * It handles user interactions, including adding emails, viewing the list of emails and individual email reports,
 * displaying the list of flagged emails, and showing overall summary statistics,
 * and saving/loading the entire application state to/from a file.
 */

public class PhishingAnalyzer {
    
    private EmailAnalysis emails;
    private Scanner input;

    private PersistenceManager pm;
    private final String stateFilePath = "./data/appState.json";
    
    /*
     * Constructs a PhishingAnalyzer object.
     * 
     * Modifies: Initializes the EmailAnalysis instance and Scanner.
     * Effects: Prepares the console interface for user interaction
     */
    public PhishingAnalyzer() {
        emails = new EmailAnalysis();
        input = new Scanner(System.in);
        pm = new PersistenceManager();
    }
    
    /*
     * Starts the console user interface.
     * 
     * Modifies: Reads user input from the console.
     * Effects: Displays the menu and processes user choices until the user exits the application.
     */
    @SuppressWarnings("methodlength")
    public void start() {
        boolean exit = false;
        while (!exit) {
            displayMenu();
            System.out.print("Enter your choice: ");
            int choice = Integer.parseInt(input.nextLine()); //Converts String to int as it initially stores as string
            if (choice == 1) {
                addEmail();
            } else if (choice == 2) {
                viewAllEmails();
            } else if (choice == 3) {
                viewFlaggedEmails();
            } else if (choice == 4) {
                viewSummaryReport();
            } else if (choice == 5) {
                saveApplicationState();
            } else if (choice == 6) {
                loadApplicationState();
            } else if (choice == 7) {
                exit = true;
                System.out.println("Exiting Phishing Analyzer. Goodbye!");
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /*
     * Displays the main menu options to the user.
     * 
     * Effects: Prints the available user actions to the console.
     */
    private void displayMenu() {
        System.out.println("\nPhishing Analyzer Menu:");
        System.out.println("1. Add an Email");
        System.out.println("2. View All Emails");
        System.out.println("3. View All Flagged Emails");
        System.out.println("4. View Summary Report");
        System.out.println("5. Save Application State");
        System.out.println("6. Load Application State");
        System.out.println("7. Exit");
    }
    
    /*
     * Prompts the user to enter details for a new email and adds it to the emails.
     * 
     * Requires: Valid input from the user for sender, subject, body, and URL.
     * Modifies: Adds a new Email to the EmailAnalysis after computing its risk score.
     * Effects: Reads email details from the console and stores the new email for emails.
     */
    private void addEmail() {
        System.out.print("Enter sender id: ");
        String sender = input.nextLine();
        System.out.print("Enter subject: ");
        String subject = input.nextLine();
        System.out.print("Enter body: ");
        String body = input.nextLine();
        System.out.print("Enter URL: ");
        System.out.print("Note - To add nonASCII characters, fn+shift+insert instead of ctrl+v");
        String url = input.nextLine();
        
        Email email = new Email(sender, subject, body, url);
        email.calculateRiskScore();
        emails.addEmail(email);
        System.out.println("Email added successfully!");
    }
    
     /*
     * Displays all emails stored in the emails.
     * 
     * Effects: Retrieves all emails from the EmailAnalysis and prints their summaries to the console.
     */
    private void viewAllEmails() {
        List<Email> allEmails = emails.getAllEmails();
        if (allEmails.isEmpty()) {
            System.out.println("No emails available in the collection.");
            return;
        }

        while (true) {
            System.out.println("\nList of All Emails:");
            for (int i = 0; i < allEmails.size(); i++) {
                System.out.println((i + 1) + ". " + allEmails.get(i).getSubject());
            }
            System.out.print("Enter the number of the email to view its report, or 0 to return to the menu: ");
            int choice = Integer.parseInt(input.nextLine());
            if (choice == 0) {
                break;
            } else if (choice > 0 && choice <= allEmails.size()) {
                viewEmailAndReport(allEmails.get(choice - 1));
            } else {
                System.out.println("Invalid selection. Please try again.");
            }
        }
    }

    /*
     * Displays the phishing analysis report for a specific email.
     * 
     * Requires: A valid email selected by the user and risk score has been calculated.
     * Effects: Retrieves and prints the phishing risk score and major indicator for the selected email.
     */
    private void viewEmailAndReport(Email email) {
        System.out.println("\nEmail:");
        System.out.println("Sender :" + email.getSender());
        System.out.println("Subject :" + email.getSubject());
        System.out.println("Body :" + email.getBody());
        System.out.println("URL :" + email.getUrl());
        System.out.println("\nEmail Report:");
        System.out.println(email.generatePhishingReport());
    }
    
    /*
     * Displays a list of all flagged emails.
     *
     * Effects: Retrieves and prints the list of emails flagged as phishing.
     */
    private void viewFlaggedEmails() {
        List<Email> flaggedEmails = emails.getAllFlaggedEmails();
        if (flaggedEmails.isEmpty()) {
            System.out.println("No flagged emails available.");
            return;
        }
        while (true) {
            System.out.println("\nList of Flagged Emails:");
            for (int i = 0; i < flaggedEmails.size(); i++) {
                System.out.println((i + 1) + ". " + flaggedEmails.get(i).getSubject());
            }
            System.out.print("Enter the number of the email to view its report, or 0 to return to the menu: ");
            int choice = Integer.parseInt(input.nextLine());
            if (choice == 0) {
                break;
            } else if (choice > 0 && choice <= flaggedEmails.size()) {
                viewEmailAndReport(flaggedEmails.get(choice - 1));
            } else {
                System.out.println("Invalid selection. Please try again.");
            }
        }
    }
    
    /*
     * Displays a summary report of phishing indicators for all flagged emails.
     * 
     * Effects:  Retrieves overall summary statistics from EmailAnalysis 
     * and prints them to the console.
     */
    private void viewSummaryReport() {
        System.out.println("\nSummary Report:");
        System.out.println("Most common indicator: ");
        System.out.println(emails.getMostCommonIndicator());
        System.out.println("\nIndicator percentages: ");
        System.out.println(emails.getIndicatorPercentages());
        System.out.println("\nFlagged email percentage: ");
        System.out.println(emails.getFlaggedPercentage());

    }

    /*
     * Saves the current application state to a JSON file.
     * 
     * Effects: Persists the EmailAnalysis state by converting the list of emails to a JSON array
     *          and saving it to file using PersistenceManager.
     */
    private void saveApplicationState() {
        JSONObject state = pm.convertEmailAnalysisToJson(emails);
        pm.saveState(stateFilePath, state);
        System.out.println("Application state saved to " + stateFilePath);
    }
    
    /*
     * Loads the application state from a JSON file.
     * 
     * Requires: The state file exists at the specified relative path.
     * Modifies: Replaces the current EmailAnalysis state with the loaded state.
     * Effects: Loads the application state by reading a JSON file, converting the JSON array of emails
     *          to Email objects, and updating the EmailAnalysis.
     */
    private void loadApplicationState() {
        JSONObject state = pm.loadState(stateFilePath);
        if (state.isEmpty()) {
            System.out.println("No saved state found.");
            return;
        }
        emails = pm.convertJsonToEmailAnalysis(state);
        System.out.println("Application state loaded from " + stateFilePath);
    }
}
