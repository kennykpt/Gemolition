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

        Board board = new Board(widthInPixels, heightInPixels);
        board.draw(gc);

        Group group = new Group();
        group.getChildren().add(canvas);

        primaryStage.setTitle("Gemolition");
        primaryStage.setScene(new Scene(group, widthInPixels, heightInPixels));
        primaryStage.show();
    }
}
