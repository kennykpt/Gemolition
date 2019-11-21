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
    private boolean matchFlag = false;

    public GameFlow() {
        board = new Board(Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);
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

            int delta = Constants.ANIMATION_PIXEL_CHANGE;
            for (Iterator<Gem> it = gems.iterator(); it.hasNext(); ) {
                Gem gem = it.next();
                if (gem.getBeforeSwapX() < gem.getX())
                    gem.setBeforeSwapX(gem.getBeforeSwapX() + delta);
                else if (gem.getBeforeSwapX() > gem.getX())
                    gem.setBeforeSwapX(gem.getBeforeSwapX() - delta);
                else if (gem.getBeforeSwapY() < gem.getY())
                    gem.setBeforeSwapY(gem.getBeforeSwapY() + delta);
                else if (gem.getBeforeSwapY() > gem.getY())
                    gem.setBeforeSwapY(gem.getBeforeSwapY() - delta);
                else {
                    gem.setAnimating(false);
                    it.remove();
                }
            }

            if (gems.isEmpty()) {
                animation.stop();
                if (animating)
                    handleMatchGroups();
            }
        }));
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();
    }

    public void handleMatchGroups() {
        board.formGemGroups();
        Set<GemMatchGroup> gemMatchGroups = board.getAllMatchGroups();
        if (!gemMatchGroups.isEmpty()) {
            matchFlag = true;
            board.clearMatchedGems();
            animate();
        } else {
            // If gemMatchGroups is empty and no prior match animations were played, then swap the gems back
            if (!matchFlag)
                board.swap(board.getSelectedGem(), board.getSelectedGem2());
            animating = false;
            animate();  // Since animating is false, handleMatchGroups() won't be triggered again

            matchFlag = false;
            board.resetSelectedGems();
        }
    }

    public static boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < Constants.BOARD_HEIGHT && col >= 0 && col < Constants.BOARD_WIDTH;
    }
}
