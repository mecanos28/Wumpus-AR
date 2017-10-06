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

public class DrawMazeActivity extends Activity {

    private DrawCanvas myCanvas;
    private Graph customMaze;
    private int caveToDelete, cave1, cave2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_draw);
        myCanvas = findViewById(R.id.viewDrawCanvas);
        Toast.makeText(DrawMazeActivity.this, "Una vez que presione la pantalla, se capturarán las coordenadas para crear la próxima cueva.", Toast.LENGTH_LONG).show();
    }

    //Agregar una cueva
    public void addC(View v){
        myCanvas.addCave();
        Toast.makeText(DrawMazeActivity.this, "Recuerde que para agregar una nueva cueva, debe presionar primero su ubicación en la pantalla.", Toast.LENGTH_SHORT).show();

    }

    //Borrar una cueva
    public void delC(View v){
        final Dialog dialogDeleteCave= new Dialog(this);
        dialogDeleteCave.setContentView(R.layout.layout_choosecave);
        final EditText edtTxtCaveToDelete = dialogDeleteCave.findViewById(R.id.editTxtNumCave);
        Button btnAcceptDeleteCave = dialogDeleteCave.findViewById(R.id.btnAccept);
        Button btnCancelDeleteCave = dialogDeleteCave.findViewById(R.id.btnCancel);
        btnAcceptDeleteCave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                caveToDelete = Integer.parseInt(edtTxtCaveToDelete.getText().toString());
                if(caveToDelete < myCanvas.getNumCave()) {
                    myCanvas.deleteCave(caveToDelete);
                    dialogDeleteCave.dismiss();
                }
                else
                {
                    Toast.makeText(DrawMazeActivity.this, "Error, vuelva a ingresar la cueva que desea eliminar.", Toast.LENGTH_LONG).show();
                }
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
        dialogAddArc.setContentView(R.layout.layout_choosecaves);
        final EditText edtTxtCave1 = dialogAddArc.findViewById(R.id.editTxtCave1);
        final EditText edtTxtCave2 = dialogAddArc.findViewById(R.id.editTxtCave2);
        Button btnAcceptAddArc = dialogAddArc.findViewById(R.id.btnAccept);
        Button btnCancelAddArc = dialogAddArc.findViewById(R.id.btnCancel);
        btnAcceptAddArc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                cave1 = Integer.parseInt(edtTxtCave1.getText().toString());
                cave2 = Integer.parseInt(edtTxtCave2.getText().toString());
                if(cave1 < myCanvas.getNumCave() && cave2 < myCanvas.getNumCave()){
                    if (cave1 != cave2)
                    {
                        Cave c1 = myCanvas.searchCave(cave1);
                        Cave c2 = myCanvas.searchCave(cave2);
                        if(c1 != null && c2 != null) {
                            myCanvas.addArc(c1, c2);
                            myCanvas.getRelations().add(new IntPair(c1.getId(), c2.getId()));
                        }
                    }
                    else
                    {
                        Toast.makeText(DrawMazeActivity.this, "Error, no puede haber un camino hacia la misma cueva.", Toast.LENGTH_SHORT).show();
                    }

                }
                else if(cave1 > myCanvas.getNumCave() && cave2 < myCanvas.getNumCave()){
                    Toast.makeText(DrawMazeActivity.this, "Error, vuelva a ingresar la primera cueva.", Toast.LENGTH_SHORT).show();
                }
                else if (cave1 < myCanvas.getNumCave() && cave2 > myCanvas.getNumCave()){
                    Toast.makeText(DrawMazeActivity.this, "Error, vuelva a ingresar la segunda cueva", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(DrawMazeActivity.this, "Error, vuelva a ingresar las cuevas.", Toast.LENGTH_SHORT).show();
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
        dialogDeleteArc.setContentView(R.layout.layout_choosecaves);
        final EditText edtTxtCaveDel1 = dialogDeleteArc.findViewById(R.id.editTxtCave1);
        final EditText edtTxtCaveDel2 = dialogDeleteArc.findViewById(R.id.editTxtCave2);
        Button btnAcceptDelArc = dialogDeleteArc.findViewById(R.id.btnAccept);
        Button btnCancelDelArc = dialogDeleteArc.findViewById(R.id.btnCancel);
        btnAcceptDelArc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                cave1 = Integer.parseInt(edtTxtCaveDel1.getText().toString());
                cave2 = Integer.parseInt(edtTxtCaveDel2.getText().toString());
                if(cave1 < myCanvas.getNumCave() && cave2 < myCanvas.getNumCave()){
                    if (cave1 != cave2)
                    {
                        Cave c1 = myCanvas.searchCave(cave1);
                        Cave c2 = myCanvas.searchCave(cave2);
                        if(c1 != null && c2 != null) {
                            myCanvas.deleteArc(c1, c2);
                        }
                    }
                    else
                    {
                        Toast.makeText(DrawMazeActivity.this, "Error, no hay caminos hacia la misma cueva.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(cave1 > myCanvas.getNumCave() && cave2 < myCanvas.getNumCave()){
                    Toast.makeText(DrawMazeActivity.this, "Error, vuelva a ingresar el número de la primera cueva.", Toast.LENGTH_SHORT).show();
                }
                else if (cave1 < myCanvas.getNumCave()&& cave2 > myCanvas.getNumCave()){
                    Toast.makeText(DrawMazeActivity.this, "Error, vuelva a ingresar el número de la segunda cueva", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(DrawMazeActivity.this, "Error, vuelva a ingresar el número de las cuevas.", Toast.LENGTH_SHORT).show();
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
        newDialog.setTitle("Reiniciar laberinto");
        newDialog.setMessage("¿Está seguro que desea comenzar un nuevo dibujo? Perderá el progreso actual.");
        newDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                myCanvas.newDraw();
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

    public void checkD(View v){
        customMaze = new Graph(myCanvas.getNumCave());
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
