package com.example.benja.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

class IntPair {
    int x;
    int y;
    IntPair(int x, int y) {this. x= x;this.y = y;}
}

public class DrawCanvas extends View {

    private Path drawPath; //Guardo el trazo
    private Paint drawPaint, canvasPaint; //Pincel
    private Canvas drawCanvas; //Lienzo
    private Bitmap canvasBitmap; //Para guardar
    private ArrayList<IntPair> relations; //Array que almacena todas las relaciones existentes

    public ArrayList<Cave> getCaves() {
        return caves;
    }

    private ArrayList<Cave> caves; //Array que almacena todas las cuevas existentes
    private float touchX, touchY; //Coordenadas
    private int numCave;

    public int getTotalCaves() {
        return totalCaves;
    }

    private int totalCaves;
    private int maxCaves; //Contador para asignar un id a cada cueva; Contador del total de cuevas; Máximo de cuevas definido

    public DrawCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    //Configuracion del area donde se va a dibujar
    public void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(0xFFFFFFFF); //Blanco //0xFF000000); //Negro
        drawPaint.setAntiAlias(true); //Trazo suave, no completamente recta
        drawPaint.setStrokeWidth(20); //Ancho del pincel
        drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG); //Pintar difuminado
        relations = new ArrayList<>();
        caves = new ArrayList<>();
        touchX = 0;
        touchY = 0;
        totalCaves = 0;
        numCave = 0;
        maxCaves = 20;
    }

    //Tamaño asignado a la vista
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w,h,oldw,oldh);
        canvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawColor(Color.TRANSPARENT);//(0xFF000000); //Negro
    }

    //Pinta la vista, se llama desde el OnTouchEvent
    @Override
    protected void onDraw(Canvas drawCanvas){
        drawCanvas.drawBitmap(canvasBitmap,0,0,canvasPaint); //Poner un dibujo en memoria con ese formato
        //drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawPath(drawPath, drawPaint);
        invalidate();
    }

    //Registra los toques del usuario
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX = event.getX();
        touchY = event.getY();
        return true;
    }

    //Reinicializa la pantalla
    public void newDraw(){
        setupDrawing();
        drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    //Agrega una cueva
    public void addCave(){
        String tag;
        if(touchX > 0 && touchY > 0) {
            if (totalCaves < maxCaves) {
                drawPaint.setStrokeWidth(20);
                drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                drawPaint.setColor(0xFFFFFFFF);//Blanco //(0xFF000000); //Negro
                drawPath.addCircle(touchX, touchY, 50, Path.Direction.CW); //Dibujo una cueva en esas coordenadas
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                tag = Integer.toString(numCave);
                drawPaint.setStrokeWidth(3);
                drawPaint.setTextSize(30);
                drawPaint.setStyle(Paint.Style.STROKE);
                drawPaint.setColor(0xFF000000);//Negro //(0xFFFFFFFF); //Blanco
                drawCanvas.drawText(tag, touchX - 5, touchY + 5, drawPaint);
                caves.add(new Cave(numCave, touchX, touchY)); //Añado la nueva cueva a la lista
                totalCaves++;
                numCave++;
                invalidate();
            }
        }
    }

    //Elimina una cueva
    public void deleteCave(int c){ //Recibo el id de la cueva que deseo borrar
        String tag;
        Cave c1, c2;
        drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        //Borro todas las aristas relacionadas con esa cueva
        int l = 0;
        IntPair pair;
        while(l < relations.size()) {
            pair = relations.get(l);
            if (pair.x == c || pair.y == c){
                relations.remove(l);
            }
            else{
                l++;
            }
        }
        //Borro la cueva
        boolean found = false;
        int j = 0;
        while (j < caves.size() && !found)
        {
            if (caves.get(j).getId() == c) {
                caves.remove(j);
                totalCaves--;
                found = true;
            }
            else {
                j++;
            }
        }
        //Dibujo las demás cuevas
        int i = 0;
        while(i < caves.size()) {
            c1 = caves.get(i);
            touchX = c1.getCorX();
            touchY = c1.getCorY();
            drawPath.moveTo(touchX, touchY);
            drawPaint.setStrokeWidth(20);
            drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            drawPaint.setColor(0xFFFFFFFF);//Blanco //(0xFF000000); //Negro
            drawPath.addCircle(touchX, touchY, 50, Path.Direction.CW); //Dibujo una cueva
            drawCanvas.drawPath(drawPath, drawPaint); //Llama al onDraw
            drawPath.reset();
            tag = Integer.toString(c1.getId());
            drawPaint.setStrokeWidth(3);
            drawPaint.setTextSize(30);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setColor(0xFF000000);//(0xFFFFFFFF); //Color: Blanco
            drawCanvas.drawText(tag, touchX - 5, touchY + 5, drawPaint);
            i++;
        }
        //Dibujo los demás arcos
        int k = 0;
        while (k < relations.size()) {
            c1 = searchCave(relations.get(k).x);
            c2 = searchCave(relations.get(k).y);
            addArc(c1, c2);
            k++;
        }
        drawPath.reset();
        invalidate();
    }

    //Agrega un camino/arco entre 2 cuevas
    public void addArc(Cave c1, Cave c2){
        float x1,y1,x2,y2;
        String tag;
        x1 = c1.getCorX();
        y1 = c1.getCorY();
        x2 = c2.getCorX();
        y2 = c2.getCorY();
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        drawPaint.setColor(0xFFFFFFFF);//Blanco //(0xFF000000); /Negro
        drawPath.moveTo(x1,y1);
        drawPath.lineTo(x2,y2);
        drawCanvas.drawPath(drawPath, drawPaint);
        drawPath.reset();
        drawPaint.setStrokeWidth(3);
        drawPaint.setTextSize(30);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setColor(0xFF000000);//(0xFFFFFFFF); //Color: Blanco
        tag = Integer.toString(c1.getId());
        drawCanvas.drawText(tag, x1 - 5, y1 + 5, drawPaint);
        tag = Integer.toString(c2.getId());
        drawCanvas.drawText(tag, x2 - 5, y2 + 5, drawPaint);
        invalidate();
    }

    //Elimina un camino/arco entre 2 cuevas
    public void deleteArc(Cave c1, Cave c2){
        String tag;
        drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        //Borro los caminos entre esas cuevas
        int l = 0;
        IntPair pair;
        boolean found = false;
        while(l < relations.size() && !found) {
            pair = relations.get(l);
            if ((pair.x == c1.getId() && pair.y == c2.getId()) || (pair.y == c1.getId() && pair.x == c2.getId())){
                relations.remove(l);
                found = true;
            }
            else{
                l++;
            }
        }
        //Dibujo las cuevas
        int i = 0;
        while(i < caves.size()) {
            c1 = caves.get(i);
            touchX = c1.getCorX();
            touchY = c1.getCorY();
            drawPath.moveTo(touchX, touchY);
            drawPaint.setStrokeWidth(20);
            drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            drawPaint.setColor(0xFFFFFFFF);//Blanco //(0xFF000000); //Negro
            drawPath.addCircle(touchX, touchY, 50, Path.Direction.CW); //Dibujo una cueva
            drawCanvas.drawPath(drawPath, drawPaint); //Llama al onDraw
            drawPath.reset();
            tag = Integer.toString(c1.getId());
            drawPaint.setStrokeWidth(3);
            drawPaint.setTextSize(30);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setColor(0xFF000000);//(0xFFFFFFFF); //Color: Blanco
            drawCanvas.drawText(tag, touchX - 5, touchY + 5, drawPaint);
            i++;
        }
        //Dibujo los demás arcos
        int k = 0;
        while (k < relations.size()) {
            c1 = searchCave(relations.get(k).x);
            c2 = searchCave(relations.get(k).y);
            addArc(c1, c2);
            k++;
        }
        drawPath.reset();
        invalidate();
    }

    //Busca la cueva con el id especificado y la devuelve
    public Cave searchCave(int id){
        int i = 0;
        while(i < totalCaves){
            if (caves.get(i).getId() == id) {
                return caves.get(i);
            }
            else{
                i++;
            }
        }
        return null;
    }

    public ArrayList<IntPair> getRelations() {
        return relations;
    }

    public int getNumCave() {
        return numCave;
    }
}