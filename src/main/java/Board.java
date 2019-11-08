package main.java;

import javafx.scene.canvas.GraphicsContext;

public class Board {

    private Gem[][] gems;

    public Board(int boardWidth, int boardHeight) {
        gems = new Gem[boardWidth][boardHeight];
        populateBoard();
    }

    public void populateBoard() {
        for (int i = 0; i < gems.length; i++)
            for (int j = 0; j < gems[0].length; j++)
                gems[i][j] = new Gem(i, j, GemType.getRandomType());
    }

    public void draw(GraphicsContext gc) {
        for (int i = 0; i < gems.length; i++) {
            for (int j = 0; j < gems[0].length; j++) {
                gems[i][j].draw(gc);
            }
        }
    }
}
