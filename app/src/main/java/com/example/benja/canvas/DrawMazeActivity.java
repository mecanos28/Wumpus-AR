package com.example.benja.canvas;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.ImageButton;

public class DrawMazeActivity extends Activity {

    private DrawCanvas myCanvas;
    private Graph customMaze;
    private int caveToDelete, cave1, cave2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        myCanvas = (DrawCanvas)findViewById(R.id.viewDrawCanvas);
        //customMaze = myCanvas.getCustomMaze();
    }

    //Agregar una cueva
    public void addC(View v){
        //Deshabilitar el canvas hasta que se presione el botón de agregar
        //OnTouch
        //myCanvas.setEnabled(true);
    }

    //Borrar una cueva
    public void delC(View v){
        final Dialog dialogDeleteCave= new Dialog(this);
        dialogDeleteCave.setTitle("Borrar cueva");
        dialogDeleteCave.setContentView(R.layout.layout_choosecave);
        final EditText edtTxtCaveToDelete = (EditText)dialogDeleteCave.findViewById(R.id.editTxtNumCave);
        Button btnAcceptDeleteCave = (Button)dialogDeleteCave.findViewById(R.id.btnAccept);
        Button btnCancelDeleteCave = (Button)dialogDeleteCave.findViewById(R.id.btnCancel);
        btnAcceptDeleteCave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                caveToDelete = Integer.parseInt(edtTxtCaveToDelete.getText().toString());
                myCanvas.deleteCave(caveToDelete);
                dialogDeleteCave.dismiss();
            }
        });
        btnCancelDeleteCave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialogDeleteCave.cancel();
            }
        });
        dialogDeleteCave.show();
    }

    //Agregar un arco
    public void addA(View v)
    {
        final Dialog dialogAddArc = new Dialog(this);
        dialogAddArc.setTitle("Agregar arco");
        dialogAddArc.setContentView(R.layout.layout_choosecaves);
        final EditText edtTxtCave1 = (EditText)dialogAddArc.findViewById(R.id.editTxtCave1);
        final EditText edtTxtCave2 = (EditText)dialogAddArc.findViewById(R.id.editTxtCave2);
        Button btnAcceptAddArc = (Button)dialogAddArc.findViewById(R.id.btnAccept);
        Button btnCancelAddArc = (Button)dialogAddArc.findViewById(R.id.btnCancel);
        btnAcceptAddArc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                cave1 = Integer.parseInt(edtTxtCave1.getText().toString());
                cave2 = Integer.parseInt(edtTxtCave2.getText().toString());
                Cave c1 = myCanvas.searchCave(cave1);
                Cave c2 = myCanvas.searchCave(cave2);
                if(c1 != null && c2 != null){
                    myCanvas.addArc(c1, c2);
                    myCanvas.getRelations().add(new IntPair(c1.getId(),c2.getId()));
                    myCanvas.getRelations().add(new IntPair(c2.getId(),c1.getId()));
                }
                else if(c1 == null && c2 != null){
                    Toast.makeText(DrawMazeActivity.this, "Error, type the first cave's number again.", Toast.LENGTH_SHORT).show();
                }
                else if (c1 != null && c2 == null){
                    Toast.makeText(DrawMazeActivity.this, "Error, type the second cave's number again.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(DrawMazeActivity.this, "Error, type the caves' number again.", Toast.LENGTH_SHORT).show();
                }
                dialogAddArc.dismiss();
            }
        });
        btnCancelAddArc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialogAddArc.cancel();
            }
        });
        dialogAddArc.show();
    }

    //Borrar un arco
    public void delA(View v){
        final Dialog dialogDeleteArc = new Dialog(this);
        dialogDeleteArc.setTitle("Borrar arco");
        dialogDeleteArc.setContentView(R.layout.layout_choosecaves);
        final EditText edtTxtCaveDel1 = (EditText)dialogDeleteArc.findViewById(R.id.editTxtCave1);
        final EditText edtTxtCaveDel2 = (EditText)dialogDeleteArc.findViewById(R.id.editTxtCave2);
        Button btnAcceptDelArc = (Button)dialogDeleteArc.findViewById(R.id.btnAccept);
        Button btnCancelDelArc = (Button)dialogDeleteArc.findViewById(R.id.btnCancel);
        btnAcceptDelArc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                cave1 = Integer.parseInt(edtTxtCaveDel1.getText().toString());
                cave2 = Integer.parseInt(edtTxtCaveDel2.getText().toString());
                Cave c1 = myCanvas.searchCave(cave1);
                Cave c2 = myCanvas.searchCave(cave2);
                if(cave1 < customMaze.getMaximumCaves() && cave2 < customMaze.getMaximumCaves()) {
                    myCanvas.deleteArc(c1, c2);
                }
                dialogDeleteArc.dismiss();
            }
        });
        btnCancelDelArc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialogDeleteArc.cancel();
            }
        });
        dialogDeleteArc.show();
    }

    //Reiniciar el dibujo
    public void newD(View v){
        //¿Reiniciar el dibujo o guardar el que estaba haciendo y empezar uno nuevo?
        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle("Nuevo Laberinto");
        newDialog.setMessage("¿Está seguro que desea comenzar un nuevo dibujo? Perderá el dibujo actual.");
        newDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                myCanvas.newDraw();
                myCanvas.setupDrawing();
                dialog.dismiss();
            }
        });
        newDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        newDialog.show();
    }

    public void check(View v){
        customMaze = new Graph(myCanvas.getNumCaves());
        customMaze.fillGraph(myCanvas.getRelations());
        saveMaze();
    }

    /*
    * Saves the actual maze.
    */
    public void saveMaze() {
        if (customMaze.valid()) {
            String relations = customMaze.arrayToString();

            AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();

            ContentValues data = new ContentValues();
            data.put("relations", relations);
            data.put("number_of_caves", customMaze.getMaximumCaves());
            db.insert("GRAPH", null, data);

            Cursor cell = db.rawQuery("SELECT id FROM GRAPH WHERE GRAPH.relations = " + relations, null);
            if (cell.moveToFirst()) {
                int graphID = cell.getInt(0);
                cell.close();
                db.close();
                //TODO: Ask if they want to keep drawing or if they want to play with the new maze.
                //TODO: If they want to play, send the graphID to the next Layout.
            } else {
                Toast.makeText(this, "The Wumpus doesn't seem to like this caves. Try drawing another one!", Toast.LENGTH_LONG).show();
            }
            cell.close();
            db.close();
        }
        else {
            Toast.makeText(this, "The Wumpus doesn't seem to like this caves. Try drawing another one!", Toast.LENGTH_LONG).show();
        }
    }

}
