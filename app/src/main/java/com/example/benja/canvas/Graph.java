package com.example.benja.canvas;

import java.util.ArrayList;

public class Graph {


    private ArrayList<Cave> allCaves;
    private boolean[][] cavesRelations;
    private int maximumCaves;

    //Creates an empty graph for irregular mazes.
    public Graph() {
        this.maximumCaves = 0;
        this.cavesRelations = new boolean[20][20];
        this.allCaves = new ArrayList<Cave>();
    }

    //Creates a graph from a relation's string and the corresponding number of caves.
    public Graph(String relations, int numberOfCaves) {
        this.maximumCaves = numberOfCaves;
        this.cavesRelations = new boolean[this.maximumCaves][this.maximumCaves];
        stringToArray(relations);
        this.allCaves = new ArrayList<Cave>();
    }

    /*
    public Graph(boolean[][] relations) {
        this.maximumCaves = relations.length;
        this.cavesRelations = relations;
        this.allCaves = new ArrayList<Cave>();
    }
    */

    public void addCave() {
        this.maximumCaves++;
    }

    public void removeCave() {
        this.maximumCaves--;
    }

    public void add_Bi_Relation(int caveX_id, int caveY_id) {

        this.cavesRelations[caveX_id][caveY_id] = true;
        this.cavesRelations[caveY_id][caveX_id] = true;
    }

    public void remove_Bi_Relation(int caveX_id, int caveY_id) {

        this.cavesRelations[caveX_id][caveY_id] = true;
        this.cavesRelations[caveY_id][caveX_id] = true;
    }

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
            result = result + "*";
        }
        return result;
    }

}
