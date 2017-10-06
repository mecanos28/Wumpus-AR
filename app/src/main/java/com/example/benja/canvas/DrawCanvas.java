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
    private Graph customMaze; //Implementación del grafo
    private float touchX, touchY; //Coordenadas
    private boolean active; //Modo: agregar cueva
    private int numCaves; //Contador del número de cuevas
    private ArrayList<IntPair> relations; //Array que almacena todas las relaciones existentes

    public DrawCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    //Configuracion del area donde se va a dibujar
    private void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(0xFF000000); //Color inicial del pincel: Negro
        drawPaint.setAntiAlias(true); //Trazo suave, no completamente recta
        drawPaint.setStrokeWidth(20); //Ancho del pincel
        drawPaint.setStyle(Paint.Style.FILL_AND_STROKE); //Pintar bordes o trazos
        drawPaint.setStrokeJoin(Paint.Join.ROUND); //Pintura redondeada
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG); //Permite pintar difuminado
        customMaze = new Graph();
        touchX = 0;
        touchY = 0;
        active = false;
        numCaves = 0;
        relations = new ArrayList<IntPair>();
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
        setupDrawing();
    }

    //Pinta la vista, se llama desde el OnTouchEvent
    @Override
    protected void onDraw(Canvas drawCanvas){
        drawCanvas.drawBitmap(canvasBitmap,0,0,canvasPaint); //Poner un dibujo en memoria con ese formato
        drawCanvas.drawPath(drawPath, drawPaint);
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
                    customMaze.addCave(new Cave(numCaves, touchX, touchY)); //La añado al grafo
                    numCaves++;
                    drawPath.reset();
                    break;
                default:
                    return false;
            }
            invalidate();
        }
        return true;
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
        relations.add(new IntPair(c1.getId(),c2.getId()));
        relations.add(new IntPair(c2.getId(),c1.getId()));
    }

    //Borra una cueva
    public void deleteCave(int cave){ //Recibo la posicion de la cueva que deseo borrar
        if(cave < customMaze.getMaximumCaves()) {
            Cave specifiedCave = customMaze.searchCave(cave);
            drawPaint.setStrokeWidth(20);
            drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            drawPaint.setColor(0xFFFFFFFF); //Color: Blanco
            drawPath.addCircle(specifiedCave.getCorX(), specifiedCave.getCorY(), 52, Path.Direction.CW);
            drawCanvas.drawPath(drawPath, drawPaint);
            drawPath.reset();
            customMaze.removeCave(cave); //La borro del grafo
            numCaves--;
            for (int i = 0; i < relations.size(); ++i)
            {
                if (relations.get(i).x == cave || relations.get(i).y == cave) {
                    relations.remove(i);
                    //Borrar del dibujo también
                }
            }
        }
    }

    //Borra un arco entre 2 cuevas
    public void deleteArc(int cave1, int cave2){
        Cave c1 = customMaze.searchCave(cave1);
        Cave c2 = customMaze.searchCave(cave2);
        if(c1 != null && c2 != null)
        {
            drawPaint.setStrokeWidth(25);
            drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            drawPaint.setColor(0xFFFFFFFF);
            drawPath.moveTo(c1.getCorX()-56,c1.getCorY());
            drawPath.lineTo(c2.getCorX(),c2.getCorY()-52);
            drawCanvas.drawPath(drawPath, drawPaint);
            drawPath.reset();
            //customMaze.remove_Bi_Relation(cave1,cave2);
        }
    }

    public Graph getCustomMaze() {
        return customMaze;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
