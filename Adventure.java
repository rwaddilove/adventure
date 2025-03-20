// modify troll fight to add armour, shield

// Great Adventure!
// A bit like Zork (en.wikipedia.org/wiki/Zork) and Colossal Cave.
// By Roland Waddilove (github.com/rwaddilove/) as an exercise while
// learning Java. It's probably bad code, but it's just for fun.

import java.util.Random;
import java.util.Scanner;

class Input {
    public static String inputStr(String prompt) {
        System.out.print(prompt);
        Scanner input = new Scanner(System.in);
        return input.nextLine().strip().toLowerCase();
    }
}


class Initialise {

    public static void SetUp(int[][] itemLoc, int maprows, int mapcols, Player[] trolls, Player me) {
        Random random = new Random();
        // set up some stuff for the start of the game
        for (int[] i : itemLoc) {                   // random object locations
            i[0] = random.nextInt(maprows);
            i[1] = random.nextInt(mapcols); }
        itemLoc[6][0] = 2;      // DELETE - SETS SILVER AND GOLD HERE
        itemLoc[6][1] = 2;
        itemLoc[8][0] = 2;
        itemLoc[8][1] = 2;
        for (int i = 0; i < trolls.length; ++i) {   // random troll locations
            trolls[i] = new Player();
            do {
                trolls[i].row = random.nextInt(maprows);
                trolls[i].col = random.nextInt(mapcols);
            } while (trolls[i].row == me.row && trolls[i].col == me.col);
        }
    }

    public static void Help(Player me, String[][] placeName) {
        System.out.println("\n---------- GREAT ADVENTURE! ----------");
        System.out.println("You are currently in " + placeName[me.row][me.col]);
        System.out.println("Explore, find objects, and kill bad guys!\n");
        System.out.println("Commands: help, eat, status, search, look, quit,\nget [object], use [object], go [NESW].");
    }
}


class Player {
    int health = 50;
    int maxHealth = 100;
    int carrying = 0;           // number of items carried
    int maxCarrying = 5;
    int money = 100;
    int row = 2;                // location in map[][]
    int col = 2;
    int attack = 10;            // fists (dagger=20/axe=30/sword=40)
    int maxAttack = 20;         // used with trolls
    String weapon = "fists";    // no weapon
    int armour = 0;             // troll attack reduced by this amount
    int shield = 0;             // troll attack reduced by this amount

    public static void Troll(Player me, Player[] trolls, String[][] map) {
        Random random = new Random();
        for (Player troll : trolls) {                           // for each troll
            if (troll.row == me.row && troll.col == me.col && troll.health >=0) {  // if at my location
                System.out.println("\nThere is an angry troll here!");
                System.out.println("Your health is " + me.health+". The troll's health is " + troll.health);
                if (me.weapon.startsWith("fist"))
                    System.out.println("You have no weapon. Beating a troll with\nyour fists is hard, but not impossible.");
                else
                    System.out.println("Your weapon: " + me.weapon + " causing damage up to " + me.attack + "\n");
                if (me.armour > 0) System.out.println("You are wearing amour for protection.");
                if (me.shield > 0) System.out.println("You have a shield for protection.");
                if (TrollBattle(me, troll)) {   // true if you run
                    System.out.println("In a blind panic to escape, you run\nand run, but now you are lost...");
                    me.row = random.nextInt(0, map.length);
                    me.col = random.nextInt(0,map[0].length);
                    return; }           // it's over if you run
                if (me.health < 0) return;      // you died!
            }
        }
    }

    public static boolean TrollBattle(Player me, Player troll) {  // false=battle, true=run
        Random random = new Random();

        // troll battle loop
        while (me.health >= 0 && troll.health >=0 ) {
            String inp="";
            while (!inp.equals("r") && !inp.equals("f")) {
                inp = Input.inputStr("(F)ight or (R)un? "); }
            if (inp.startsWith("r")) return true;      // true if you run

            // you attack troll
            int attack = random.nextInt(me.attack / 2, me.attack);
            troll.health -= attack;
            System.out.println("You attack the troll with your "+me.weapon+"\ncausing "+attack+" damage to it.");
            if (troll.health < 0) break;

            // troll attacks you
            attack = random.nextInt((troll.maxAttack - me.armour - me.shield));    // troll attacks
            me.health -= attack;
            System.out.println("The troll attacks, does " + attack + " damage to you.");
        }
        if (me.health < 0)      // either you or the troll is dead
            System.out.println("\nAgghh! You died! The troll killed you!");
        else
            System.out.println("\nYes! You killed the troll!\n");
        return false;   // had a battle
    }
}


