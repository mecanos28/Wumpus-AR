package com.example.benja.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
    IntPair(int x, int y) {this.x=x;this.y=y;}
}

public class DrawCanvas extends View {

    private Path drawPath; //Guardo el trazo
    private Paint drawPaint, canvasPaint; //Pincel
    private Canvas drawCanvas; //Lienzo
    private Bitmap canvasBitmap; //Para guardar
    private ArrayList<IntPair> relations; //Array que almacena todas las relaciones existentes
    private ArrayList<Cave> caves; //Array que almacena todas las cuevas existentes
    private float touchX, touchY; //Coordenadas
    private int numCaves; //Contador del número de cuevas

    public DrawCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    //Configuracion del area donde se va a dibujar
    public void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(0xFF000000); //Color inicial del pincel: Negro
        drawPaint.setAntiAlias(true); //Trazo suave, no completamente recta
        drawPaint.setStrokeWidth(20); //Ancho del pincel
        drawPaint.setStyle(Paint.Style.FILL_AND_STROKE); //Pintar bordes o trazos
        drawPaint.setStrokeJoin(Paint.Join.ROUND); //Pintura redondeada
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG); //Permite pintar difuminado
        relations = new ArrayList<IntPair>();
        caves = new ArrayList<Cave>();
        touchX = 0;
        touchY = 0;
        numCaves = 0;
    }

    //Tamaño asignado a la vista
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w,h,oldw,oldh);
        canvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    //Reinicializa la pantalla
    public void newDraw(){
        drawCanvas.drawColor(0,PorterDuff.Mode.CLEAR);
        invalidate();
    }

    //Pinta la vista, se llama desde el OnTouchEvent
    @Override
    protected void onDraw(Canvas drawCanvas){
        drawCanvas.drawBitmap(canvasBitmap,0,0,canvasPaint); //Poner un dibujo en memoria con ese formato
        drawCanvas.drawPath(drawPath, drawPaint);
        invalidate();
    }

    //Registra los toques del usuario
    //Agrega una cueva
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX = event.getX();
        touchY = event.getY();
        if (numCaves < 20) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawPath.moveTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    drawPaint.setStrokeWidth(20);
                    drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    drawPaint.setColor(0xFF000000); //Color: Negro
                    drawPath.addCircle(touchX, touchY, 50, Path.Direction.CW); //Dibujo una cueva en negro en esas coordenadas
                    drawCanvas.drawPath(drawPath, drawPaint); //Llama al onDraw
                    String tag = Integer.toString(numCaves);
                    drawPaint.setStrokeWidth(3);
                    drawPaint.setTextSize(30);
                    drawPaint.setStyle(Paint.Style.STROKE);
                    drawPaint.setColor(0xFFFFFFFF); //Color: Blanco
                    drawCanvas.drawText(tag, touchX - 5, touchY + 5, drawPaint);
                    drawPath.reset();
                    caves.add(numCaves, new Cave(numCaves, touchX, touchY)); //La añado al grafo
                    numCaves++;
                    break;
                default:
                    return false;
            }
            invalidate();
        }
        return true;
    }

    //Borra una cueva
    public void deleteCave(int c){ //Recibo la posicion de la cueva que deseo borrar
        /*String tag;
        Cave c1, c2;
        if(c < numCaves) {
            Cave cave = searchCave(c);
            //Borro todas las aristas relacionadas con esa cueva
            for (int i = 0; i < relations.size(); ++i) {
                if (relations.get(i).x == c || relations.get(i).y == c) {
                    relations.remove(i);
                }
            }
            //Borro la cueva
            caves.remove(cave);
            numCaves--;
            //Dibujo las demás cuevas
            int i = 0;
            while (caves.get(i) != null) {
                drawPaint.setStrokeWidth(20);
                drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                drawPaint.setColor(0xFF000000); //Color: Negro
                touchX = caves.get(i).getCorX();
                touchY = caves.get(i).getCorY();
                drawPath.addCircle(touchX, touchY, 50, Path.Direction.CW); //Dibujo una cueva
                tag = Integer.toString(numCaves);
                drawPaint.setStrokeWidth(3);
                drawPaint.setTextSize(30);
                drawPaint.setStyle(Paint.Style.STROKE);
                drawPaint.setColor(0xFFFFFFFF); //Color: Blanco
                drawCanvas.drawText(tag, touchX - 5, touchY + 5, drawPaint);
            }
            drawCanvas.drawPath(drawPath, drawPaint); //Llama al onDraw
            drawPath.reset();
            for (int j = 0; j < relations.size(); ++j) {
                c1 = searchCave(relations.get(i).x);
                c2 = searchCave(relations.get(i).y);
                addArc(c1, c2);
            }
        }*/

        Cave cave = searchCave(c);
        //Borro todas las aristas relacionadas con esa cueva
        for (int i = 0; i < relations.size(); ++i)
        {
            if (relations.get(i).x == c || relations.get(i).y == c) {
                Cave c1 = searchCave(relations.get(i).x);
                Cave c2 = searchCave(relations.get(i).y);
                deleteArc(c1,c2);
                relations.remove(i);
            }
        }
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        drawPaint.setColor(0xFFFFFFFF); //Color: Blanco
        drawPath.addCircle(cave.getCorX(), cave.getCorY(), 52, Path.Direction.CW);
        drawCanvas.drawPath(drawPath, drawPaint);
        drawPath.reset();
        //Borro la cueva
        /*boolean found = false;
        int j = 0;
        while (j < numCaves && !found)
        {
            if (cave.getId() == c) {
                caves.remove(j);
                found = true;
            }
            j++;
        }*/
        caves.remove(cave);
        numCaves--;
    }

    //Agrega un arco entre 2 cuevas
    public void addArc(Cave c1, Cave c2){
        float x1,y1,x2,y2;
        x1 = c1.getCorX();
        y1 = c1.getCorY();
        x2 = c2.getCorX();
        y2 = c2.getCorY();

        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        drawPaint.setColor(0xFF000000);     //Color: Negro

        //Acomodar coordenadas desde la Cueva 1
        if(y1 < y2){                       //Si el arco se dibuja hacia abajo
            if(x1 < x2){                   //Si es hacia la derecha
                drawPath.moveTo(x1,y1+50);
            }
            else {                         //Si es hacia la izquierda
                drawPath.moveTo(x1,y1+50);
            }
        }
        else {                             //Si el arco se dibuja hacia arriba
            if(x1 < x2){                   //Si es hacia la derecha
                drawPath.moveTo(x1,y1-50);
            }
            else {                         //Si es hacia la izquierda
                drawPath.moveTo(x1,y1-50);
            }
        }
        //Acomodar coordenadas hasta la Cueva 2
        if(y1 > y2){                       //Si el arco viene de abajo
            if(x1 < x2){                   //Si viene de la izquierda
                drawPath.lineTo(x2,y2+50);
            }
            else {                         //Si viene de la derecha
                drawPath.lineTo(x2,y2+50);
            }
        }
        else {                             //Si el arco viene de arriba
            if(x1 < x2){                   //Si viene de la izquierda
                drawPath.lineTo(x2,y2-50);
            }
            else {                         //Si viene de la derecha
                drawPath.lineTo(x2,y2-50);
            }
        }
        drawCanvas.drawPath(drawPath, drawPaint);
        drawPath.reset();
        invalidate();
    }

    //Borra un arco entre 2 cuevas
    public void deleteArc(Cave c1, Cave c2){
        float x1,y1,x2,y2;
        x1 = c1.getCorX();
        y1 = c1.getCorY();
        x2 = c2.getCorX();
        y2 = c2.getCorY();

        drawPaint.setStrokeWidth(25);
        drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        drawPaint.setColor(0xFFFFFFFF);     //Color: Blanco

        //Acomodar coordenadas desde la Cueva 1
        if(y1 < y2){                       //Si el arco se dibuja hacia abajo
            if(x1 < x2){                   //Si es hacia la derecha
                drawPath.moveTo(x1,y1+50);
            }
            else {                         //Si es hacia la izquierda
                drawPath.moveTo(x1,y1+50);
            }
        }
        else {                             //Si el arco se dibuja hacia arriba
            if(x1 < x2){                   //Si es hacia la derecha
                drawPath.moveTo(x1,y1-50);
            }
            else {                         //Si es hacia la izquierda
                drawPath.moveTo(x1,y1-50);
            }
        }
        //Acomodar coordenadas hasta la Cueva 2
        if(y1 > y2){                       //Si el arco viene de abajo
            if(x1 < x2){                   //Si viene de la izquierda
                drawPath.lineTo(x2,y2+50);
            }
            else {                         //Si viene de la derecha
                drawPath.lineTo(x2,y2+50);
            }
        }
        else {                             //Si el arco viene de arriba
            if(x1 < x2){                   //Si viene de la izquierda
                drawPath.lineTo(x2,y2-50);
            }
            else {                         //Si viene de la derecha
                drawPath.lineTo(x2,y2-50);
            }
        }
        drawCanvas.drawPath(drawPath, drawPaint);
        drawPath.reset();
        invalidate();
    }

    //Busca la cueva con el id especificado
    public Cave searchCave(int id){
        boolean found = false;
        int i = 0;
        while(i < numCaves && !found){
            if (caves.get(i).getId() == id) {
                found = true;
            }
            else{
                i++;
            }
        }
        return caves.get(i);
    }

    public ArrayList<IntPair> getRelations() {
        return relations;
    }

    public int getNumCaves() {
        return numCaves;
    }
}
