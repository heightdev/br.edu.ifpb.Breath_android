package br.edu.ifpb.breath.entities;

import com.google.gson.Gson;

/**
 * This class represents a point.
 *
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class Point {
    public int x;
    public float y;

    /**
     * Unreachable constructor method.
     */
    private Point(){}

    /**
     * Constructor method.
     * @param x - X coordinate.
     * @param y - Y coordinate.
     */
    public Point(int x, float y){
        this.x = x;
        this.y = y;
    }

    /**
     * Gets X coordinate.
     * @return - X coordinate.
     */
    public int getX(){
        return x;
    }

    /**
     * Gets Y coordinate.
     * @return - Y coordinate.
     */
    public float getY(){
        return y;
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Point))
            return false;

        Point pt = (Point) obj;
        return (pt.getX() == x && pt.getY() == y);
    }

    @Override
    public String toString(){
        return new Gson().toJson(this);
    }

    @Override
    public int hashCode(){
        return toString().hashCode();
    }
}