class MapLoc {

    public static void Look(Player me, String[][] map, String[][] placeName, Player[] trolls, int[][] itemLoc, String[] items) {
        System.out.println("Location: " + placeName[me.row][me.col] + " (" + me.row + "," + me.col + ")");  // DELETE (me.row,me.col)
        if ((me.col == 1 && me.row == 2) || (me.col == 3 && me.row == 3)) {     // foggy locations
            System.out.println("You can't see because of thick fog.");
            return;
        }
        for (Player troll : trolls)      // for each troll
            if (troll.row == me.row && troll.col == me.col && troll.health >= 0) {  // troll here?
                System.out.println("Can't see! A huge troll blocks your view.");
                return;
            }

        ShowExits(map[me.row][me.col]);

        // enter market to buy stuff?
        if (me.row == 2 && me.col == 2) {
            String enter = Input.inputStr("You see the village market. Enter it (y/n)? ");
            if (enter.equals("y")) Market(me, items, itemLoc);
        }
    }

    public static void ShowExits(String exits) {
        System.out.print("Paths lead: ");       // eg. 10101010 = NW, NE, E, S
        if (exits.charAt(0) == '1') System.out.print("NW ");
        if (exits.charAt(1) == '1') System.out.print("N ");
        if (exits.charAt(2) == '1') System.out.print("NE ");
        if (exits.charAt(3) == '1') System.out.print("W ");
        if (exits.charAt(4) == '1') System.out.print("E ");
        if (exits.charAt(5) == '1') System.out.print("SW ");
        if (exits.charAt(6) == '1') System.out.print("S ");
        if (exits.charAt(7) == '1') System.out.print("SE ");
        System.out.println();
    }

    public static void Market(Player me, String[] items, int[][] itemLoc) {
        int food = 20;      // cost of buying items
        int armour = 30;
        int shield = 40;
        System.out.println("\nYou head for the market...");
        SellGoldSilver(me, items, itemLoc);     // need more money?
        while (true) {
            System.out.println("\nYou are in the market. You have $" + me.money);
            System.out.println("You see for sale: food $"+food+", armour $"+armour+", shield $"+shield);
            String inp = Input.inputStr("Buy item or Leave? ");
            if (inp.equals("leave") || inp.equals("l")) break;
            String[] words = inp.split(" ");       // is item included? eg. 'buy shield'
            String item = (words.length > 1) ? words[1] : Input.inputStr("Buy what? ");
            if (!item.isBlank()) BuyItem(item, me, food, armour, shield);
            Input.inputStr("Press Enter...");
        }
    }

    public static void BuyItem(String item, Player me, int food, int armour, int shield) {
        if (item.equals("food")) {
            if (me.money < food) {
                System.out.println("You don't have enough money to buy food.");
                return;
            }
            System.out.println("You buy food, eat it, and boost your health.");
            me.health += 50;
            me.money -= food;
        }
        if (item.equals("armour") && me.armour > 0) {
            System.out.println("You are already wearing armour.");
            return;
        }
        if (item.equals("armour")) {
            if (me.money < armour) {
                System.out.println("You don't have enough money for armour.");
                return;
            }
            System.out.println("You wear the amour, increasing your defense.");
            me.armour = 5;
            me.money -= armour;
        }
        if (item.equals("shield") && me.shield > 0) {
            System.out.println("You already have a shield to protect you.");
            return;
        }
        if (item.equals("shield")) {
            if (me.money < shield) {
                System.out.println("You don't have enough money for shield.");
                return;
            }
            System.out.println("You carry the shield and boost your defense.");
            me.shield = 5;
            me.money -= shield;
        }
    }

    public static void SellGoldSilver(Player me, String[] items, int[][] itemLoc) {
        for (int i = 0; i < itemLoc.length; ++i) {
            if (itemLoc[i][0] < 0 && items[i].equals("gold")) {
                if (Input.inputStr("Sell your gold (y/n)?").equals("y")) {
                    System.out.println("You sell your gold for $100. You have $" + me.money);
                    itemLoc[i][0] = 999;        // take it off the map
                    me.money += 100;
                }
            }
            if (itemLoc[i][0] < 0 && items[i].equals("silver")) {
                if (Input.inputStr("Sell your silver (y/n)?").equals("y")) {
                    System.out.println("You sell your silver for $50. You have $" + me.money);
                    itemLoc[i][0] = 999;        // take it off the map
                    me.money += 50;
                }
            }
        }
    }
}

