package main.java;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

public class GemolitionApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        int widthInPixels = Constants.BOARD_WIDTH * Constants.GEM_PIXEL_LENGTH;
        int heightInPixels = Constants.BOARD_HEIGHT * Constants.GEM_PIXEL_LENGTH;

        Canvas canvas = new Canvas(widthInPixels, heightInPixels);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        GameElements gameElements = new GameElements();
        gameElements.draw(gc);

        Group group = new Group();
        group.getChildren().add(canvas);
        Scene scene = new Scene(group, widthInPixels, heightInPixels);
        scene.setOnMouseClicked(e -> {
            int row = (int) e.getY() / Constants.GEM_PIXEL_LENGTH;
            int col = (int) e.getX() / Constants.GEM_PIXEL_LENGTH;
            if (Board.isInsideBoard(row, col)) {
                Board board = gameElements.getBoard();
                board.clickBoard(row, col);
                board.drawSwap(gc);
            }
        });

        primaryStage.setTitle("Gemolition");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
