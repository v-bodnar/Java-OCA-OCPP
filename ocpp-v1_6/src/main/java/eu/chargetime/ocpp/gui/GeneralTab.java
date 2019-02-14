package eu.chargetime.ocpp.gui;

import eu.chargetime.ocpp.OcppServerService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

public class GeneralTab {
    private Label serverState = new Label("Stopped");
    private Button serverButton = new Button("Start");

    public Tab constructTab(){
        Tab tab = new Tab();
        tab.setText("General");
        tab.setClosable(false);

        TextArea textArea = new TextArea();
        textArea.setPrefWidth(995);
        textArea.setPrefHeight(595);

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(event -> textArea.clear());

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(5));
        hBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(serverState, Priority.ALWAYS);
        hBox.getChildren().addAll(serverState, serverButton, clearButton);

        ConsoleStream console = new ConsoleStream(textArea);
        PrintStream ps = new PrintStream(console, true);
        System.setOut(ps);
        System.setErr(ps);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(hBox, textArea);
        tab.setContent(vBox);

        startStateChecker();
        return tab;
    }

    private void checkAndSetServerStateColor() {
        if (OcppServerService.isRunning()) {
            serverState.setStyle("-fx-text-fill: #0aa000;");
            serverState.setText("Started");
            serverButton.setText("Stop");
            serverButton.setDisable(false);
            serverButton.setOnAction(event -> {
                CompletableFuture.runAsync(OcppServerService::stop);
                serverState.setText("Stopping...");
                serverButton.setDisable(true);
            });
        } else {
            serverState.setStyle("-fx-text-fill: #be0000;");
            serverState.setText("Stopped");
            serverButton.setText("Start");
            serverButton.setDisable(false);
            serverButton.setOnAction(event -> {
                CompletableFuture.runAsync(OcppServerService::start);
                serverState.setText("Starting...");
                serverButton.setDisable(true);
            });
        }
    }

    private void startStateChecker() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    checkAndSetServerStateColor();
                });
            }
        }, 0, 3000);
    }
}
