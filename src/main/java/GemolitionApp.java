package main.java;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

public class GemolitionApp extends Application {

    private GameFlow gameFlow;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        int widthInPixels = Constants.BOARD_WIDTH * Constants.GEM_PIXEL_LENGTH;
        int heightInPixels = Constants.BOARD_HEIGHT * Constants.GEM_PIXEL_LENGTH;

        Canvas canvas = new Canvas(widthInPixels, heightInPixels);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        GameFlow gameFlow = new GameFlow();
        gameFlow.draw(gc);

        Group group = new Group();
        group.getChildren().add(canvas);
        Scene scene = new Scene(group, widthInPixels, heightInPixels);
        scene.setOnMousePressed(e -> {
            int row = (int) e.getY() / Constants.GEM_PIXEL_LENGTH;
            int col = (int) e.getX() / Constants.GEM_PIXEL_LENGTH;
            if (GameFlow.isInsideBoard(row, col)) {
                gameFlow.mousePressed(row, col);
            }
        });
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, Constants.BOARD_PIXEL_WIDTH, Constants.BOARD_PIXEL_HEIGHT);
                gameFlow.draw(gc);
            }
        }.start();

        primaryStage.setTitle("Gemolition");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