class DoAction {    // actions you type in

    public static void Go(Player me, String exits, String direction) {    // exits = map[me.row][me.col]
        int row = me.row;               // so we can see if moved
        int col = me.col;
        String[] words = direction.split(" ");       // is direction included? eg. 'go nw'
        String dir = (words.length > 1) ? words[1] : Input.inputStr("Which way? ");   // ask direction
        if (dir.equals("nw") && exits.charAt(0) == '1') {
            me.row -= 1;
            me.col -= 1; }
        if (dir.equals("n") && exits.charAt(1) == '1') {
            me.row -= 1; }
        if (dir.equals("ne") && exits.charAt(2) == '1') {
            me.row -= 1;
            me.col += 1; }
        if (dir.equals("w") && exits.charAt(3) == '1') {
            me.col -= 1; }
        if (dir.equals("e") && exits.charAt(4) == '1') {
            me.col += 1; }
        if (dir.equals("sw") && exits.charAt(5) == '1') {
            me.row += 1;
            me.col -= 1; }
        if (dir.equals("s") && exits.charAt(6) == '1') {
            me.row += 1; }
        if (dir.equals("se") && exits.charAt(7) == '1') {
            me.row += 1;
            me.col += 1; }
        if (me.row == row && me.col == col) {   // not moved
            System.out.println("You cannot go that way!"); }
        else {
            System.out.println("You follow a long and twisty path " + dir.toUpperCase());
            System.out.println("and eventually come to a new place.");
            me.health -= 1; }     // moving uses energy, eat food!
    }

    public static void Search(Player me, int[][] itemLoc, String[] items, Player[] trolls) {
        // check for trolls first
        for (Player troll : trolls) {
            if (troll.row == me.row && troll.col == me.col) {  // if troll here
                if (troll.health < 0) {
                    System.out.println("You see a dead troll lying here."); }
                else {
                    System.out.println("The troll won't let you search!");
                    return; } }
        }
        // search for items at this location
        System.out.println("\nSearching...");
        String found = "";
        for (int i = 0; i < itemLoc.length; ++i) {
            if (itemLoc[i][0] == me.row && itemLoc[i][1] == me.col)      // if item at this location
                found += items[i] + ", "; }
        if (found.isEmpty()) found = "nothing of interest., ";
        System.out.println("You found: " + (found.substring(0, found.length()-2)));
    }

    public static void Status(Player me, String[] items, int[][] itemLoc) {
        System.out.println("Your health is " + me.health + "/" + me.maxHealth);
        System.out.println("You have $" + me.money + " in your wallet.");
        String carry = "";      // items you are carrying
        for (int i = 0; i < itemLoc.length; ++i) {
            if (itemLoc[i][0] < 0) {
                carry += items[i] + ", "; } }
        if (carry.isEmpty()) carry += "nothing, ";       // last 2 chars (, ) removed
        System.out.println("Carrying: " + carry.substring(0, carry.length()-2) + ".");
    }

    public static String Eat(Player me, int[][] itemLoc, String[] items, String[][] map) {
        Random random = new Random();
        for (int i = 0; i < itemLoc.length; ++i) {                      // for each item
                if (itemLoc[i][0] < 0 && items[i].equals("food")) {     // if food carried (location<0)
                    me.health = me.maxHealth;
                    itemLoc[i][0] = random.nextInt(map.length);         // create new food
                    itemLoc[i][1] = random.nextInt(map[0].length);      // at random location
                    me.carrying -= 1;                                   // less to carry
                    return "You eat the food and your health is boosted."; }
        }
        return "You aren't carrying any food!";
    }

    public static String Get(Player me, int[][] itemLoc, String[] items, String input) {  // get object
        if (me.carrying == me.maxCarrying) return "You cannot carry any more items!";
        String[] words = input.split(" ");        // item supplied? eg ;'get dagger'
        String object = (words.length > 1) ? words[1] : Input.inputStr("Item to get? ");
        if (object.equals("troll")) return "You cannot get the troll, he is too heavy.";

        for (int i = 0; i < itemLoc.length; ++i) {                      // for each item
            if (itemLoc[i][0] == me.row && itemLoc[i][1] == me.col) {   // at my location
                if (items[i].equals(object)) {      // is this object to get?
                    itemLoc[i][0] = -1;             // location < 0 means we're carrying it
                    me.carrying += 1;               // carrying another item
                    return "You pick up the " + object + "."; }
            }
        }
        return "Could not find that! Enter 'search'\nto see what items are here.";
    }

