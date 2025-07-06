# Phishing Email Analyzer

## Proposed Project Idea

My proposed project is a **Phishing Email Analyzer**, a tool designed to help users assess the likelihood of phishing in suspicious emails. The application will allow users to input essential details such as the sender, subject, body, and an URL within the email. The system will then scan for phishing indicators using *three primary detectors*: 
- **Common phishing keyword analysis in the Subject:** Identifies common phishing terms (e.g., "urgent", "verify now", "limited offer"), assigning 10 points if empty and 30 points for identification.
- **URL inspection for non-ASCII characters:** contributing 25 points when character detected.
- **Content-length analysis of the Body:** Evaluates the email body by assigning 15 points if empty and 25 points if shorter than 90 characters.
to identify phishing. Each detector contributes to a combined **phishing risk score**, and emails with scores *greater than or equal to 40%* are flagged and stored for future reference in a collection of emails. The system provides detailed reports that include the overall risk score, flagged status, and the major indicator contributing most significantly to the score. Additionally, summary statistics are presented, showing the distribution of flagged emails by indicator and the overall percentage of emails flagged.

This application is designed for anyone concerned about potential phishing attacks from suspicious emails. It can be used in various contexts, including by companies for *organizational security or by individuals for personal protection*. The tool is particularly helpful when assessing emails from potentially fraudulent accounts, such as those claiming to be from banks, payment services, or other sensitive sources.

This project interests me because it aligns with my passion for cybersecurity and my goal to develop tools that empower users to safeguard themselves against online threats. Phishing remains one of the most common and dangerous forms of cybercrime, and I want to contribute to combating it by **creating an accessible and user-friendly tool**. Through this project, I aim to apply my technical skills to address real-world security challenges, deepen my understanding of cybersecurity threats and defences, and work toward pursuing a career in this field one day.
 
 ## User Stories

- As a user, I want to be able to add an email with the sender ID, subject, body, and URL of an email so that I can assess phishing risks.
- As a user, I want to be able to view the calculated phishing score calculated by the system based on the main three phishing indicators.
- As a user, I want emails with a risk score of 40 or higher to be automatically flagged, and for the system to indicate which phishing indicator contributed the most.
- As a user, I want to view the detailed report for any individual email, including its risk score, flagged status, and major indicator.
- As a user, I want to see a list of all emails and flagged emails, so that I can quickly review potential phishing attempts.
- As a user, I want to see overall summary statistics—such as the most common phishing indicator among flagged emails, the percentage distribution of flagged emails by indicator, and the overall percentage of emails flagged—to help me recognize recurring phishing trends and patterns.
- As a user, I want to have the option to save the entire state of the Phishing Email Analyzer (including all emails and analysis data) to a file, so that I can preserve my data for future sessions.
- As a user, I want to have the option to reload the saved state from a file when starting the application, so that I can resume exactly where I left off.

## Instructions for End User

- You can generate the first required action related to the user story "adding multiple Xs to a Y" by navigating to the **"Add Email"** tab, filling in the sender, subject, body, and URL fields, and then clicking the **"Add Email"** button.
- You can generate the second required action related to the user story "adding multiple Xs to a Y" by navigating to the **"View Flagged Emails"** tab, where a list of flagged emails is displayed; selecting an email from this list and clicking the **"View Email Report"** button will show the email’s detailed report.
- You can locate my visual component by navigating to the **"Summary Report"** tab, where a pie chart (the visual component) displays the distribution of flagged emails by major phishing indicator.
- You can save the state of my application by selecting **"Save State"** from the File menu.
- You can reload the state of my application by selecting **"Load State"** from the File menu.

## Phase 4: Task 2

- Sun Mar 30 17:20:46 PDT 2025 -> Viewed all emails. Total emails: 2
- Sun Mar 30 17:20:48 PDT 2025 -> Viewed all flagged emails. Total flagged emails: 1
- Sun Mar 30 17:20:49 PDT 2025 -> Summary report computed
- Sun Mar 30 17:20:51 PDT 2025 -> Email Added: View Exam Schedule on Workday
- Sun Mar 30 17:21:32 PDT 2025 -> Viewed all emails. Total emails: 3
- Sun Mar 30 17:21:44 PDT 2025 -> Viewed all flagged emails. Total flagged emails: 1
- Sun Mar 30 17:22:33 PDT 2025 -> Email Added: Verify Bank Details now
- Sun Mar 30 17:22:55 PDT 2025 -> Viewed all emails. Total emails: 4
- Sun Mar 30 17:23:24 PDT 2025 -> Viewed all flagged emails. Total flagged emails: 2
- Sun Mar 30 17:24:01 PDT 2025 -> Summary report computed

## Phase 4: Task 3

If I had more time to work on the project, one refactoring I would pursue is to further 
decompose *the PhishingAnalyzerGUI class into smaller*, more focused components. Currently,
the GUI class manages multiple responsibilities such as creating and laying out panels, 
handling user input, and updating model data. By **extracting the different tabs** (e.g., 
"Add Email", "View All Emails", "View Flagged Emails", and "Summary Report") into **separate**
 **classes or components**, I could adhere more closely to **the Single Responsibility Principle.**
 This change would simplify each component, make the codebase easier to understand and 
 maintain, and facilitate more targeted testing of individual parts of the UI.


