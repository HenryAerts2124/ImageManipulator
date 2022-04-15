/*
 * Course: CS1021 - 051
 * Winter 2022
 * Lab 9
 * Name: Henry Aerts
 * Created: 2/2/2022
 */

package aertsh;
import edu.msoe.cs1021.ImageUtil;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Scanner;

/**
 * Class to handle conversion of file types and file IO
 */
public class ImageIO extends Application {

    private static final int RGB_MAX = 255;
    private static final int RGB_HEX = 0x000000FF;
    private static final int HEX_MAX = 8;
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 400;
    private static final double RGB_MAX_DOUBLE = 255.0;
    private static final int ONE_BYTE = 8;
    private static final int TWO_BYTE = 16;
    private static final int THREE_BYTE = 24;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("lab8.fxml")));
        stage.setTitle("Image Manipulator V1");
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.show();
    }


    /**
     * Overarching method to read an image from a path and return an image object
     * @param path - passed in path to read image from
     * @return - javafx image object
     * @throws IllegalArgumentException - Thrown when the hex values in a .msoe file
     * are not formatted correctly
     * @throws IOException - Thrown when a file cannot be found/cannot be read
     */
    public static Image read(Path path) throws IllegalArgumentException, IOException {
        String name = path.toString();
        Image image;
        image = switch (name.substring(name.indexOf('.') + 1)) {
            case "png", "jpg" -> ImageUtil.readImage(path);
            case "msoe" -> ImageIO.readMSOE(path);
            case "bmsoe" -> ImageIO.readBMSOE(path);
            default -> null;
        };

        return image;
    }

    /**
     * Method to handle writing an image to a new path
     * @param image - image passed through to be written to the new file
     * @param path - path for the image to be written to
     * @throws IOException - thrown when there is an issue with writing to the path, an example is
     * the path not being found.
     *
     */
    public static void write(Image image, Path path) throws IOException {
        String name = path.toString();
        switch (name.substring(name.indexOf('.') + 1)) {
            case "png", "jpg" -> ImageUtil.writeImage(path, image);
            case "msoe" -> ImageIO.writeMSOE(image, path);
            case "bmsoe" -> ImageIO.writeBMSOE(image, path);
        }

    }

    private static Image readMSOE(Path path) throws IOException {
        Scanner scanner = new Scanner(path);
        double width = 0;
        double height = 0;
        if(scanner.hasNext() && Objects.equals(scanner.next(), "MSOE")){
            scanner.nextLine();
            width = scanner.nextDouble();
            height = scanner.nextDouble();
        }
        Scanner parser = new Scanner(path);
        parser.nextLine();
        parser.nextLine();
        WritableImage photo = new WritableImage((int) width, (int) height);
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++) {
                String hex = parser.next();
                photo.getPixelWriter().setColor(j, i, Color.valueOf(hex));
            }
        }
        return photo;
    }

    private static Image readBMSOE(Path path) throws IOException {
        FileInputStream in = new FileInputStream(path.toString());
        DataInputStream stream = new DataInputStream(in);
        char b = (char) stream.readByte();
        char m = (char) stream.readByte();
        char s = (char) stream.readByte();
        char o = (char) stream.readByte();
        char e = (char) stream.readByte();
        int width = stream.readInt();
        int height = stream.readInt();
        WritableImage photo = new WritableImage(width, height);
        if (b == 'B' && m == 'M' && s == 'S' && o == 'O' && e == 'E') {
            DataInputStream dIn = new DataInputStream(in);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int hex = dIn.readInt();
                    photo.getPixelWriter().setColor(j, i, intToColor(hex));
                }
            }
        }
        in.close();
        stream.close();
        return photo;
    }

    private static void writeMSOE(Image image, Path path) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(path.toFile());
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        Color color;
        writer.println("MSOE");
        writer.println(width + " " + height);
        for(int i = 0; i < height; i++){
            if(i > 0){
                writer.write("\n");
            }
            for(int j = 0; j < width; j++){
                color = image.getPixelReader().getColor(j, i);
                String hex = color.toString();
                String hexOut = ("#" + hex.substring(2, HEX_MAX) + "  ");
                writer.print(hexOut);
            }

        }
        writer.close();


    }
    private static void writeBMSOE(Image image, Path path) throws IOException {
        FileOutputStream out = new FileOutputStream(path.toString());
        DataOutputStream outer = new DataOutputStream(out);
        outer.writeByte('B');
        outer.writeByte('M');
        outer.writeByte('S');
        outer.writeByte('O');
        outer.writeByte('E');
        outer.writeInt((int) image.getWidth());
        outer.writeInt((int) image.getHeight());
        for(int i = 0; i < (int)image.getHeight(); i++){
            for(int j = 0; j < (int)image.getWidth(); j++){
                outer.writeInt(colorToInt(image.getPixelReader().getColor(j, i)));
            }
        }
    }


    private static Color intToColor(int color) {
        double red = ((color >> TWO_BYTE) & RGB_HEX)/RGB_MAX_DOUBLE;
        double green = ((color >> ONE_BYTE) & RGB_HEX)/RGB_MAX_DOUBLE;
        double blue = (color & RGB_HEX)/RGB_MAX_DOUBLE;
        double alpha = ((color >> THREE_BYTE) & RGB_HEX)/RGB_MAX_DOUBLE;
        return new Color(red, green, blue, alpha);
    }

    private static int colorToInt(Color color) {
        int red = ((int)(color.getRed()*RGB_MAX)) & RGB_HEX;
        int green = ((int)(color.getGreen()*RGB_MAX)) & RGB_HEX;
        int blue = ((int)(color.getBlue()*RGB_MAX)) & RGB_HEX;
        int alpha = ((int)(color.getOpacity()*RGB_MAX)) & RGB_HEX;
        return (alpha << THREE_BYTE) + (red << TWO_BYTE) + (green << ONE_BYTE) + blue;
    }


}
