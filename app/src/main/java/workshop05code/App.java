package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            logger.log(Level.INFO,"Wordle created and connected.");
        } else {
            logger.log(Level.WARNING,"Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            logger.log(Level.INFO,"Wordle structures in place.");
        } else {
            logger.log(Level.WARNING,"Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                logger.log(Level.INFO,"Valid words: " + line);
                wordleDatabaseConnection.addValidWord(i, line);
                i++;
            }

        } catch (IOException e) {
            System.out.println("Not able to load . Sorry!");
            System.out.println(e.getMessage());
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            logger.log(Level.INFO,"The 'Guessing' program runs"); //logger if program runs
            String guess = scanner.nextLine();

            while (!guess.equals("q")) {
                System.out.println("You've guessed '" + guess+"'.");
                logger.log(Level.INFO,"User has provided input");

                if(!guess.matches("[a-z]+") || !(guess.length() == 4)){
                    System.out.println("This is not acceptible input, please try again.\n");
                    logger.log(Level.SEVERE,"User input is out of scope, guessed '" + guess + "'.");

                } else {

                    if (wordleDatabaseConnection.isValidWord(guess)) { 
                        System.out.println("Success! It is in the the list.\n");
                        logger.log(Level.INFO,"Word was present in the list, provided appropriate feedback");
                    }else{
                        System.out.println("Sorry. This word is NOT in the the list.\n");
                        logger.log(Level.WARNING,"User entered '" + guess + "' which is not present in the list, returned appropriate response.");
                    }

                }

                System.out.print("Enter a 4 letter word for a guess or q to quit: " );
                guess = scanner.nextLine();                
                
            }
            logger.log(Level.INFO,"User has quit the program");
            
        } catch (NoSuchElementException | IllegalStateException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "Your message.", e); 
            System.out.println("Please check your input values");
        }

    }
}