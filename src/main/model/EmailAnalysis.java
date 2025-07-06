package model;

import java.util.ArrayList;
import java.util.List;

/*
 * The EmailAnalysis class manages a collection of Email objects.
 * It provides methods to add emails, retrieve all emails, retrieve all flagged emails,
 * and compute summary statistics including:
 *   1. The most common major phishing indicator among flagged emails.
 *   2. The percentage distribution of flagged emails by each major indicator.
 *   3. The percentage of emails that are flagged as phishing.
 */

public class EmailAnalysis {
    
    private List<Email> emails;
    
    /*
     * Constructs an EmailAnalysis object.
     * 
     * Modifies: Initializes the internal list of emails.
     * Effects: Creates an empty EmailAnalysis instance.
     */

    public EmailAnalysis() {
        emails = new ArrayList<>();
    }
    
    /**
     * Adds an Email to the analysis.
     * 
     *  email the Email object to be added.
     * 
     * Requires: email is non-null.
     * Modifies: Adds the email to the internal list.
     * Effects: The email is stored for subsequent analysis.
     */
    public void addEmail(Email email) {
        //need a method to no add Null 
        emails.add(email);

        EventLog.getInstance().logEvent(new Event("Email added: " + email.getSubject()));
    }
    
    /*
     * Returns a list of all Email objects in the analysis.
     * 
     * Effects: Returns a copy of the internal list of emails.
     */
    public List<Email> getAllEmails() {
        List<Email> allEmails = new ArrayList<>(emails);
        EventLog.getInstance().logEvent(new Event("Viewed all emails. Total emails: " + allEmails.size()));
        return allEmails;
    }
    
    /*
     * Returns a list of all flagged Email objects in the analysis.
     * 
     * Effects: Iterates over the internal list and collects flagged emails.
     */
    public List<Email> getAllFlaggedEmails() {
        List<Email> flaggedEmails = new ArrayList<>();
        for (Email email : emails) {
            if (email.isFlagged()) {
                flaggedEmails.add(email);
            }
        }
        
        String text = "Viewed all flagged emails. Total flagged emails: ";
        EventLog.getInstance().logEvent(new Event(text+flaggedEmails.size()));
        return flaggedEmails;
    }
    
    /*
     *  computes counts for each major indicator among flagged emails.
     * 
     * return an int array of three elements where:
     *         [0] is the count for "Common Phishing Word in Subject",
     *         [1] is the count for "Body Length",
     *         [2] is the count for "Non-ASCII Character Identified in URL".
     * 
     * Effects: Iterates over flagged emails using simple counters.
     */
    private int[] computeIndicatorCounts() {
        int countCommon = 0;
        int countBody = 0;
        int countNonAscii = 0;
        
        List<Email> flaggedEmails = getAllFlaggedEmails();
        for (Email email : flaggedEmails) {
            String indicator = email.getMajorIndicator();
            if ("Common Phishing Word in Subject".equals(indicator)) {
                countCommon++;
            } else if ("Body Length".equals(indicator)) {
                countBody++;
            } else if ("Non-ASCII Character Identified in URL".equals(indicator)) {
                countNonAscii++;
            }
        }
        return new int[] {countCommon, countBody, countNonAscii};
    }

    /*
     * Determines and returns the most common major phishing indicator among flagged emails.
     * 
     * return a String representing the most frequent major indicator,
     *         or "None" if there are no flagged emails.
     * 
     * Requires: The internal list of emails may be empty.
     * Effects: Uses computeIndicatorCounts() to determine the most common indicator.
     */
    public String getMostCommonIndicator() {
        List<Email> flaggedEmails = getAllFlaggedEmails();
        if (flaggedEmails.isEmpty()) {
            return "None";
        }
        
        int[] counts = computeIndicatorCounts();
        int countCommon = counts[0];
        int countBody = counts[1];
        int countNonAscii = counts[2];
        
        if (countCommon >= countBody && countCommon >= countNonAscii) {
            return "Common Phishing Word in Subject";
        } else if (countBody >= countCommon && countBody >= countNonAscii) {
            return "Body Length";
        } else {
            return "Non-ASCII Character Identified in URL";
        }
    }
    
    /*
     * Calculates and returns the percentage distribution of flagged emails by major indicator.
     * 
     * return a String summarizing the percentage of flagged emails for each major indicator.
     * 
     * Effects: Uses computeIndicatorCounts() and arithmetic to calculate percentages.
     */
    public String getIndicatorPercentages() {
        List<Email> flaggedEmails = getAllFlaggedEmails();
        int totalFlagged = flaggedEmails.size();
        if (totalFlagged == 0) {
            return "No flagged emails.";
        }
        
        int[] counts = computeIndicatorCounts();
        int countCommon = counts[0];
        int countBody = counts[1];
        int countNonAscii = counts[2];
        
        double percentCommon = (countCommon * 100.0) / totalFlagged;
        double percentBody = (countBody * 100.0) / totalFlagged;
        double percentNonAscii = (countNonAscii * 100.0) / totalFlagged;
        
        return "Common Phishing Word in Subject: " + percentCommon + "%, " + "Body Length: " 
            + percentBody + "%, " + "Non-ASCII Character Identified in URL: " + percentNonAscii + "%";
    }
    
    /*
     * Calculates and returns a formatted text string representing the percentage of
     *  flagged emails among all emails in the form "<percentage>% of the emails are flagged."
     * 
     * Effects: Iterates over all emails to count flagged emails and calculates the percentage.
     */
    public String getFlaggedPercentage() {
        if (emails.isEmpty()) {
            return "0.0% of the emails are flagged.";
        }
        int flaggedCount = 0;
        for (Email email : emails) {
            if (email.isFlagged()) {
                flaggedCount++;
            }
        }

        // Ensure floating-point division by casting emails.size() or flaggedCount to double
        double fraction = (double) flaggedCount / emails.size(); 
        double percentage = fraction * 100.0;
        // Format to one decimal place, example from tests, 33.3% instead of 33.3333333...%

        EventLog.getInstance().logEvent(new Event("Summary report computed"));
        return String.format("%.1f%% of the emails are flagged.", percentage);
    }
}