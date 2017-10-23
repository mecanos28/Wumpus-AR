package com.clavicusoft.wumpus.Maze;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.clavicusoft.wumpus.Draw.IntPair;

public class Graph {

    private ArrayList<Cave> allCaves;
    private boolean[][] cavesRelations;
    private int maximumCaves;
    private boolean[] connected;
    private int[] caveToArrayMapping;
    private Random random;
    //Creates an empty graph for irregular mazes.

    /**
     * Creates a simple Graph with 20 maximum, zero caves and zero relations.
     */
    public Graph() {
        this.maximumCaves = 20;
        this.cavesRelations = new boolean[20][20];
        this.random = new Random();
        //this.allCaves = new ArrayList<Cave>();
    }

    //Creates an empty graph for irregular mazes with the specified number of caves.

    /**
     * Creates a simple Graph with a given value of maximum caves, with zero caves and zero relations.
     * @param numCaves Maximum number of caves for the Graph
     */
    public Graph(int numCaves) {
        this.maximumCaves = numCaves;
        this.cavesRelations = new boolean[numCaves][numCaves];
        this.random = new Random();
        //this.allCaves = new ArrayList<Cave>();
    }


    /**
     *  Creates an empty graph for irregular mazes with the specified number of caves.
     * @param numCaves Maximum number of caves for the Graph
     * @param caveArrayList A list of all the caves in the Graph
     */
    public Graph(int numCaves, ArrayList<Cave> caveArrayList) {
        this.maximumCaves = numCaves;
        this.allCaves = caveArrayList;
        this.cavesRelations = new boolean[numCaves][numCaves];
        this.caveToArrayMapping = new int[numCaves];
        this.random = new Random();
        this.allCaves = new ArrayList<Cave>();
    }


    /**
     * Creates a graph from a relation's string and the corresponding number of caves.
     * @param relations A especial String that contains all the relations between caves.
     * @param numberOfCaves Maximum number of caves in the Graph
     */
    public Graph(String relations, int numberOfCaves) {
        this.maximumCaves = numberOfCaves;
        this.cavesRelations = new boolean[this.maximumCaves][this.maximumCaves];
        this.stringToArray(relations);
        this.random = new Random();
        this.allCaves = new ArrayList<Cave>();
    }

    /**
     * Yields the index in the CaveArray with a given Id.
     * @param x The cave Id.
     * @return
     */
    public int searchIdInArray (int x){
        int i = 0;
        boolean resume = false;
        while ( i<this.caveToArrayMapping.length && !resume )
        {
            if(caveToArrayMapping[i]==x) {
                resume = true;
            }else {
                i++;
            }
        }
        return  i;
    }

    /**
     * Fills the graph array with the information from the drawing
     * @param relations Array with the information of the drawing
     */
    public void fillGraph(ArrayList<IntPair> relations){

        //Maps
        int l = 0;
        while(l < this.allCaves.size()) {
            this.caveToArrayMapping[l] = allCaves.get(l).getId();
            l++;
        }
        //Inserts the relations in the array
        for(int i = 0; i < relations.size(); i++)
        {
            add_Bi_Relation(searchIdInArray(relations.get(i).x),searchIdInArray(relations.get(i).y));
            //cavesRelations[relations.get(i).x][relations.get(i).y] = true;
        }
    }

    /**
     * Since a cave is added there is one less in the maximum available
     * @param cave Cave inserted
     */
    public void addCave(Cave cave) {
        this.maximumCaves--;
        allCaves.add(cave);
    }

    /**
     * Since a cave is removed there is one more in the maximum available
     * @param cave Cave removed
     */
    public void removeCave(int cave) {
        /*boolean found = false;
        int i = 0;
        while(!found && i < maximumCaves) {
            if (allCaves.get(i).getId() == cave) {
                found = true;
                allCaves.remove(cave);
            }
            ++i;
        }*/
        this.maximumCaves++;
    }

    /**
     *Gets the maximum caves
     * @return the maximum caves
     */
    public int getMaximumCaves () {
        return maximumCaves;
    }

    /**
     * Inserts a relation between two caves
     * @param caveX_id First cave
     * @param caveY_id Second cave
     */
    public void add_Bi_Relation(int caveX_id, int caveY_id) {

        this.cavesRelations[caveX_id][caveY_id] = true;
        this.cavesRelations[caveY_id][caveX_id] = true;
    }

    /**
     * Removes a relation between two caves
     * @param caveX_id First cave
     * @param caveY_id Second cave
     */
    public void remove_Bi_Relation(int caveX_id, int caveY_id) {
        this.cavesRelations[caveX_id][caveY_id] = false;
        this.cavesRelations[caveY_id][caveX_id] = false;
    }

    /**
     * This method returns the first adjacent cave of a given father cave.
     * @param caveFather The father cave that have all his adjacent caves.
     * @return The first adjacent cave of the father, otherwise null.
     */
    public Cave getFirstCave(Cave caveFather) {
        Cave first = null;
        int index = 0;
        boolean repeat = true;
        while (index < this.allCaves.size() && repeat) {
            if (this.cavesRelations[allCaves.indexOf(caveFather)][index]) {
                first = this.allCaves.get(index);
                repeat = false;
            } else {
                index++;
            }
        }
        return first;
    }

    /**
     * This method returns the next adjacent cave of a father, from a given adjacent cave.
     * @param caveFather The father cave that have all his adjacent caves.
     * @param caveChild One od the adjacent caves of the father cave.
     * @return The next adjacent cave from the given cave, otherwise null.
     */
    public Cave getNextCave(Cave caveFather, Cave caveChild) {
        Cave next = null;
        int index = allCaves.indexOf(caveChild);
        boolean repeat = true;
        while (index < this.allCaves.size() && repeat) {
            if (this.cavesRelations[allCaves.indexOf(caveFather)][index]) {
                next = this.allCaves.get(index);
                repeat = false;
            } else {
                index++;
            }
        }
        return next;
    }

