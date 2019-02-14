package eu.chargetime.ocpp.gui;

import eu.chargetime.ocpp.rest.WebServer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GuiApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        createMainScene(primaryStage);
        new WebServer(8080).startServer();
    }

    public void createMainScene(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 600);

        // bind to take available space
        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
        root.getChildren().add(borderPane);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().add(new GeneralTab().constructTab());
        borderPane.setCenter(tabPane);

        primaryStage.setTitle("Ocpp Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }


}
