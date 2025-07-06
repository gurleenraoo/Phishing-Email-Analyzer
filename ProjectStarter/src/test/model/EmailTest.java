package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmailTest {
    private Email e1;
    private Email e2;
    private Email e3;
    private Email e4;
    
    /*
     * Initializes Email objects before each test.
     * e1: A safe email with no phishing indicators.
     * e2: An email with phishing keywords and non-ASCII characters.
     * e3: An email with empty subject, body, and URL.
     */

    @BeforeEach
    void runBefore() {
        e1 = new Email("test@example.com", "Test Subject", "Test Body with safe content", "http://test.com");
        e2 = new Email("phisher@example.com", "Urgent : Verify Now", "Please verify your account immediately.", "http://málicious.com");
        e3 = new Email("", "", "", "");
        e4 = new Email("googgle@mail.com", "Limited Offer Today", "It is intentionally made long enough to bypass the body scoring condition. Please verify your account immediately. This is a safe email body that avoids phishing triggers.", "http://málicious.com");
    /*
    extra examples
        e6 = new Email("a@example.com", "", "Please verify now", "http://exámple.com");
        e7 = new Email("b@example.com", "Hello", "", "http://exámple.com");
        e8 = new Email("url@example.com", "Safe Subject", "", "http://exámple.com");
        */
    }

    /*
     * Expected outcomes based on our specification:
     * - e1 (safe email): risk score 0.0.
     * - e2 (phishing keywords & non-ASCII in URL): risk score 75.0.
     * - e3 (empty fields): risk score 35.0.
     */
    @Test
    void testCalculateRiskScore() {
        e1.calculateRiskScore();
        e2.calculateRiskScore();
        e3.calculateRiskScore();
        e4.calculateRiskScore();
        
        assertEquals(25.0, e1.getPhishingRiskScore());
        assertEquals(75.0, e2.getPhishingRiskScore());
        assertEquals(35.0, e3.getPhishingRiskScore());
        assertEquals(50.0, e4.getPhishingRiskScore());
    }

    @Test
    void testGeneratePhishingReport() {
        e1.calculateRiskScore();
        e2.calculateRiskScore();
        e3.calculateRiskScore();
        
        String report1 = e1.generatePhishingReport();
        String report2 = e2.generatePhishingReport();
        String report3 = e3.generatePhishingReport();
        
        assertEquals("Phishing Risk Score: 25.0, Flagged: false, Major Indicator: None - not flagged", report1);
        assertEquals("Phishing Risk Score: 75.0, Flagged: true, Major Indicator: Common Phishing Word in Subject", 
                    report2);
        assertEquals("Phishing Risk Score: 35.0, Flagged: false, Major Indicator: None - not flagged", report3);
    }
    
    @Test
    void testGetSender() {
        assertEquals("test@example.com", e1.getSender());
        assertEquals("phisher@example.com", e2.getSender());
        assertEquals("", e3.getSender());
    }
    
    @Test
    void testGetSubject() {
        assertEquals("Test Subject", e1.getSubject());
        assertEquals("Urgent : Verify Now", e2.getSubject());
        assertEquals("", e3.getSubject());
    }
    
    @Test
    void testGetBody() {
        assertEquals("Test Body with safe content", e1.getBody());
        assertEquals("Please verify your account immediately.", e2.getBody());
        assertEquals("", e3.getBody());
    }
    
    @Test
    void testGetUrl() {
        assertEquals("http://test.com", e1.getUrl());
        assertEquals("http://málicious.com", e2.getUrl());
        assertEquals("", e3.getUrl());
    }
    
    /**
     * Tests the getPhishingRiskScore() method for each Email instance.
     * Initially, the risk score should be 0.0.
     */
    @Test
    void testGetPhishingRiskScore() {
        assertEquals(0.0, e1.getPhishingRiskScore());
        assertEquals(0.0, e2.getPhishingRiskScore());

        e2.calculateRiskScore();
        assertEquals(75.0, e2.getPhishingRiskScore());
        assertEquals(0.0, e3.getPhishingRiskScore());
    }
    
    @Test
    void testIsFlaggedStatus() {
        e1.calculateRiskScore();
        e2.calculateRiskScore();
        e3.calculateRiskScore();
        
        // e1 and e3 safe email should not be flagged.
        assertFalse(e1.isFlagged());
        assertFalse(e3.isFlagged());
        
        // e2 should be flagged due to high risk scores.
        assertTrue(e2.isFlagged());
    }
    
    /*
     * Expected outcomes:
     * - e1: "None"
     * - e2: "Common Phishing Word"
     * - e3: "Body Length"
     */
    @Test
    void testGetMajorIndicator() {
        e1.calculateRiskScore();
        e2.calculateRiskScore();
        e3.calculateRiskScore();
        
        assertEquals("None - not flagged", e1.getMajorIndicator());
        assertEquals("Common Phishing Word in Subject", e2.getMajorIndicator());
        assertEquals("None - not flagged", e3.getMajorIndicator());
    }
    
}