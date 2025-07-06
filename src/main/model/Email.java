package model;

/*
 * The Email class represents an individual email for phishing analysis.
 * It stores email details such as sender id, subject, body, URL, a calculated phishing risk score,
 * a flagged status (indicating whether the email is flagged as phishing), and the primary phishing indicator.
 * <p>
 * The phishing risk score is computed based on the email content and may result in flagging the email.
 * The mainIndicator field can take one of the following values:
 * "None" for clean emails,
 * "Common Phishing Word" if flagged due to phishing keywords,
 * "Body Length" if flagged due to suspicious email body length,
 * "Non-ASCII Character Identified" if flagged due to non-ASCII characters in sender id or URL.
 */

public class Email {
    private String sender;
    private String subject;
    private String body;
    private String url;
    private double phishingRiskScore;
    private boolean flagged;
    private String mainIndicator;

    /*
     * Constructs an Email object with the specified details.
     *
     * sender  the sender id of the email
     * subject the email subject
     * body    the email body content
     * url     the URL contained in the email
     *
     * Requires: sender, subject, body, and url are non-null.
     * Modifies: Initializes the Email object's fields.
     * Effects: Creates an Email object with the provided details, an initial phishing risk score of 0.0,
     *          flagged status set to false, and mainIndicator set to "None".
     */
    public Email(String sender, String subject, String body, String url) {
        this.sender = sender;
        this.subject = subject;
        this.body = body;
        this.url = url;
        this.phishingRiskScore = 0.0;
        this.flagged = false;
        this.mainIndicator = "None";
    }

    /**
     * Calculates the phishing risk score based on email content, keywords, and other factors.
     *
     * Requires: Email properties (sender, subject, body, url) are set.
     * Modifies: Updates the phishingRiskScore, flagged, and mainIndicator fields.
     * Effects: Analyzes the email for phishing indicators and computes the risk score.
     *          If the risk score exceeds a certain threshold (>=40), sets flagged to true.
     *          Determines the primary phishing indicator among "Common Phishing Word", "Body Length",
     *          "Non-ASCII Character Identified", or "None" for clean emails.
     * 
     *          Subject: empty = 10 points 
     *                   common phishing word identifies = 30 points 
     *          Body : empty = 15 points 
     *                 body length<90 char = 25 points 
     * *        URL : empty = 0 points 
     *                nonAscii char  = 25 points 
     */

    @SuppressWarnings("methodlength") //not a misuse, only used once
    public void calculateRiskScore() {
        double subjectScore = 0.0;
        double bodyScore = 0.0;
        double urlScore = 0.0;
        
        //subjectScore
        if (subject.isEmpty()) {
            subjectScore = 10.0;
        } else {
            String lowerSubject = subject.toLowerCase();
            if (lowerSubject.contains("urgent") || lowerSubject.contains("verify now") 
                    || lowerSubject.contains("limited offer")) {
                subjectScore = 30.0;
            }
        }

        //bodyScore:
        if (body.isEmpty()) {
            bodyScore = 25.0;
        } else if (body.length() < 90) {
            bodyScore = 25.0;
        }
        
        //urlScore
        if (url.isEmpty()) {
            urlScore = 0.0;
        } else {
            boolean nonAsciiFound = false; 
            for (int i = 0; i < url.length(); i++) {
                if (url.charAt(i) > 127) {   //the character value over 127 is non-ASCII
                    nonAsciiFound = true;
                    break;
                }
            }
            if (nonAsciiFound) {
                urlScore = 20.0;
            }
        }
        
        //modifying the risk score and flagged or not
        phishingRiskScore = subjectScore + bodyScore + urlScore;
        flagged = phishingRiskScore >= 40.0;
        
        //determining the mainIndicator
        if (!flagged) {
            mainIndicator = "None - not flagged";
        } else if (subjectScore >= bodyScore && subjectScore >= urlScore && subjectScore > 0) {
            mainIndicator = "Common Phishing Word in Subject";
        } else if (bodyScore >= subjectScore && bodyScore >= urlScore && bodyScore > 0) {
            mainIndicator = "Body Length";
        } else if (urlScore > 0) {
            mainIndicator = "Non-ASCII Character Identified in URL";
        }
    }


    /*
     * Generates a detailed phishing report based on the analysis performed.
     *
     * Requires: calculateRiskScore() has been called and phishingRiskScore is computed.
     * Effects: Returns a String summarizing the phishing indicators, overall risk score, flagged status,
     *          and the primary phishing indicator.
     *
     * returns a String report detailing the phishing analysis.
     */
    public String generatePhishingReport() {
        return "Phishing Risk Score: " + phishingRiskScore 
                + ", Flagged: " + flagged 
                + ", Major Indicator: " + mainIndicator;
    }

    /**
     * Returns the sender id of the email.
     *
     * Effects: Returns the sender id value.
     *
     */
    public String getSender() {
        return sender;
    }

    /**
     * Returns the subject of the email.
     *
     * Effects: Returns the subject value.
     *
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Returns the body content of the email.
     *
     * Effects: Returns the body content.
     *
     */
    public String getBody() {
        return body;
    }

    /**
     * Returns the URL present in the email.
     *
     * Effects: Returns the URL value.
     *
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the current phishing risk score of the email.
     *
     * Effects: Returns the phishingRiskScore value.
     *
     */
    public double getPhishingRiskScore() {
        return phishingRiskScore;
    }

    /**
     * Returns the flagged status of the email.
     *
     * Requires: calculateRiskScore() has been called if updated status is desired.
     * Effects: Returns true if the email is flagged as phishing, false otherwise.
     *
     */
    public boolean isFlagged() {
        return flagged;
    }

    /**
     * Returns the primary phishing indicator that contributed most significantly to the risk score.
     *
     * Requires: calculateRiskScore() has been called.
     * Effects: Determines and returns a String representing the primary phishing indicator.
     *          Possible return values are "None", "Common Phishing Word", "Body Length",
     *          or "Non-ASCII Character Identified".
     *
     */
    public String getMajorIndicator() {
        return mainIndicator;
    }
}

