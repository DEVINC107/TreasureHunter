import java.util.Scanner;

/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all the display based on the messages it receives from the Town object. <p>
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class TreasureHunter {
    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private Town currentTown;
    private Hunter hunter;
    private String difficulty; // "e / n / h"
    private boolean testMode;

    /**
     * Constructs the Treasure Hunter game.
     */
    public TreasureHunter() {
        // these will be initialized in the play method
        currentTown = null;
        hunter = null;
        difficulty = "n";
    }

    /**
     * Starts the game; this is the only public method
     */
    public void play() {
        welcomePlayer();
        enterTown();
        showMenu();
    }

    /**
     * Creates a hunter object at the beginning of the game and populates the class member variable with it.
     */
    private void welcomePlayer() {
        Window.window.addTextToWindow("\nWelcome to " + Colors.CYAN + "TREASURE HUNTER" + Colors.RESET + "!");
        Window.window.addTextToWindow("\nGoing hunting for the big treasure, eh?");
        Window.window.addTextToWindow("\nWhat's your name, Hunter? ");
        String name = SCANNER.nextLine().toLowerCase();

        Window.window.addTextToWindow("\nEasy, Normal, or Hard? (e/n/h): ");
        String difficultyChoice = SCANNER.nextLine().toLowerCase();
        if (difficultyChoice.equals("e") || difficultyChoice.equals("n") || difficultyChoice.equals("h") || difficultyChoice.equals("s")) {
            difficulty = difficultyChoice;
        } else if (difficultyChoice.equals("test")) {
            testMode = true;
        }

        // set hunter instance variable
        int startingGold = testMode ? 100 : 10;
        if (difficulty.equals("e")) {
            startingGold *= 2;
        }
        hunter = new Hunter(name, startingGold, difficulty);
        if (testMode) {
            hunter.addItem("water");
            hunter.addItem("rope");
            hunter.addItem("machete");
            hunter.addItem("horse");
            hunter.addItem("boat");
            hunter.addItem("boots");
        }
    }

    /**
     * Creates a new town and adds the Hunter to it.
     */
    private void enterTown() {
        double markdown = 0.5;
        double toughness = 0.4;
        if (difficulty.equals("e")) {
            markdown = 0.75;
            toughness = 0.2;
        } else if (difficulty.equals("h")) {
            // in hard mode, you get less money back when you sell items
            markdown = 0.25;

            // and the town is "tougher"
            toughness = 0.75;
        }

        // note that we don't need to access the Shop object
        // outside of this method, so it isn't necessary to store it as an instance
        // variable; we can leave it as a local variable
        Shop shop = new Shop(markdown);

        // creating the new Town -- which we need to store as an instance
        // variable in this class, since we need to access the Town
        // object in other methods of this class
        currentTown = new Town(shop, toughness);

        shop.setTown(currentTown);

        // calling the hunterArrives method, which takes the Hunter
        // as a parameter; note this also could have been done in the
        // constructor for Town, but this illustrates another way to associate
        // an object with an object of a different class
        currentTown.hunterArrives(hunter);
    }

    /**
     * Displays the menu and receives the choice from the user.<p>
     * The choice is sent to the processChoice() method for parsing.<p>
     * This method will loop until the user chooses to exit.
     */
    private void showMenu() {
        String choice = "";

        while (!choice.equals("x") && hunter.getGold() > 0 && !hunter.checkForWin()) {
            Window.window.clear();
            Window.window.addTextToWindow("\n");
            Window.window.addTextToWindow(currentTown.getLatestNews());
            Window.window.addTextToWindow("\n***");
            Window.window.addTextToWindow(hunter.toString());
            Window.window.addTextToWindow(currentTown.toString());
            Window.window.addTextToWindow("\n(B)uy something at the shop.");
            Window.window.addTextToWindow("\n(S)ell something at the shop.");
            Window.window.addTextToWindow("\n(M)ove on to a different town.");
            Window.window.addTextToWindow("\n(L)ook for trouble!");
            Window.window.addTextToWindow("\n(D)ig for treasure!");
            Window.window.addTextToWindow("\n(H)unt for treasure!");
            Window.window.addTextToWindow("\nGive up the hunt and e(X)it.");
            Window.window.addTextToWindow("\n");
            Window.window.addTextToWindow("\nWhat's your next move? ");
            choice = SCANNER.nextLine().toLowerCase();
            processChoice(choice);
        }
        if (hunter.getGold() <= 0) {
            Window.window.addTextToWindow(currentTown.getLatestNews());
            Window.window.addTextToWindow("\nGame over");
        }
    }


    /**
     * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
     * @param choice The action to process.
     */
    private void processChoice(String choice) {
        if (choice.equals("b") || choice.equals("s")) {
            currentTown.enterShop(choice, difficulty);
        } else if (choice.equals("m")) {
            if (currentTown.leaveTown()) {
                // This town is going away so print its news ahead of time.
                Window.window.addTextToWindow(currentTown.getLatestNews());
                enterTown();
            }
        } else if (choice.equals("l")) {
            currentTown.lookForTrouble();
        } else if (choice.equals("x")) {
            Window.window.addTextToWindow("\nFare thee well, " + hunter.getHunterName() + "!");
        } else if (choice.equals("d")) {
            currentTown.lookForGold();
        } else if (choice.equals("h")) {
            currentTown.lookForTreasure();
        } else {
            Window.window.addTextToWindow("\nYikes! That's an invalid option! Try again.");
        }
    }
}