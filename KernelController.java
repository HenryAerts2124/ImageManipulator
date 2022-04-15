/*
 * Course: CS1021 - 051
 * Winter 2022
 * Lab 9
 * Name: Henry Aerts
 * Created: 2/2/2022
 */
package aertsh;

import edu.msoe.cs1021.ImageUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

/**
 * Controller class for the image blur/sharpen edit window
 */
public class KernelController {
    @FXML
    TextField one;
    @FXML
    TextField two;
    @FXML
    TextField three;
    @FXML
    Button blur;
    @FXML
    TextField four;
    @FXML
    TextField five;
    @FXML
    TextField six;
    @FXML
    Button sharpen;
    @FXML
    TextField seven;
    @FXML
    TextField eight;
    @FXML
    TextField nine;
    @FXML
    Button apply;
    @FXML
    ImageView photo;

    /**
     * Method that sets the filter values to the default "blur" values
     */
    public void blur(){
        one.setText("0");
        two.setText("1");
        three.setText("0");
        four.setText("1");
        five.setText("5");
        six.setText("1");
        seven.setText("0");
        eight.setText("1");
        nine.setText("0");
    }

    /**
     * Method that sets the filter values to the default "sharpen" values
     */
    public void sharpen(){
        one.setText("0");
        two.setText("-1");
        three.setText("0");
        four.setText("-1");
        five.setText("5");
        six.setText("-1");
        seven.setText("0");
        eight.setText("-1");
        nine.setText("0");
    }

    /**
     * Method to apply the designated filter values to the original image
     */
    public void apply() {
        int valueTotal = 0;
        double[] kernel = {Double.parseDouble(one.getText()), Double.parseDouble(two.getText()),
                Double.parseDouble(three.getText()),
                Double.parseDouble(four.getText()), Double.parseDouble(five.getText()),
                Double.parseDouble(six.getText()),
                Double.parseDouble(seven.getText()), Double.parseDouble(eight.getText()),
                Double.parseDouble(nine.getText())};
        for (double v : kernel) {
            valueTotal += v;
        }
        if(valueTotal > 0){
            for(int i = 0; i < kernel.length; i++){
                kernel[i] = kernel[i] / valueTotal;
            }
            photo.setImage(ImageUtil.convolve(photo.getImage(), kernel));
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Illegal Filter Values");
            alert.setContentText("Filter Values must sum to a positive value.");
            alert.showAndWait();
        }
    }

    public void setImageView(ImageView imageView){
        photo = imageView;
    }
}
