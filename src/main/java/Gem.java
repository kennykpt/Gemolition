package main.java;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Gem {

    public static final int gemPixelLength = 50;

    private int row;
    private int col;
    private int type;

    public Gem(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Gem(int row, int col, int type) {
        this.row = row;
        this.col = col;
        this.type = type;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getType() {
        return type;
    }

    public void draw(GraphicsContext gc, Image image) {
        gc.drawImage(image, row * gemPixelLength, col * gemPixelLength);
    }
}
