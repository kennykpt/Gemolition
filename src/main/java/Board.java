package main.java;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private Gem[][] gems;
    private List<Gem> gemsToSwap = new ArrayList<>();
    private Gem selectedGem = null;

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

    public void drawSwap(GraphicsContext gc) {
        for (Gem gem : gemsToSwap) {
            gems[gem.getRow()][gem.getCol()].removeGem(gc);
            gems[gem.getRow()][gem.getCol()].draw(gc);
        }
    }

    // General swap algorithm for GemType, does not depend on neighbors
    public void swap(Gem gem, Gem otherGem) {
        GemType tempType = gems[gem.getRow()][gem.getCol()].getType();
        gems[gem.getRow()][gem.getCol()].setType(otherGem.getType());
        gems[otherGem.getRow()][otherGem.getCol()].setType(tempType);
    }

    public boolean isNeighbor4(Gem gem, Gem otherGem) {
        if (gem.getRow() == otherGem.getRow())
            return Math.abs(gem.getCol() - otherGem.getCol()) == 1;
        else if (gem.getCol() == otherGem.getCol())
            return Math.abs(gem.getRow() - otherGem.getRow()) == 1;
        return false;
    }

    public void clickBoard(int row, int col) {
        if (selectedGem == null)
            selectedGem = gems[row][col];
        else {
            // If selectedGem2 is not a neighbor, then reselect
            Gem selectedGem2 = gems[row][col];
            if (!isNeighbor4(selectedGem, selectedGem2))
                selectedGem = gems[row][col];
            else {
                swap(selectedGem, selectedGem2);
                gemsToSwap.add(gems[selectedGem.getRow()][selectedGem.getCol()]);
                gemsToSwap.add(gems[selectedGem2.getRow()][selectedGem2.getCol()]);
                // Check for a match before swapping back
                selectedGem = null;
            }
        }
    }

    public static boolean isInsideBoard(int row, int col) {
        return row >= 0 && row <= Constants.BOARD_HEIGHT && col >= 0 && col <= Constants.BOARD_WIDTH;
    }
}
