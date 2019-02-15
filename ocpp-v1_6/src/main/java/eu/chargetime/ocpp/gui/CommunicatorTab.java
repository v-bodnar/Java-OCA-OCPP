package eu.chargetime.ocpp.gui;

import eu.chargetime.ocpp.model.SessionInformation;
import eu.chargetime.ocpp.server.OcppServerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommunicatorTab {
    private OcppServerService ocppServerService = ApplicationContext.INSTANCE.getOcppServerService();
    private ListView<String> sessionsList = new ListView<>();
    private TextField selectedClientField = new TextField();
    private ComboBox messageTypeCombo = new ComboBox();
    private TextArea messageTextArea = new TextArea();
    private Button sendButton = new Button("Send Message");

    public Tab constructTab() {
        Tab tab = new Tab();
        tab.setText("Communicator");
        tab.setClosable(false);

        ObservableList<String> items = FXCollections.observableArrayList(
                getSessionsListFormatted(ocppServerService.getSessionList()));
        sessionsList.setItems(items);
        sessionsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedClientField.setText(newValue);
        });
        ocppServerService.setSessionsListener(new SessionsListener());

        Label label = new Label();
        label.setText("Selected client:");

        selectedClientField.setText("NONE");
        selectedClientField.setPrefWidth(500);
        selectedClientField.setEditable(false);

        HBox hBox1 = new HBox();
        hBox1.setSpacing(10);
        hBox1.setPadding(new Insets(5));
        hBox1.setAlignment(Pos.CENTER_LEFT);
        hBox1.getChildren().addAll(label, selectedClientField);

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(5));
        vBox.getChildren().addAll(hBox1, messageTypeCombo, messageTextArea, sendButton);
        vBox.setFillWidth(true);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(5));
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(sessionsList, vBox);

        tab.setContent(hBox);
        return tab;
    }


    public static List<String> getSessionsListFormatted(Map<UUID, SessionInformation> sessions) {
        return sessions.values().stream()
                .map(sessionInformation ->
                        String.format("%s (%s)", sessionInformation.getIdentifier(), sessionInformation.getAddress()))
                .collect(Collectors.toList());
    }

    class SessionsListener implements eu.chargetime.ocpp.server.SessionsListener {
        @Override
        public void onSessionsCountChange(Map<UUID, SessionInformation> sessions) {
            ObservableList<String> items = FXCollections.observableArrayList(
                    getSessionsListFormatted(sessions));
            sessionsList.setItems(items);
        }
    }
}


