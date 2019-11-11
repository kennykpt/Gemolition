package main.java;

import javafx.scene.canvas.GraphicsContext;

// Use later for other features
public class GameElements {

    private Board board;

    public GameElements() {
        board = new Board(Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);
    }

    public Board getBoard() {
        return board;
    }

    public void draw(GraphicsContext gc) {
        board.draw(gc);
    }
}
