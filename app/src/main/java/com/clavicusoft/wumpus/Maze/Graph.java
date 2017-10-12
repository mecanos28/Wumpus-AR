package com.clavicusoft.wumpus.Maze;

import java.util.ArrayList;
import com.clavicusoft.wumpus.Draw.IntPair;

public class Graph {

    private ArrayList<Cave> allCaves;
    private boolean[][] cavesRelations;
    private int maximumCaves;
    private boolean[] connected;
    private int[] caveToArrayMapping;

    //Creates an empty graph for irregular mazes.
    public Graph() {
        this.maximumCaves = 20;
        this.cavesRelations = new boolean[20][20];
        //this.allCaves = new ArrayList<Cave>();
    }

    //Creates an empty graph for irregular mazes with the specified number of caves.
    public Graph(int numCaves) {
        this.maximumCaves = numCaves;
        this.cavesRelations = new boolean[numCaves][numCaves];
        //this.allCaves = new ArrayList<Cave>();
    }

    //Creates an empty graph for irregular mazes with the specified number of caves.
    public Graph(int numCaves, ArrayList<Cave> caveArrayList) {
        this.maximumCaves = numCaves;
        this.allCaves = caveArrayList;
        this.cavesRelations = new boolean[numCaves][numCaves];
        this.caveToArrayMapping = new int[numCaves];
        //this.allCaves = new ArrayList<Cave>();
    }

    //Creates a graph from a relation's string and the corresponding number of caves.
    public Graph(String relations, int numberOfCaves) {
        this.maximumCaves = numberOfCaves;
        this.cavesRelations = new boolean[this.maximumCaves][this.maximumCaves];
        this.stringToArray(relations);
        //this.allCaves = new ArrayList<Cave>();
    }

    /*
    public Graph(boolean[][] relations) {
        this.maximumCaves = relations.length;
        this.cavesRelations = relations;
        this.allCaves = new ArrayList<Cave>();
    }
    */

    public int searchIdInArray (int x){
        //Devuelve índice en el array del id
        int result=0;
        for (int i=0; i<this.caveToArrayMapping.length; i++)
        {
            result = i;
            if(caveToArrayMapping[i]==x)
                break;
        }
        return  result;
    }

    public void fillGraph(ArrayList<IntPair> relations){

        //Mapea
        int l = 0;
        while(l < this.allCaves.size()) {
            this.caveToArrayMapping[l] = allCaves.get(l).getId();
            l++;
        }
        //Mete las relaciones
        for(int i = 0; i < relations.size(); i++)
        {
            add_Bi_Relation(searchIdInArray(relations.get(i).x),searchIdInArray(relations.get(i).y));
            //cavesRelations[relations.get(i).x][relations.get(i).y] = true;
        }
    }


    public void addCave(Cave cave) {
        this.maximumCaves--;
        //allCaves.add(cave);
    }

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

    public int getMaximumCaves () {
        return maximumCaves;
    }

    public void add_Bi_Relation(int caveX_id, int caveY_id) {

        this.cavesRelations[caveX_id][caveY_id] = true;
        this.cavesRelations[caveY_id][caveX_id] = true;
    }

    public void remove_Bi_Relation(int caveX_id, int caveY_id) {
        this.cavesRelations[caveX_id][caveY_id] = false;
        this.cavesRelations[caveY_id][caveX_id] = false;
    }

    /*public Cave getFirstCave(Cave caveFather) {
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
    }*/

    /*public Cave getNextCave(Cave caveFather, Cave caveChild) {
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
    }*/

    /*
    * Converts a string of relations into an array.
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

    /*
    *  Method for Irregular Mazes
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

    /*
    * Checks if there's a relation between 2 caves.
    */
    public boolean areConnected(int caveX_id, int caveY_id) {
        boolean connected = false;
        if (this.cavesRelations[caveX_id][caveY_id]) {
            connected = true;
        }
        return connected;
    }

    /*
    * Checks if a cave is isolated.
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

    /*
    * Validates the relations array.
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

    private void checkConnectedCaves(int cave)
    {
        connected[cave] = true;
        for (int i = 0; i != maximumCaves; ++i) {
            if ((i != cave) && areConnected(i, cave) && !connected[i]){
                checkConnectedCaves(i);
            }
        }
    }
}