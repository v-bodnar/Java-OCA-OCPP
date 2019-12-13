package eu.chargetime.ocpp;

import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.handshake.ClientHandshake;

public interface HandshakeResolver {
    void onHandshake(ClientHandshake request) throws InvalidDataException;
}
