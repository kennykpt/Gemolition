package main.java;

import javafx.scene.canvas.GraphicsContext;

public class Board {

    private Gem[][] gems;

    public Board(int boardWidth, int boardHeight) {
        gems = new Gem[boardWidth][boardHeight];
        populateBoard();
    }

    // Populates the board by placing gems from left to right, top to bottom
    public void populateBoard() {
        for (int i = 0; i < gems.length; i++) {
            for (int j = 0; j < gems[0].length; j++) {
                gems[i][j] = new Gem(i, j, GemType.getRandomType());
                GemType current = gems[i][j].getType();

                // Check left
                if (j >= 2) {
                    GemType left = gems[i][j - 1].getType();
                    GemType leftTwo = gems[i][j - 2].getType();
                    if (current == left && current == leftTwo) {
                        gems[i][j] = new Gem(i, j, GemType.getRandomType());
                        j--;
                    }
                }
                // Check above
                if (i >= 2) {
                    GemType up = gems[i - 1][j].getType();
                    GemType upTwo = gems[i - 2][j].getType();
                    if (current == up && current == upTwo) {
                        gems[i][j] = new Gem(i, j, GemType.getRandomType());
                        j--;
                    }
                }
            }
        }
    }

    public void draw(GraphicsContext gc) {
        for (int i = 0; i < gems.length; i++)
            for (int j = 0; j < gems[0].length; j++)
                gems[i][j].draw(gc);
    }
}
