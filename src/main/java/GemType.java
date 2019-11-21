package main.java;

import javafx.scene.image.Image;

import java.net.URL;
import java.util.EnumMap;
import java.util.Random;

public enum GemType {
    RED("Red_Gem"),
    ORANGE("Orange_Gem"),
    YELLOW("Yellow_Gem"),
    GREEN("Green_Gem"),
    BLUE("Blue_Gem"),
    WHITE("White_Gem"),
    PURPLE("Purple_Gem");

    private static Random rand = new Random();
    private static EnumMap<GemType, Image> gemTypeImage;
    private final String color;

    GemType(final String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    // Might be slow? Calls values() every time to get the length
    public static GemType getRandomType() {
        int r = rand.nextInt(values().length);
        return values()[r];
    }

    public static Image getImage(GemType gemType) {
        if (gemTypeImage == null)
            init();

        return gemTypeImage.get(gemType);
    }

    /**
     * Initializes a mapping from GemType to Image
     */
    public static void init() {
        String root = "main/resources/";
        String extension = ".png";

        gemTypeImage = new EnumMap<>(GemType.class);
        for (GemType type : values()) {
            String path = root + type.getColor() + extension;
            URL url = ClassLoader.getSystemClassLoader().getResource(path);
            if (url != null)
                gemTypeImage.put(type, new Image(url.toString()));
        }
    }
}
