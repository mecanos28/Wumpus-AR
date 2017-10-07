package com.example.benja.canvas;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Intent;
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
    private String name;
    AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_draw);
        myCanvas = findViewById(R.id.viewDrawCanvas);
        alert = new AlertDialog.Builder(this);
        alert.setTitle("Instrucciones");
        alert.setMessage("- Para agregar una cueva debe presionar la pantalla donde desea colocarla, seguidamente presionar el botón \"Agregar Cueva\".\n\n- Para eliminar una cueva, presione el botón \"Eliminar Cueva\" e indique el número de la cueva que desea eliminar, esto eliminará a su vez los caminos conectados a esta cueva.\n\n- Para agregar o eliminar un camino entre dos cuevas, presione el botón \"Agregar Camino\" o \"Eliminar Camino\" e indique las dos cuevas que desea conectar o desconectar.\n\n- Una vez finalizado el dibujo presione el botón \"Guardar Dibujo\" lo que almacenará el laberinto en la biblioteca y permitirá utilizarlo para jugar.");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public void info (View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Instrucciones");
        alert.setMessage("- Para agregar una cueva debe presionar la pantalla donde desea colocarla, seguidamente presionar el botón \"Agregar Cueva\".\n\n- Para eliminar una cueva, presione el botón \"Eliminar Cueva\" e indique el número de la cueva que desea eliminar, esto eliminará a su vez los caminos conectados a esta cueva.\n\n- Para agregar o eliminar un camino entre dos cuevas, presione el botón \"Agregar Camino\" o \"Eliminar Camino\" e indique las dos cuevas que desea conectar o desconectar.\n\n- Una vez finalizado el dibujo presione el botón \"Guardar Dibujo\" lo que almacenará el laberinto en la biblioteca y permitirá utilizarlo para jugar.");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });
        alert.show();
    }

    //Agregar una cueva
    public void addC(View v){
        myCanvas.addCave();
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
                    alert.setTitle("Error");
                    alert.setMessage("La cueva que desea borrar no existe.");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    });
                    alert.show();
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
                        alert.setTitle("Error");
                        alert.setMessage("No puede haber un camino de una cueva hacia sí misma.");
                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which){
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                    }

                }
                else if(cave1 > myCanvas.getNumCave() && cave2 < myCanvas.getNumCave()){
                    alert.setTitle("Error");
                    alert.setMessage("La primera cueva que ingresó no existe.");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
                else if (cave1 < myCanvas.getNumCave() && cave2 > myCanvas.getNumCave()){
                    alert.setTitle("Error");
                    alert.setMessage("La segunda cueva que ingresó no existe.");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
                else {
                    alert.setTitle("Error");
                    alert.setMessage("Las cuevas que ingresó no existen.");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    });
                    alert.show();
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
        dialogDeleteArc.setContentView(R.layout.layout_delete_relation);
        final EditText edtTxtCaveDel1 = dialogDeleteArc.findViewById(R.id.editTxtCave01);
        final EditText edtTxtCaveDel2 = dialogDeleteArc.findViewById(R.id.editTxtCave02);
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
                        alert.setTitle("Error");
                        alert.setMessage("El camino que desea borrar no existe.");
                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which){
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                    }
                }
                else if(cave1 > myCanvas.getNumCave() && cave2 < myCanvas.getNumCave()){
                    alert.setTitle("Error");
                    alert.setMessage("La primera cueva que ingresó no existe.");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
                else if (cave1 < myCanvas.getNumCave()&& cave2 > myCanvas.getNumCave()){
                    alert.setTitle("Error");
                    alert.setMessage("La segunda cueva que ingresó no existe.");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
                else {
                    alert.setTitle("Error");
                    alert.setMessage("Las cuevas que ingresó no existen.");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    });
                    alert.show();
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
        if (customMaze.valid()) {
            askMazeName();
        }
        else {
            alert.setTitle("Error");
            alert.setMessage("No se puede capturar al wumpus en este dibujo creado, porfavor verifique que las siguientes restricciones se cumplan:\n\n-Deben haber al menos 2 cuevas.\n\n-No deben haber más de 20 cuevas.\n\n- No pueden existir cuevas aisladas, es decir, se puede llegar de una cueva a cualquier otra a través de uno o varios caminos.");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.dismiss();
                }
            });
            alert.show();
        }
    }

    /*
    * Saves the actual maze.
    */
    public void saveMaze() {
        String relations = customMaze.arrayToString();

        AdminSQLite admin = new AdminSQLite(this, "WumpusDB", null, 5);
        SQLiteDatabase db = admin.getWritableDatabase();

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = "-" + tsLong.toString();

        ContentValues data = new ContentValues();
        data.put("name", name + ts);
        data.put("relations", relations);
        data.put("number_of_caves", customMaze.getMaximumCaves());
        db.insert("GRAPH", null, data);

        Cursor cell = db.rawQuery("SELECT id FROM GRAPH WHERE GRAPH.relations = \"" + relations + "\"", null);
        if (cell.moveToFirst()) {
            final String graphID = cell.getString(0);
            cell.close();
            db.close();
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("El dibujo ha sido guardado");
            newDialog.setMessage("¿Desea iniciar una partida con este laberinto?");
            newDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.dismiss();
                    startGame(graphID);
                }
            });
            newDialog.setNegativeButton("No", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    myCanvas.newDraw();
                    dialog.dismiss();
                }
            });
            newDialog.show();
        } else {
            alert.setTitle("Error");
            alert.setMessage("Hubo un problema guardando el laberinto.");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.dismiss();
                }
            });
            alert.show();
        }
        cell.close();
        db.close();
    }

    public void askMazeName () {
        final Dialog dialogAddArc = new Dialog(this);
        dialogAddArc.setContentView(R.layout.layout_maze_name);
        final EditText edtTxtName = dialogAddArc.findViewById(R.id.editTxtNameMaze);
        Button btnAccept = dialogAddArc.findViewById(R.id.btnAcceptName);
        Button btnCancel = dialogAddArc.findViewById(R.id.btnCancelName);
        btnAccept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!edtTxtName.getText().toString().equals("")){
                    name =  edtTxtName.getText().toString();
                    dialogAddArc.dismiss();
                    saveMaze();
                }
                else {
                    alert.setTitle("Error");
                    alert.setMessage("Debe introducir un nombre para el dibujo");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialogAddArc.cancel();
            }
        });
        dialogAddArc.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void startGame (String stringGraphID) {
        Intent i = new Intent(this, Coordenadas.class);
        i.putExtra("graphID",stringGraphID);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        startActivity(i, options.toBundle());
    }

}
