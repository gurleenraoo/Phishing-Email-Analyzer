package ui;

/*
 * The Main class serves as the entry point for the Phishing Email Analyzer console application.
 * It creates an instance of PhishingAnalyzer and starts the user interface.
 */

public class Main {
    public static void main(String[] args) {
        
        new SplashScreen();
        new PhishingAnalyzerGUI();

        System.out.println("Welcome to the Phishing Email Analyzer Project!");
        new PhishingAnalyzer().start();
    }
}