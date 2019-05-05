package eu.chargetime.ocpp.gui;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ServerTab {

    public Tab constructTab(Stage primaryStage) {
        Tab tab = new Tab();
        tab.setText("G Server");
        tab.setClosable(false);

        ImageView imageView = new ImageView();
        imageView.setImage(new Image(getClass().getResourceAsStream("/images/under_construction.gif")));

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(imageView);

        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);

        tab.setContent(stackPane);
        return tab;
    }
}
