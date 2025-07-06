package persistence;

import static org.junit.jupiter.api.Assertions.*;
import model.Email;
import model.EmailAnalysis;
import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/*
 * The PersistenceManagerTest class contains unit tests for the PersistenceManager class.
 * It tests both the loadState and saveState methods using a hand-crafted JSON file
 * located in the ./data directory. 
 * and it also tests the conversion methods:
 *   - convertEmailAnalysisToJson
 *   - convertJsonToEmailAnalysis
 */

public class PersistenceManagerTest {
    private PersistenceManager pm;
    private final String testFilePath = "./data/testPersistence.json";
    
    /*
     * Initializes the test environment by ensuring no test file exists and
     * instantiating a new PersistenceManager.
     * 
     * Requires: The file path is valid.
     * Modifies: Deletes any existing test file.
     * Effects: Ensures a clean state before each test.
     */
    @BeforeEach
    void runBefore() throws Exception {
        Files.deleteIfExists(Paths.get(testFilePath));
        pm = new PersistenceManager();
    }
    
    @Test
    void testSaveState() throws Exception {
        JSONObject state = new JSONObject();
        state.put("key", "value");
        
        pm.saveState(testFilePath, state);
        
        String content = new String(Files.readAllBytes(Paths.get(testFilePath)));
        JSONObject loaded = new JSONObject(content);
        assertEquals("value", loaded.getString("key"));
    }
    
    @Test
    void testLoadState() throws Exception {
        JSONObject state = new JSONObject();
        state.put("sampleKey", "sampleValue");
        try (FileWriter writer = new FileWriter(testFilePath)) {
            writer.write(state.toString());
        }
        
        JSONObject loadedState = pm.loadState(testFilePath);
        assertEquals("sampleValue", loadedState.getString("sampleKey"));
    }

    @Test
    void testLoadStateNoSuchFile() {
        // This file doesn't exist, so loadState should return an empty JSONObject.
        String nonExistentFilePath = "./data/noSuchFile.json";
        JSONObject loadedState = pm.loadState(nonExistentFilePath);
        assertTrue(loadedState.isEmpty(), "Expected an empty JSONObject on error");
    }

    @Test
    void testSaveStateInvalidPath() throws Exception {
        // Attempt to save to a directory that doesn't exist or is invalid.
        String invalidDirFilePath = "./invalidDir/testPersistence.json";
        
        JSONObject state = new JSONObject();
        state.put("key", "value");
        
        pm.saveState(invalidDirFilePath, state);
        
        // Since the directory doesn't exist, we expect no file to be created.
        assertFalse(Files.exists(Paths.get(invalidDirFilePath)),
                "File should not exist after attempting to save to an invalid path");
    }

    @Test
    void testConvertEmailAnalysisToJson() {
        EmailAnalysis analysis = new EmailAnalysis();
        Email email1 = new Email("a@example.com", "Subject A", "Body A", "http://example.com");
        email1.calculateRiskScore();
        Email email2 = new Email("b@example.com", "Urgent: Verify Now", "Body B", "http://málicious.com");
        email2.calculateRiskScore();
        analysis.addEmail(email1);
        analysis.addEmail(email2);
        
        JSONObject state = pm.convertEmailAnalysisToJson(analysis);
        assertTrue(state.has("emails"), "State should have an 'emails' key");
        
        JSONArray emailArray = state.getJSONArray("emails");
        assertEquals(2, emailArray.length(), "There should be 2 emails in the JSON array");
        
        JSONObject emailJson = emailArray.getJSONObject(0);
        assertEquals("a@example.com", emailJson.getString("sender"));
        // Additional checks can be added for subject, body, etc.
    }

    @Test
    @SuppressWarnings("methodlength")
    void testConvertJsonToEmailAnalysis() {
        JSONArray emailArray = new JSONArray();
        
        JSONObject emailJson1 = new JSONObject();
        emailJson1.put("sender", "a@example.com");
        emailJson1.put("subject", "Subject A");
        emailJson1.put("body", "Body A");
        emailJson1.put("url", "http://example.com");
        emailJson1.put("phishingRiskScore", 25.0);
        emailJson1.put("flagged", false);
        emailJson1.put("majorIndicator", "None - not flagged");
        emailArray.put(emailJson1);
        
        JSONObject emailJson2 = new JSONObject();
        emailJson2.put("sender", "b@example.com");
        emailJson2.put("subject", "Urgent: Verify Now");
        emailJson2.put("body", "Body B");
        emailJson2.put("url", "http://málicious.com");
        emailJson2.put("phishingRiskScore", 75.0);
        emailJson2.put("flagged", true);
        emailJson2.put("majorIndicator", "Common Phishing Word in Subject");
        emailArray.put(emailJson2);
        
        JSONObject state = new JSONObject();
        state.put("emails", emailArray);
        
        EmailAnalysis analysis = pm.convertJsonToEmailAnalysis(state);
        assertEquals(2, analysis.getAllEmails().size(), "There should be 2 emails in the analysis");
        
        Email firstEmail = analysis.getAllEmails().get(0);
        assertEquals("a@example.com", firstEmail.getSender());

        Email secondEmail = analysis.getAllEmails().get(1);
        assertEquals("b@example.com", secondEmail.getSender());
    }

    @Test
    void testConvertJsonToEmailAnalysisEmpty() {
        // Create an empty JSONObject
        JSONObject emptyState = new JSONObject();
        
        // Convert the empty state to an EmailAnalysis object
        EmailAnalysis analysis = pm.convertJsonToEmailAnalysis(emptyState);
        
        // Verify that the resulting EmailAnalysis has no emails
        assertTrue(analysis.getAllEmails().isEmpty(), "Expected EmailAnalysis to be empty when JSON state is empty");
    }


}