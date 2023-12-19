import java.util.HashMap;
import java.util.Map;

/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean dugForGold;
    private String treasure;
    private boolean lookedForTreasure;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;

        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);

        int treasureId = (int) (Math.random() * 4);
        if (treasureId == 0) {
            treasure = "crown";
        }
        if (treasureId == 1) {
                treasure = "trophy";
        }
        if (treasureId == 2) {
                treasure = "gem";
        }
        if (treasureId == 3) {
                treasure = "dust";
        }
    }

    public String getLatestNews() {
        return printMessage;
    }

    public void setLatestNews(String newNews) {
        printMessage = newNews;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";

        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item;
            }

            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        shop.enter(hunter, choice);
    }

    public void lookForTreasure() {
        if (lookedForTreasure) {
            printMessage = "You already searched for treasure, get the hell out";
            return;
        }
        lookedForTreasure = true;
        printMessage = "You found " + treasure;
        if (treasure.equals("dust")) {
            return;
        }
        if (hunter.hasTreasureInInventory(treasure)) {
            printMessage = "Unfortunately you already have one of these. Try your luck in the next town!";
            return;
        }
        hunter.addTreasure(treasure);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }

        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n" + Colors.RESET;
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (Math.random() > noTroubleChance) {
                printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                printMessage += "\nYou won the brawl and receive " + goldDiff + " gold.";
                hunter.changeGold(goldDiff);
            } else {
                printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold.";
                hunter.changeGold(-goldDiff);
            }
        }
    }

    public void lookForGold() {
        if (dugForGold) {
            printMessage = "Already dug here, get the hell out.";
            return;
        }
        if (hunter.hasItemInKit("shovel")) {
            double rand = Math.random();
            if (rand > 0.5) {
                int goldReward = ((int) (Math.random() * 20)) + 1;
                hunter.changeGold(goldReward);
                printMessage = String.format("You dug up %d gold!", goldReward);
            } else {
                printMessage = "You dug but only found dirt";
            }
            dugForGold = true;
        } else {
            printMessage = "You can't dig for gold without a shovel!";
        }
    }

    public String toString() {
        return "This nice little town is surrounded by " + Colors.PURPLE + terrain.getTerrainName() + Colors.RESET + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        HashMap<String, String> terrains = new HashMap<>();
        terrains.put("Mountains", "Rope");
        terrains.put("Ocean", "Boat");
        terrains.put("Plains", "Horse");
        terrains.put("Desert", "Water");
        terrains.put("Jungle", "Machete");
        terrains.put("Marsh", "Boots");

        int rnd = (int) (Math.random() * terrains.size());
        int currentIndex = 0;

        for (Map.Entry<String, String> entry : terrains.entrySet()) {
            if (currentIndex == rnd) {
                return new Terrain(entry.getKey(), entry.getValue());
            }
            currentIndex++;
        }

        return new Terrain("FHWAUIDHUIWHUIDUIAWDHAWUIHDI", "WGADGAWYDGUUAWGDUWGDAWGDU");
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }
}