package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class EmailAnalysisTest {
    private EmailAnalysis analysis;
    private EmailAnalysis emptyListofEmails;
    private Email e1; // risk score 0.0, flagged false, main indicator "None - not flagged"
    private Email e2; // risk score 75.0, flagged true, indicator "Common Phishing Word in Subject"
    private Email e3; // risk score 35.0, flagged false, main indicator "None - not flagged"
    private Email e4; // risk score 55.0, flagged true, indicator "Body Length"
    private Email e5;

    /**
     * Initializes EmailAnalysis and Email objects before each test.
     * e1: Safe email with no phishing indicators.
     * e2: Email with phishing keywords and non-ASCII characters in URL.
     * e3: Email with empty subject, body, and URL.
     * e4: Email with empty subject and body but non-ASCII character in URL.
     */
    @BeforeEach
    void runBefore() {
        analysis = new EmailAnalysis();
        emptyListofEmails = new EmailAnalysis();
        
        e1 = new Email("test@example.com", "Test Subject", "Test Body with safe content", "http://test.com");
        e2 = new Email("phisher@example.com", "Urgent: Verify Now", "Please verify your account immediately.", "http://málicious.com");
        e3 = new Email("empty@example.com", "", "", "");
        e4 = new Email("another@example.com", "", "", "http://exámple.com");
        e5 = new Email("","","","");
        
        e1.calculateRiskScore(); // Expected risk score: 0.0
        e2.calculateRiskScore(); // Expected risk score: 75.0, flagged true, indicator "Common Phishing Word in Subject"
        e3.calculateRiskScore(); // Expected risk score: 35.0 (10 + 25 + 0), not flagged
        e4.calculateRiskScore(); // Expected risk score: 55.0 (10 + 25 + 20), flagged true, indicator "Body Length"
        
        // Adding 3 emails to the analysis.
        analysis.addEmail(e1);
        analysis.addEmail(e2);
        analysis.addEmail(e3);
        analysis.addEmail(e5);
    }
    
    @Test
    void testAddEmails() {
        List<Email> allEmails = analysis.getAllEmails();
        assertEquals(4, allEmails.size());
        analysis.addEmail(e4);
        allEmails = analysis.getAllEmails();
        assertEquals(5, allEmails.size());
        assertTrue(allEmails.contains(e4));
    }

    @Test
    void testGetAllEmails() {
        List<Email> allEmails = analysis.getAllEmails();
        assertTrue(allEmails.contains(e1));
        assertTrue(allEmails.contains(e2));
        assertTrue(allEmails.contains(e3));
    }
    
    @Test
    void testGetAllFlaggedEmails() {
        // Initially, only e2 is flagged.
        List<Email> flaggedEmails = analysis.getAllFlaggedEmails();
        assertEquals(1, flaggedEmails.size());
        assertTrue(flaggedEmails.contains(e2));
        assertFalse(flaggedEmails.contains(e4));
        // Now add e4, which is flagged.
        analysis.addEmail(e4);
        flaggedEmails = analysis.getAllFlaggedEmails(); //re-get list after addition
        assertEquals(2, flaggedEmails.size());
        assertTrue(flaggedEmails.contains(e2));
        assertTrue(flaggedEmails.contains(e4));
    }
    
    /*
     * Tests that getMostCommonIndicator returns the most frequent major indicator among flagged emails.
     * Initially, only e2 is flagged, so expected result is "Common Phishing Word in Subject".
     * After adding e4, with e2 indicator "Common Phishing Word in Subject" and e4 "Body Length",
     * in case of a tie the method returns "Common Phishing Word in Subject".
     */
    @Test
    void testGetMostCommonIndicator() {
        String empty = emptyListofEmails.getMostCommonIndicator();
        assertEquals("None", empty);

        String mostCommon = analysis.getMostCommonIndicator();
        assertEquals("Common Phishing Word in Subject", mostCommon);
        analysis.addEmail(e4);
        mostCommon = analysis.getMostCommonIndicator();
        assertEquals("Common Phishing Word in Subject", mostCommon);
    }
    
    /*
     * Tests that getIndicatorPercentages returns the correct percentage distribution among flagged emails.
     * With 2 flagged emails (e2 and e4): expect 50% for "Common Phishing Word in Subject",
     * 50% for "Body Length", and 0% for "Non-ASCII Character Identified in URL".
     */
    @Test
    void testGetIndicatorPercentages() {
        String empty = emptyListofEmails.getIndicatorPercentages();
        assertEquals(empty, "No flagged emails.");

        String percentages = analysis.getIndicatorPercentages();
        assertTrue(percentages.contains("Common Phishing Word in Subject: 100.0%"));
        assertTrue(percentages.contains("Body Length: 0.0%"));
        assertTrue(percentages.contains("Non-ASCII Character Identified in URL: 0.0%"));

        analysis.addEmail(e4);
        percentages = analysis.getIndicatorPercentages();
        assertTrue(percentages.contains("Common Phishing Word in Subject: 50.0%"),
                "After adding e4, expected 50% for Common Phishing Word in Subject");
        assertTrue(percentages.contains("Body Length: 50.0%"), "After adding e4, expected 50% for Body Length");
        assertTrue(percentages.contains("Non-ASCII Character Identified in URL: 0.0%"));
    }
    
    @Test
    void testGetFlaggedPercentage() {
        String empty = emptyListofEmails.getFlaggedPercentage();
        //0 emails, so 0.0%
        assertEquals(empty,"0.0% of the emails are flagged.");

        String flaggedPercentageStr = analysis.getFlaggedPercentage();
        //4 emails with 1 flagged, so 25.0% 
        assertEquals(flaggedPercentageStr,"25.0% of the emails are flagged.");
        
        // Add e4, then total emails = 5, flagged = 2, so flagged percentage should be 40.0%
        analysis.addEmail(e4);
        flaggedPercentageStr = analysis.getFlaggedPercentage();
        assertEquals(flaggedPercentageStr,"40.0% of the emails are flagged.");
    }
}