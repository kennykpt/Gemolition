package main.java;

import javafx.scene.canvas.GraphicsContext;

public class Gem {

    private int row;
    private int col;
    private GemType type;

    public Gem(int row, int col, GemType type) {
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

    public GemType getType() {
        return type;
    }

    public void setType(GemType type) {
        this.type = type;
    }

    public void draw(GraphicsContext gc) {
        int x = col * Constants.GEM_PIXEL_LENGTH;
        int y = row * Constants.GEM_PIXEL_LENGTH;
        gc.drawImage(GemType.getImage(type), x, y);
    }

    public void removeGem(GraphicsContext gc) {
        int x = col * Constants.GEM_PIXEL_LENGTH;
        int y = row * Constants.GEM_PIXEL_LENGTH;
        gc.clearRect(x, y, Constants.GEM_PIXEL_LENGTH, Constants.GEM_PIXEL_LENGTH);
    }
}
