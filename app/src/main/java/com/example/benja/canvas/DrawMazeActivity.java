package com.example.benja.canvas;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.UUID;

import java.io.FileInputStream;
import java.io.IOException;

public class DrawMazeActivity extends Activity implements View.OnClickListener{

    private DrawCanvas myCanvas;
    private Graph customMaze;
    private ImageButton newDrawing, saveDrawing, addCave, addArc, deleteCave, deleteArc;
    private int caveToDelete, cave1, cave2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        myCanvas = (DrawCanvas)findViewById(R.id.viewDrawCanvas);

        newDrawing = (ImageButton)findViewById(R.id.imgBtnNew);
        saveDrawing = (ImageButton)findViewById(R.id.imgBtnSave);
        addCave = (ImageButton)findViewById(R.id.imgBtnAddCave);
        addArc = (ImageButton)findViewById(R.id.imgBtnAddArc);
        deleteCave = (ImageButton)findViewById(R.id.imgBtnDeleteCave);
        deleteArc = (ImageButton)findViewById(R.id.imgBtnDeleteArc);

        newDrawing.setOnClickListener(this);
        saveDrawing.setOnClickListener(this);
        addCave.setOnClickListener(this);
        addArc.setOnClickListener(this);
        deleteCave.setOnClickListener(this);
        deleteArc.setOnClickListener(this);

        customMaze = myCanvas.getCustomMaze();
    }

    //Monitorea la actividad de los botones
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.imgBtnNew:    //Nuevo dibujo
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle("Nuevo Laberinto");
                newDialog.setMessage("¿Está seguro que desea comenzar un nuevo dibujo? Perderá el dibujo actual.");
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
                break;
            case R.id.imgBtnSave:   //Guardar dibujo
                AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
                saveDialog.setTitle("Guardar Laberinto");
                saveDialog.setMessage("¿Desea guardar este laberinto?");
                saveDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        myCanvas.setDrawingCacheEnabled(true);
                        String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(),myCanvas.getDrawingCache(), UUID.randomUUID().toString()+".png","drawing");
                        if(imgSaved!=null){
                            Toast savedToast = Toast.makeText(getApplicationContext(),"Su laberinto se ha guardado exitosamente.", Toast.LENGTH_SHORT);
                            saveMaze();
                            savedToast.show();
                        }
                        else{
                            Toast unsavedToast = Toast.makeText(getApplicationContext(),"Error, no se ha podido guardar su laberinto.", Toast.LENGTH_SHORT);
                            unsavedToast.show();
                        }
                        myCanvas.destroyDrawingCache();
                    }
                });
                saveDialog.setNegativeButton("No", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                saveDialog.show();
                break;
            case R.id.imgBtnAddCave: //Agregar cueva
                //TODO: Buscar una manera de desactivar el canvas si no se presiona el botón
                break;
            case R.id.imgBtnAddArc: //Agregar un arco
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
                        if(cave1 < customMaze.getMaximumCaves() && cave2 < customMaze.getMaximumCaves()) {
                            myCanvas.addArc(cave1, cave2);
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
                break;
            case R.id.imgBtnDeleteCave: //Borrar una cueva
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
                //TODO: Arreglarlo, se borra un click después de haber escogido
                break;
            case R.id.imgBtnDeleteArc: //Borrar un arco
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
                        if(cave1 < customMaze.getMaximumCaves() && cave2 < customMaze.getMaximumCaves()) {
                            myCanvas.deleteArc(cave1, cave2);
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
                //TODO: Verificar las coordenadas para que se borre bien
                break;
            default:
                break;
        }
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
