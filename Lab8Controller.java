/*
 * Course: CS1021 - 051
 * Winter 2022
 * Lab 8
 * Name: Henry Aerts
 * Created: 2/2/2022
 */

package aertsh;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Controller class for GUI elements, handles loading, saving and image manipulation.
 */
public class  Lab8Controller {
    private static final double RED_MULTI = 0.2126;
    private static final double GREEN_MULTI = 0.7152;
    private static final double BLUE_MULTI = 0.0722;


    @FXML
    Button redGray;
    @FXML
    Button red;
    @FXML
    Button open;

    @FXML
    Button save;

    @FXML
    Button reload;

    @FXML
    Button grayScale;

    @FXML
    Button negative;

    @FXML
    ImageView imageView;

    File file = null;
    Image originalImage;


    Stage stage;

    /**
     * Method to open image files
     */
    @FXML
    public void open(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.setInitialDirectory(Paths.get("./images").toFile());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.msoe",
                        "*.bmsoe"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            file = selectedFile;
        }
        try {
            imageView.setImage(ImageIO.read(file.toPath()));
            originalImage = ImageIO.read(file.toPath());
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("IO Exception");
            alert.setHeaderText("There was an error in accessing the file");
            alert.setContentText("The file could not be opened");
            alert.showAndWait();
        } catch (IllegalArgumentException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Illegal Argument Exception");
            alert.setHeaderText("There was an error in accessing the file");
            alert.setContentText("The pixels in the .msoe file could not be read");
            alert.showAndWait();
        }
    }


    /**
     * Method to save files to whatever type and path the user specifies
     */
    @FXML
    public void save(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image File");
        fileChooser.setInitialDirectory(Paths.get("./images").toFile());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.msoe",
                        "*.bmsoe"));
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            file = selectedFile;
        }
        Path path = file.toPath();
        Image photo = imageView.getImage();
        try {
            ImageIO.write(photo, path);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("IO Exception");
            alert.setHeaderText("There was an error in saving the file");
            alert.setContentText("The file could not be saved.");
            alert.showAndWait();
        }
    }

    /**
     * method to reset to most recently loaded image after manipulation
     */
    @FXML
    public void reload(){
        imageView.setImage(originalImage);
    }

    /**
     * method to transform image to the grayscale version of itself
     */
    @FXML
    public void grayScale(){
        if(imageView.getImage() != null) {
            imageView.setImage(transformImage(imageView.getImage(), (y, c) ->
                    Color.gray((RED_MULTI*c.getRed() +
                            GREEN_MULTI*c.getGreen() + BLUE_MULTI*c.getBlue()))));
        } else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to add filter.");
            alert.setContentText("No image has been loaded.");
            alert.showAndWait();
        }

    }

    /**
     * method to transform image to the negative version of itself
     */
    @FXML
    public void negative(){
        if(imageView.getImage() != null) {
            imageView.setImage(transformImage(imageView.getImage(), (y, c) ->
                    Color.color(1 - c.getRed(), 1 - c.getGreen(), 1 - c.getBlue())));
        } else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to add filter.");
            alert.setContentText("No image has been loaded.");
            alert.showAndWait();
        }


    }

    /**
     * Method to convert images to a red-gray version of themselves
     */
    @FXML
    public void redGray() {
        Color color;
        WritableImage photo = new WritableImage(imageView.getImage().getPixelReader(),
                (int) imageView.getImage().getWidth(), (int) imageView.getImage().getHeight());
        double height = photo.getHeight();
        double width = photo.getWidth();
        for(int i = 0; i < height; i++){
            if(i % 2 == 0) {
                for (int j = 0; j < width; j++) {
                    color = photo.getPixelReader().getColor(j, i);
                    photo.getPixelWriter().setColor(j, i, color.grayscale());
                }
            } else{
                for (int j = 0; j < width; j++) {
                    color = photo.getPixelReader().getColor(j, i);
                    photo.getPixelWriter().setColor(j, i, Color.color(color.getRed(), 0, 0));
                }
            }
        }
        imageView.setImage(photo);
    }

    /**
     * Method to convert images to a red-only version of themselves
     */
    @FXML
    public void red() {
        if(imageView.getImage() != null) {
            imageView.setImage(transformImage(imageView.getImage(), (y, c) ->
                    Color.color(c.getRed(), 0, 0)));
        } else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to add filter.");
            alert.setContentText("No image has been loaded.");
            alert.showAndWait();
        }
    }

    private static Image transformImage(Image image, Transformable transform){
        PixelReader reader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage photo = new WritableImage(width, height);
        PixelWriter writer = photo.getPixelWriter();

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                Color c = reader.getColor(x, y);
                writer.setColor(x, y, transform.apply(y, c));
            }
        }

        return photo;

    }


    /**
     * Functional interface used to streamline transformation of images
     */
    @FunctionalInterface
    public interface Transformable {
        /**
         * Abstract method that takes in a y-value of a pixel and its color and returns
         * the pixel's color after applying the specified transformation
         * @param y - the y-axis location of the pixel
         * @param pixelColor - the color of the pixel
         * @return - the color of the pixel after the transformation has been applied
         */
        Color apply(int y, Color pixelColor); }
}
