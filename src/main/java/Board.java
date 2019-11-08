package main.java;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Board {

    private Gem[][] gems;
    private int sideLength;

    public Board(int sideLength) {
        this.sideLength = sideLength;
        gems = new Gem[sideLength][sideLength];
        for (int i = 0; i < gems.length; i++) {
            for (int j = 0; j < gems[0].length; j++) {
                gems[i][j] = new Gem(i, j, 0);
            }
        }
    }

    public void draw(GraphicsContext gc, Image image) {
        for (int i = 0; i < gems.length; i++) {
            for (int j = 0; j < gems[0].length; j++) {
                gems[i][j].draw(gc, image);
            }
        }
    }
}