    public static String Drop(Player me, int[][] itemLoc, String[] items, String input) {  // eg. 'get dagger'
        String[] words = input.split(" ");     // object supplied?
        String object = (words.length > 1) ? words[1] : Input.inputStr("Item to drop? ");
        for (int i = 0; i < itemLoc.length; ++i) {                  // for each item
            if (itemLoc[i][0] < 0 && items[i].equals(object)) {   // is it carried? (location<0)
                itemLoc[i][0] = me.row;     // set dropped item location to here
                itemLoc[i][1] = me.col;
                me.carrying -= 1;           // carrying one less item
                return "You drop the " + items[i];
            }
        }
        return "You aren't carrying that item!";
    }
    
    public static String Use(Player me, int[][] itemLoc, String[] items, String inp) {    // eg. 'use dagger'
        String[] words = inp.split(" ");     // object supplied? items[0/1/2] are weapons
        String object = (words.length > 1) ? words[1] : Input.inputStr("Item to use? ");
        if (object.equals(items[0]) && itemLoc[0][0] < 0) {  // item[0]=dagger
            me.weapon = items[0];
            me.attack = 20;
            return "Weapon selected: " + me.weapon; }
        if (object.equals(items[1]) && itemLoc[1][0] < 0) {  // item[1]=axe
            me.weapon = items[1];   
            me.attack = 30;
            return "Weapon selected: " + me.weapon; }
        if (object.equals(items[2]) && itemLoc[2][0] < 0) {    // item[2]=sword
            me.weapon = items[2];
            me.attack = 40;
            return "Weapon selected: " + me.weapon; }
        // add other items to be used here...
        return "Couldn't use that item!";
    }
}

public class Adventure {        // =========> STARTS HERE! <==============
    public static void main(String[] args) {
        // 4x4 grid of locations, exits = NW/N/NE/W/E/SW/S/SE - see MapLoc.Look()
        String[][] map = {
                {"00001000","00001100","00011010","00010010"},
                {"01100000","01001000","01010010","01000010"},
                {"01001010","11111111","01011100","01010100"},
                {"00001000","00101000","00101000","00010000"}
        };
        // a name for every place 'You are in..."
        String[][] placeName = {
                {"a large cave", "a dense wood", "clearing in the wood", "Reddrift village"},
                {"Grayhost valley", "Craghorn Farm", "a boggy marsh", "Mudgrave village"},
                {"Whitemere village", "dense fog", "Deephelm village", "Beargrave forest"},
                {"Whitemaw mountain", "Drydrift village", "the high plains", "a mountain fog"}
        };
        // items 0/1/2 are weapons
        String[] items = {"dagger","axe", "sword","food","food","food","gold","money","silver","money"};
        // location for each item, itemLoc[i][0] < 0 = you carry it
        int[][] itemLoc = new int[items.length][2];
        
        Player me = new Player();
        Player[] trolls = new Player[10];            // how many bad guys do you want?
        Initialise.SetUp(itemLoc, map.length, map[0].length, trolls, me);   // set up stuff

        Initialise.Help(me, placeName);     // show help on game start
        while (true) {         // game loop
            Player.Troll(me, trolls, map);
            if (me.health < 0) break;
            String inp = Input.inputStr("\nAction: ");
            if (inp.startsWith("quit") || inp.equals("q")) break;
            if (inp.startsWith("help")) Initialise.Help(me, placeName);
            if (inp.startsWith("use")) System.out.println(DoAction.Use(me, itemLoc, items, inp));
            if (inp.startsWith("drop")) System.out.println(DoAction.Drop(me, itemLoc, items, inp));
            if (inp.startsWith("eat")) System.out.println(DoAction.Eat(me, itemLoc,items, map));
            if (inp.startsWith("get")) System.out.println(DoAction.Get(me, itemLoc, items, inp));
            if (inp.startsWith("status")) DoAction.Status(me, items, itemLoc);
            if (inp.startsWith("search")) DoAction.Search(me, itemLoc, items, trolls);
            if (inp.startsWith("look")) MapLoc.Look(me, map, placeName, trolls, itemLoc, items);
            if (inp.startsWith("go")) DoAction.Go(me, map[me.row][me.col], inp);
        }
    }
}
