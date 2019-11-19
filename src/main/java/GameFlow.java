package main.java;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

// Use later for other features
public class GameFlow {

    private Board board;
    private Animation animation;
    private boolean animating = false;

    public GameFlow() {
        board = new Board(Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);
    }

    public Board getBoard() {
        return board;
    }

    public void draw(GraphicsContext gc) {
        board.draw(gc);
    }

    public void mousePressed(int row, int col) {
        if (!animating && board.mousePressed(row, col)) {
            animating = true;
            animate();
        }
    }

    public void animate() {
        animation = new Timeline(new KeyFrame(Duration.millis(5), e -> {
            Set<Gem> gems = Arrays.stream(board.getGems())
                    .flatMap(Arrays::stream)
                    .filter(Gem::isAnimating)
                    .collect(Collectors.toSet());

            for (Iterator<Gem> it = gems.iterator(); it.hasNext(); ) {
                Gem gem = it.next();
                if (gem.getBeforeSwapX() < gem.getX())
                    gem.setBeforeSwapX(gem.getBeforeSwapX() + 1);
                else if (gem.getBeforeSwapX() > gem.getX())
                    gem.setBeforeSwapX(gem.getBeforeSwapX() - 1);
                else if (gem.getBeforeSwapY() < gem.getY())
                    gem.setBeforeSwapY(gem.getBeforeSwapY() + 1);
                else if (gem.getBeforeSwapY() > gem.getY())
                    gem.setBeforeSwapY(gem.getBeforeSwapY() - 1);
                else {
                    gem.setAnimating(false);
                    it.remove();
                }
            }

            if (gems.isEmpty()) {
                animation.stop();
                if (animating)
                    handleMatches();
            }
        }));
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();
    }

    public void handleMatches() {
        animating = false;
        board.getSelectedGem().setAnimating(true);
        board.getSelectedGem2().setAnimating(true);
        board.swap(board.getSelectedGem(), board.getSelectedGem2());
        animate();
        board.resetSelectedGems();
    }

    public static boolean isInsideBoard(int row, int col) {
        return row >= 0 && row <= Constants.BOARD_HEIGHT && col >= 0 && col <= Constants.BOARD_WIDTH;
    }
}
