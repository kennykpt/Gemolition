package main.java;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;

public class GemolitionApp extends Application {

    private static int N_GEM_LENGTH = 15;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        int size = N_GEM_LENGTH * Gem.gemPixelLength;
        Canvas canvas = new Canvas(size, size);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Board board = new Board(N_GEM_LENGTH);
        URL url = ClassLoader.getSystemClassLoader().getResource("main/resources/Random_Gem.jpg");
        Image image = new Image(url.toString());
        board.draw(gc, image);

        Group root = new Group();
        root.getChildren().add(canvas);

        Scene scene = new Scene(root, size, size);

        primaryStage.setTitle("Gemolition");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
