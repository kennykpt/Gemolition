package main.java;

import javafx.scene.canvas.GraphicsContext;

public class Gem {

    private int row;
    private int col;
    private int beforeSwapX;
    private int beforeSwapY;
    private GemType type;
    private boolean inMatchGroup = false;
    private boolean animating = false;

    public Gem(int row, int col, GemType type) {
        this.row = row;
        this.col = col;
        this.type = type;
        beforeSwapX = getX();
        beforeSwapY = getY();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRowAndCol(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getX() {
        return col * Constants.GEM_PIXEL_LENGTH;
    }

    public int getY() {
        return row * Constants.GEM_PIXEL_LENGTH;
    }

    public int getBeforeSwapX() {
        return beforeSwapX;
    }

    public void setBeforeSwapX(int beforeSwapX) {
        this.beforeSwapX = beforeSwapX;
    }

    public int getBeforeSwapY() {
        return beforeSwapY;
    }

    public void setBeforeSwapY(int beforeSwapY) {
        this.beforeSwapY = beforeSwapY;
    }

    public void setBeforeSwapXAndY() {
        this.beforeSwapX = getX();
        this.beforeSwapY = getY();
    }

    public GemType getType() {
        return type;
    }

    public boolean isInMatchGroup() {
        return inMatchGroup;
    }

    public void setInMatchGroup(boolean inMatchGroup) {
        this.inMatchGroup = inMatchGroup;
    }

    public boolean isAnimating() {
        return animating;
    }

    public void setAnimating(boolean animating) {
        this.animating = animating;
    }

    public void draw(GraphicsContext gc) {
        gc.drawImage(GemType.getImage(type), getBeforeSwapX(), getBeforeSwapY());
    }

    public void removeGem(GraphicsContext gc) {
        gc.clearRect(getX(), getY(), Constants.GEM_PIXEL_LENGTH, Constants.GEM_PIXEL_LENGTH);
    }
}