    /**
     * Converts a string of relations into an array.
     * @param relations
     */
    public void stringToArray(String relations) {
        int row = 0;
        int column = 0;
        for (int i = 0; i < relations.length() - 1; ++i)
        {
            if (relations.charAt(i) == '1') {
                this.add_Bi_Relation(row, column);
                ++column;
            }
            else if (relations.charAt(i) == '0') {
                ++column;
            }
            else {
                ++row;
                column = 0;
            }
        }
    }

    /**
     * Translates the current Graph into a String.
     *
     * @return The Graph as a String.
     */
    public String arrayToString() {
        String result = "";
        for (int i = 0; i < maximumCaves; i++) {
            for (int j = 0; j < maximumCaves; j++) {
                if (cavesRelations[i][j]){
                    result = result + "1";
                }
                else {
                    result = result + "0";
                }
            }
            if (i != (maximumCaves - 1)){
                result = result + "*";
            }
        }
        return result;
    }

    /**
     * Checks if two caves are connected.
     *
     * @param caveX_id First cave.
     * @param caveY_id Second cave.
     * @return True if the caves are connected, false otherwise.
     */
    public boolean areConnected(int caveX_id, int caveY_id) {
        boolean connected = false;
        if (this.cavesRelations[caveX_id][caveY_id]) {
            connected = true;
        }
        return connected;
    }

    /**
     * Checks if the cave isn't connected to other caves.
     *
     * @param cave Actual cave.
     * @return True if the cave has no connections, false otherwise.
     */
    public boolean isIsolated(int cave) {
        boolean isolated = true;
        int i = 0;
        while (isolated && i < this.maximumCaves) {
            if (this.areConnected(i, cave)) {
                isolated = false;
            }
            ++i;
        }
        return isolated;
    }

    /**
     * Checks if the current graph is a valid one for the Wumpus game.
     *
     * @return True if the graph it's valid, false otherwise.
     */
    public boolean valid () {
        boolean valid = true;
        //A maze has to have at least 2 caves.
        if (this.maximumCaves < 2) {
            return false;
        }
        int i = 0;
        while (i < this.maximumCaves && valid) {
            if (this.isIsolated(i)) {
                valid = false;
            }
            ++i;
        }
        if (valid) {
            connected = new boolean[maximumCaves];
            checkConnectedCaves(0);
            i = 0;
            while (valid && i < maximumCaves){
                if (!connected[i]) {
                    valid = false;
                }
                ++i;
            }
        }
        return valid;
    }

    /**
     * Checks if all the caves are connected.
     *
     * @param cave Actual cave.
     */
    private void checkConnectedCaves(int cave)
    {
        connected[cave] = true;
        for (int i = 0; i != maximumCaves; ++i) {
            if ((i != cave) && areConnected(i, cave) && !connected[i]){
                checkConnectedCaves(i);
            }
        }
    }

    //TODO RANDOM GEN

    /**
     *
     * @param playerPosition Id of the cave where the player will init the game
     * @return
     */
    public CaveContent[] randomEntitiesGen(int playerPosition) {
        CaveContent[] caveContents = new CaveContent[maximumCaves];
        boolean[] isCaveNotEmpty = new boolean[maximumCaves];
        Arrays.fill(caveContents, CaveContent.EMPTY);
        Arrays.fill(isCaveNotEmpty, false);

        caveContents[this.searchIdInArray(playerPosition)]= CaveContent.PLAYER;
        isCaveNotEmpty[this.searchIdInArray(playerPosition)] = true;

        /**1 wumpus, 2 pits (0<=3,1<=6) , 2 bats (0<=3, 1<=6)**/
        int amount[] = new int[3]; // 0: pits, 1: bats, 2: wumpus
        amount[0] = 0;
        amount[1] = 0;
        amount[2] = 1;

        if(maximumCaves > 3 && maximumCaves <= 6) {
            amount[0] = 1;
            amount[1] = 1;
        } else if (maximumCaves > 6) {
            amount[0] = 2;
            amount[1] = 2;
        }

        boolean retry = true;

        while(retry) {

            for (int i = 0; i < 3; i++) {

                for (int j = 0; j < amount[i] ; j++) {

                    boolean replace = true;
                    while (replace) {

                        int cavePosition = this.random.nextInt(maximumCaves);
                        if(!isCaveNotEmpty[cavePosition]) {

                            switch (i) {
                                case 0:
                                    caveContents[cavePosition] = CaveContent.PIT;
                                    break;
                                case 1:
                                    caveContents[cavePosition] = CaveContent.BAT;
                                    break;
                                case 2:
                                    caveContents[cavePosition] = CaveContent.WUMPUS;
                                    break;
                            }
                            isCaveNotEmpty[cavePosition] = true;
                            replace = false;
                        }
                    }
                }
            }
            retry = this.randomEntitiesGenValidator(caveContents);

            if(retry) {
                Arrays.fill(caveContents, CaveContent.EMPTY);
                Arrays.fill(isCaveNotEmpty, false);

                caveContents[this.searchIdInArray(playerPosition)]= CaveContent.PLAYER;
                isCaveNotEmpty[this.searchIdInArray(playerPosition)] = true;
            }
        }

        System.out.println(Arrays.toString(caveContents));

        /**Put information in every caves**/
        for (int i = 0; i < this.allCaves.size() ; i++) {
            this.allCaves.get(i).setCaveContent(caveContents[i]);
        }
        return caveContents;
    }
    //TODO RANDOM GEN VALIDATOR
    private boolean randomEntitiesGenValidator(CaveContent[] caveContents) {
        return false;
    }
}
