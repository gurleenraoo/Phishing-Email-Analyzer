package persistence;

import model.Email;
import model.EmailAnalysis;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

//import org.json.JSONArray;

/*
 * The PersistenceManager class provides methods for saving and loading the state
 * of the Phishing Email Analyzer to and from a JSON file.
 * 
 * Data files are stored in the "./data" directory relative to the project root.
 * This class uses the provided JSON library (JSONObject and JSONArray) to handle JSON operations.
 */

public class PersistenceManager {

    /*
     * Loads the application state from a JSON file.
     * 
     * filePath - the relative path to the JSON file
     * returns a JSONObject representing the saved state.
     * 
     * Requires: filePath is non-null and the file exists.
     * Effects: Reads the JSON file and parses its content into a JSONObject.
     */

    public JSONObject loadState(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return new JSONObject(content);
        } catch (IOException e) {
            // error handling is done in the caller.
            return new JSONObject(); // Return an empty state on error
        }
    }

    /*
     * Saves the current application state to a JSON file.
     * 
     * filePath - the relative path where the JSON file should be saved.
     * state - the JSONObject representing the current application state.
     * 
     * Requires: filePath and state are non-null.
     * Modifies: Writes to a file.
     * Effects: Serializes the state to a JSON string and writes it to the specified file.
     */

    public void saveState(String filePath, JSONObject state) {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(state.toString(4)); // Pretty print with 4-space indent
            file.flush();
        } catch (IOException e) {
            // error handling is done in the caller
        }
    } 

    /*
     * Modifies: Writes the current state to a file in the ./data directory.
     * Effect: Converts the given EmailAnalysis object to a JSONObject.
     * returns a JSONObject representing the state.
     */
    public JSONObject convertEmailAnalysisToJson(EmailAnalysis analysis) {
        JSONArray emailArray = new JSONArray();
        for (Email email : analysis.getAllEmails()) {
            JSONObject emailJson = new JSONObject();
            emailJson.put("sender", email.getSender());
            emailJson.put("subject", email.getSubject());
            emailJson.put("body", email.getBody());
            emailJson.put("url", email.getUrl());
            emailJson.put("phishingRiskScore", email.getPhishingRiskScore());
            emailJson.put("flagged", email.isFlagged());
            emailJson.put("majorIndicator", email.getMajorIndicator());
            emailArray.put(emailJson);
        }
        JSONObject state = new JSONObject();
        state.put("emails", emailArray);
        return state;
    }

     /*
     * Requires: The state file exists at the specified relative path.
     * Modifies: Replaces the current EmailAnalysis state with the loaded state.
     * Effect: Converts the given JSONObject to an EmailAnalysis object.
     * returns an EmailAnalysis object populated with Email objects.
     */
    public EmailAnalysis convertJsonToEmailAnalysis(JSONObject state) {
        EmailAnalysis analysis = new EmailAnalysis();
        if (state.isEmpty()) {
            return analysis;
        }
        JSONArray emailArray = state.getJSONArray("emails");
        for (int i = 0; i < emailArray.length(); i++) {
            JSONObject emailJson = emailArray.getJSONObject(i);
            Email email = new Email(emailJson.getString("sender"),
                                    emailJson.getString("subject"),
                                    emailJson.getString("body"),
                                    emailJson.getString("url"));
            email.calculateRiskScore();
            analysis.addEmail(email);
        }
        return analysis;
    }

}