package com.example.benja.canvas;

import java.util.ArrayList;

/**
 * Created by b50657 on 27/09/2017.
 */

public class Graph {


    private ArrayList<Cave> allCaves;
    private boolean[][] cavesRelations;
    private int maximumCaves;

    public Graph(String figureName) {
        switch (figureName) {
            case "tetra":
                maximumCaves = 4;
                break;
            case "octa":
                maximumCaves = 6;
                break;
            case "cube":
                maximumCaves = 8;
                break;
            case "ico":
                maximumCaves = 12;
                break;
            case "dode":
                maximumCaves = 20;
                break;
        }

        this.cavesRelations = new boolean[maximumCaves][maximumCaves];
        this.allCaves = new ArrayList<Cave>();
    }

    public Graph(boolean[][] relations) {
        this.maximumCaves = relations.length;
        this.cavesRelations = relations;
        this.allCaves = new ArrayList<Cave>();
    }

    //------------------------------------------------------------------------------

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

    //---------------------------------------------------------------------------------//
    public void createTetrahedron() {
        add_Bi_Relation(0, 1);
        add_Bi_Relation(0, 2);
        add_Bi_Relation(0, 3);
        add_Bi_Relation(1, 3);
        add_Bi_Relation(1, 2);
        add_Bi_Relation(2, 3);
    }

    public void createOctahedron() {
        add_Bi_Relation(0, 1);
        add_Bi_Relation(0, 2);
        add_Bi_Relation(0, 3);
        add_Bi_Relation(0, 4);
        add_Bi_Relation(1, 2);
        add_Bi_Relation(3, 4);
        add_Bi_Relation(3, 5);
        add_Bi_Relation(4, 5);
        add_Bi_Relation(3,1);
        add_Bi_Relation(5,1);
        add_Bi_Relation(5,2);
        add_Bi_Relation(4,2);

    }

    public void createCube() {
        add_Bi_Relation(0, 1);
        add_Bi_Relation(0, 2);
        add_Bi_Relation(0, 4);
        add_Bi_Relation(1, 5);
        add_Bi_Relation(1, 3);
        add_Bi_Relation(2, 6);
        add_Bi_Relation(2, 3);
        add_Bi_Relation(3, 7);
        add_Bi_Relation(6, 7);
        add_Bi_Relation(6, 4);
        add_Bi_Relation(4, 5);
        add_Bi_Relation(5, 7);
    }

    public void createIcosahedron() {
        add_Bi_Relation(0, 1);
        add_Bi_Relation(0, 2);
        add_Bi_Relation(0, 3);
        add_Bi_Relation(0, 4);
        add_Bi_Relation(0, 5);
        add_Bi_Relation(1, 2);
        add_Bi_Relation(1, 11);
        add_Bi_Relation(2, 11);
        add_Bi_Relation(9, 11);
        add_Bi_Relation(10, 11);
        add_Bi_Relation(8, 9);
        add_Bi_Relation(8, 10);
        add_Bi_Relation(8, 7);
        add_Bi_Relation(8, 6);
        add_Bi_Relation(6, 7);
        add_Bi_Relation(4, 6);
        add_Bi_Relation(7, 5);
        add_Bi_Relation(3, 4);
        add_Bi_Relation(3, 5);
        add_Bi_Relation(1, 9);
        add_Bi_Relation(1, 4);
        add_Bi_Relation(2, 10);
        add_Bi_Relation(2, 5);
        add_Bi_Relation(10,5);
        add_Bi_Relation(7,10);
        add_Bi_Relation(11,8);
        add_Bi_Relation(9,6);
        add_Bi_Relation(9,4);
        add_Bi_Relation(3,6);
        add_Bi_Relation(3,7);
        /**Puede que le falte*/
    }

    public void createDodecahedron() {
        add_Bi_Relation(0,1);
        add_Bi_Relation(0,2);
        add_Bi_Relation(0,5);
        add_Bi_Relation(1,3);
        add_Bi_Relation(1,6);
        add_Bi_Relation(3,8);
        add_Bi_Relation(3,4);
        add_Bi_Relation(4,9);
        add_Bi_Relation(4,2);
        add_Bi_Relation(2,7);
        add_Bi_Relation(5,10);
        add_Bi_Relation(5,11);
        add_Bi_Relation(7,11);
        add_Bi_Relation(7,13);
        add_Bi_Relation(9,13);
        add_Bi_Relation(9,14);
        add_Bi_Relation(8,14);
        add_Bi_Relation(8,12);
        add_Bi_Relation(6,12);
        add_Bi_Relation(6,10);
        add_Bi_Relation(10,15);
        add_Bi_Relation(11,16);
        add_Bi_Relation(13,18);
        add_Bi_Relation(14,19);
        add_Bi_Relation(15,16);
        add_Bi_Relation(16,18);
        add_Bi_Relation(18,19);
        add_Bi_Relation(19,17);
        add_Bi_Relation(17,15);

    }
    //--------------------------------------------------------------------------------------------------------------------------/

}
