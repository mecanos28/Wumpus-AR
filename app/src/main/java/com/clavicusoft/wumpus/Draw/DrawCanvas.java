package com.clavicusoft.wumpus.Draw;

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

import com.clavicusoft.wumpus.Maze.Cave;

import java.util.ArrayList;

public class DrawCanvas extends View {
    private Path drawPath; //Saves de drawing path
    private Paint drawPaint, canvasPaint; //Drawing brush
    private Canvas drawCanvas; //Canvas
    private Bitmap canvasBitmap; //Stores canvas bit state
    private ArrayList<IntPair> relations; //Stores all current relations

    public ArrayList<Cave> getCaves() {
        return caves;
    }

    private ArrayList<Cave> caves; //Stores all current caves
    private float touchX, touchY; //Stores coordinates
    private int numCave; //Counter to assign an ID to each cave

    public int getTotalCaves() {
        return totalCaves;
    }

    private int totalCaves; //Counter of drawn caves
    private int maxCaves; //Maximun number of caves allowed

    /**
     * Constructor of the canvas
     * @param context Context to access DrawCanvas class
     * @param attrs AttributeSet of the DrawCanvas configurations
     */
    public DrawCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    /**
     * Configuration of the area used to draw.
     */
    public void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(0xFFFFFFFF); //White
        drawPaint.setAntiAlias(true); //Soft brush
        drawPaint.setStrokeWidth(20); //Brush width
        drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG); //Difuminated draw
        relations = new ArrayList<>();
        caves = new ArrayList<>();
        touchX = 0;
        touchY = 0;
        totalCaves = 0;
        numCave = 0;
        maxCaves = 20;
    }

    /**
     * Size given to the drawing area
     * @param w width of the area after change
     * @param h height of the area after change
     * @param oldw old width of the area changed
     * @param oldh old height of the area changed
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w,h,oldw,oldh);
        canvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawColor(Color.TRANSPARENT);//(0xFF000000); //Black
    }

    /**
     * Draws to the canvas, used from OnTouchEvent
     * @param drawCanvas receives the canvas to be affected by the drawing
     */
    @Override
    protected void onDraw(Canvas drawCanvas){
        drawCanvas.drawBitmap(canvasBitmap,0,0,canvasPaint); //Puts the drawing in memory in this format
        //drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawPath(drawPath, drawPaint);
        invalidate();
    }

    /**
     * Registers the user´s touch events
     * @param event The event of a touch in the canvas
     * @return Always returns true for a touch
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX = event.getX();
        touchY = event.getY();
        return super.onTouchEvent(event);
    }

    /**
     * Restarts the drawing
     */
    public void newDraw(){
        setupDrawing();
        drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    /**
     * Adds a cave, using coordinates and giving it a unique ID
     */
    public void addCave(){
        String tag;
        if(touchX > 0 && touchY > 0) {
            if (totalCaves < maxCaves) {
                drawPaint.setStrokeWidth(20);
                drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                drawPaint.setColor(0xFFFFFFFF);//White
                drawPath.addCircle(touchX, touchY, 50, Path.Direction.CW); //Draw the cave in these coordinates
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                tag = Integer.toString(numCave);
                drawPaint.setStrokeWidth(3);
                drawPaint.setTextSize(30);
                drawPaint.setStyle(Paint.Style.STROKE);
                drawPaint.setColor(0xFF000000);//Black
                drawCanvas.drawText(tag, touchX - 5, touchY + 5, drawPaint); //Draw the cave ID
                caves.add(new Cave(numCave, touchX, touchY)); //Add cave to the list
                totalCaves++;
                numCave++;
                invalidate();
            }
        }
    }

    /**
     * Deletes a cave
     * @param c Cave to delete
     */
    public void deleteCave(int c){
        String tag;
        Cave c1, c2;
        c1 = searchCaveByCoordinates(touchX, touchY); //Receive new specified coordinates
        c = c1.getId();
        drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        //Delete all arcs related to this cave
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
        //Delete the cave
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
        //Redraw all the other caves
        int i = 0;
        while(i < caves.size()) {
            c1 = caves.get(i);
            touchX = c1.getCorX();
            touchY = c1.getCorY();
            drawPath.moveTo(touchX, touchY);
            drawPaint.setStrokeWidth(20);
            drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            drawPaint.setColor(0xFFFFFFFF);//White
            drawPath.addCircle(touchX, touchY, 50, Path.Direction.CW);
            drawCanvas.drawPath(drawPath, drawPaint);
            drawPath.reset();
            tag = Integer.toString(c1.getId());
            drawPaint.setStrokeWidth(3);
            drawPaint.setTextSize(30);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setColor(0xFF000000);//Black
            drawCanvas.drawText(tag, touchX - 5, touchY + 5, drawPaint);
            i++;
        }
        //Redraw all other arches
        int k = 0;
        while (k < relations.size()) {
            c1 = searchCaveById(relations.get(k).x);
            c2 = searchCaveById(relations.get(k).y);
            addArc(c1, c2);
            k++;
        }
        drawPath.reset();
        invalidate();
    }

    /**
     * Adds an edge between two caves
     * @param c1 First cave
     * @param c2 Second cave
     */
    public void addArc(Cave c1, Cave c2){
        float x1,y1,x2,y2;
        String tag;
        x1 = c1.getCorX();
        y1 = c1.getCorY();
        x2 = c2.getCorX();
        y2 = c2.getCorY();
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        drawPaint.setColor(0xFFFFFFFF);//White
        drawPath.moveTo(x1,y1);
        drawPath.lineTo(x2,y2);
        drawCanvas.drawPath(drawPath, drawPaint);
        drawPath.reset();
        drawPaint.setStrokeWidth(3);
        drawPaint.setTextSize(30);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setColor(0xFF000000);//Black
        //Redraw the cave ID
        tag = Integer.toString(c1.getId());
        drawCanvas.drawText(tag, x1 - 5, y1 + 5, drawPaint);
        tag = Integer.toString(c2.getId());
        drawCanvas.drawText(tag, x2 - 5, y2 + 5, drawPaint);
        invalidate();
    }

    /**
     * Deletes an edge between two caves
     * @param c1 First cave
     * @param c2 Second cave
     */
    public void deleteArc(Cave c1, Cave c2){
        String tag;
        drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        //Deletes the edges between both caves
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
        //Redraw the caves
        int i = 0;
        while(i < caves.size()) {
            c1 = caves.get(i);
            touchX = c1.getCorX();
            touchY = c1.getCorY();
            drawPath.moveTo(touchX, touchY);
            drawPaint.setStrokeWidth(20);
            drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            drawPaint.setColor(0xFFFFFFFF);//White
            drawPath.addCircle(touchX, touchY, 50, Path.Direction.CW);
            drawCanvas.drawPath(drawPath, drawPaint);
            drawPath.reset();
            tag = Integer.toString(c1.getId());
            drawPaint.setStrokeWidth(3);
            drawPaint.setTextSize(30);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setColor(0xFF000000);//Black
            drawCanvas.drawText(tag, touchX - 5, touchY + 5, drawPaint);
            i++;
        }
        //Redraw the edges
        int k = 0;
        while (k < relations.size()) {
            c1 = searchCaveById(relations.get(k).x);
            c2 = searchCaveById(relations.get(k).y);
            addArc(c1, c2);
            k++;
        }
        drawPath.reset();
        invalidate();
    }

    /**
     * Searches the cave with the id and returns it
     * @param id Cave identifier
     * @return Cave with the corresponding id
     */
    public Cave searchCaveById(int id){
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

    /**
     * Searches the cave based on the received coordenates and returns it
     * @param x coordinates for axis x
     * @param y coordinates for axis y
     * @return Cave with the corresponding coordinates
     */
    public Cave searchCaveByCoordinates(float x, float y){
        float currentX, currentY;
        int i = 0;
        while(i < totalCaves) {
            currentX = caves.get(i).getCorX();
            currentY = caves.get(i).getCorY();
            if ((x >= currentX-50 && y >= currentY-50)|| (x <= currentX+50 && y >= currentY-50) || (x >= currentX-50 && y <= currentY+50) || (x >= currentX+50 && y <= currentY+50))
            {
                return caves.get(i);
            }
            else{
                i++;
            }
        }
        return null;
    }

    /**
     * Returns the relations in the current graph
     * @return relations
     */
    public ArrayList<IntPair> getRelations() {
        return relations;
    }

    /**
     * Returns the current cave number for naming caves
     * @return numCave
     */
    public int getNumCave() {
        return numCave;
    }

    public void managePressedCave()
    {
        Cave c = searchCaveByCoordinates(touchX,touchY);
        if (c == null)
        {
            addCave();
        }
        else {
            deleteCave(c.getId());
        }
    }
}