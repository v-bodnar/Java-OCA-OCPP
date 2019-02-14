package eu.chargetime.ocpp.gui;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

public class ConsoleStream extends OutputStream {
    private TextArea output;

    public ConsoleStream(TextArea ta) {
        this.output = ta;
    }

    @Override
    public void write(int i) throws IOException {
        Platform.runLater(() -> {
            output.appendText(String.valueOf((char) i));
        });
    }
}
