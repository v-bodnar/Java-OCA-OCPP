package eu.chargetime.ocpp.server;

import eu.chargetime.ocpp.JSONServer;
import eu.chargetime.ocpp.NotConnectedException;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.PropertyConstraintException;
import eu.chargetime.ocpp.ServerEvents;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.feature.profile.ClientFirmwareManagementProfile;
import eu.chargetime.ocpp.feature.profile.ClientRemoteTriggerProfile;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.SessionInformation;
import eu.chargetime.ocpp.model.core.AvailabilityType;
import eu.chargetime.ocpp.model.core.ChangeAvailabilityRequest;
import eu.chargetime.ocpp.model.core.ChangeConfigurationRequest;
import eu.chargetime.ocpp.model.core.ClearCacheRequest;
import eu.chargetime.ocpp.model.core.GetConfigurationRequest;
import eu.chargetime.ocpp.model.core.ResetRequest;
import eu.chargetime.ocpp.model.core.ResetType;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsRequest;
import eu.chargetime.ocpp.server.handler.CoreEventHandler;
import eu.chargetime.ocpp.server.handler.FirmwareManagementEventHandler;
import eu.chargetime.ocpp.server.handler.RemoteTriggerEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static eu.chargetime.ocpp.gui.StubRequestsFactory.toJson;

/*
 ChargeTime.eu - Java-OCA-OCPP
 Copyright (C) 2015-2016 Thomas Volden <tv@chargetime.eu>

 MIT License

 Copyright (C) 2016-2018 Thomas Volden

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
public class OcppServerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OcppServerService.class);
    private ServerCoreProfile core = new ServerCoreProfile(new CoreEventHandler());
    private Map<UUID, SessionInformation> sessionList = new HashMap<>();
    private Profile firmwareProfile = new ClientFirmwareManagementProfile(new FirmwareManagementEventHandler());
    private Profile remoteTriggerProfile = new ClientRemoteTriggerProfile(new RemoteTriggerEventHandler());
    private SessionsListener sessionsListener = new StubSessionListener();

    private JSONServer server;

    public void start(String ip, String port) {
        LOGGER.info("Starting OCPP Server");
        if (server != null) {
            LOGGER.warn("Server already created, no actions will be performed");
            return;
        }
        server = new JSONServer(core);
        server.addFeatureProfile(firmwareProfile);
        server.addFeatureProfile(remoteTriggerProfile);

        LOGGER.info("Ocpp server ip: {}, port: {}", ip, port);
        server.open(ip, Integer.parseInt(port), new ServerEvents() {
            @Override
            public void newSession(UUID sessionIndex, SessionInformation information) {
                // sessionIndex is used to send messages.
                LOGGER.debug("New session " + sessionIndex + ": " + information.getIdentifier());
                sessionList.put(sessionIndex, information);
                sessionsListener.onSessionsCountChange(sessionList);
            }

            @Override
            public void lostSession(UUID sessionIndex) {
                LOGGER.debug("Session " + sessionIndex + " lost connection");
                sessionList.remove(sessionIndex);
                sessionsListener.onSessionsCountChange(sessionList);
            }
        });
    }

    public void stop() {
        server.close();
        sessionList.clear();
        sessionsListener.onSessionsCountChange(sessionList);
        server = null;
    }

    public boolean isRunning() {
        return server != null && !server.isClosed();
    }

    public void send(Request request) {
        for (Map.Entry<UUID, SessionInformation> entry : sessionList.entrySet()) {
            try {
                LOGGER.debug("Sending message: {}", request);
                server.send(entry.getKey(), request);
            } catch (OccurenceConstraintException e) {
                e.printStackTrace();
            } catch (UnsupportedFeatureException e) {
                e.printStackTrace();
            } catch (NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(Request request, String sessionToken) {
        String identifier = sessionToken.split(" ")[0];
        String address = sessionToken.split(" ")[1]
                .replace("(", "").replace(")", "");

        Optional<UUID> sessionUUID = sessionList.entrySet().stream()
                .filter(entry -> entry.getValue().getIdentifier().equals(identifier)
                        && entry.getValue().getAddress().toString().equals(address))
                .map(uuidSessionInformationEntry -> uuidSessionInformationEntry.getKey())
                .findAny();
        if (!sessionUUID.isPresent()) {
            LOGGER.error("Could not find client by session token: {}", sessionToken);
            return;
        }

        try {
            LOGGER.debug("Sending message: {} to {}", toJson(request), sessionToken);
            server.send(sessionUUID.get(), request);
        } catch (OccurenceConstraintException e) {
            e.printStackTrace();
        } catch (UnsupportedFeatureException e) {
            e.printStackTrace();
        } catch (NotConnectedException e) {
            e.printStackTrace();
        }

    }

    public void sendToAll(Request request) throws NotConnectedException, OccurenceConstraintException, UnsupportedFeatureException {
        for (Map.Entry<UUID, SessionInformation> entry : sessionList.entrySet()) {
            server.send(entry.getKey(), request);
        }
    }

    public void sendClearCacheRequest() throws Exception {

        // Use the feature profile to help create event
        ClearCacheRequest request = core.createClearCacheRequest();

        UUID sessionIndex = null;
        // Server returns a promise which will be filled once it receives a confirmation.
        // Select the distination client with the sessionIndex integer.
        server.send(sessionIndex, request).whenComplete((confirmation, throwable) -> LOGGER.debug(confirmation.toString()));
    }

    public void sendResetRequest(Integer delay) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        LOGGER.debug("Sending ResetRequest");
                        ResetRequest resetRequest = new ResetRequest();
                        resetRequest.setType(ResetType.Soft);
                        send(resetRequest);
                    }
                }, delay * 1000
        );
    }

    public void sendGetDiagnostics(Integer delay) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        LOGGER.debug("Sending getDiagnosticsRequest");
                        Calendar startTime = new GregorianCalendar(2018, 5, 24);
                        Calendar stopTime = new GregorianCalendar(2018, 5, 30);
                        GetDiagnosticsRequest getDiagnosticsRequest = new GetDiagnosticsRequest();
                        getDiagnosticsRequest.setLocation("ftp://lithos:lithos@127.0.0.1/ABB/");
                        getDiagnosticsRequest.setRetries(0);
                        getDiagnosticsRequest.setStartTime(startTime);
                        getDiagnosticsRequest.setStopTime(stopTime);
                        send(getDiagnosticsRequest);
                    }
                }, delay * 1000
        );
    }


    public void sendChangeAvailability(Integer delay, AvailabilityType availabilityType) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        LOGGER.debug("Sending ChangeAvailability" + availabilityType);
                        ChangeAvailabilityRequest changeAvailabilityRequest = new ChangeAvailabilityRequest();
                        try {
                            changeAvailabilityRequest.setConnectorId(1);
                        } catch (PropertyConstraintException e) {
                            LOGGER.debug("Should not happen");
                        }
                        changeAvailabilityRequest.setType(availabilityType);
                        send(changeAvailabilityRequest);
                    }
                }, delay * 1000
        );
    }

    public void sendConfig(Integer delay) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        LOGGER.debug("Sending ChangeConfiguration");
                        ChangeConfigurationRequest changeConfigurationRequest = new ChangeConfigurationRequest();
                        try {
                            changeConfigurationRequest.setKey("FreevendEnabled");
                            changeConfigurationRequest.setValue("true");
                        } catch (PropertyConstraintException e) {
                            e.printStackTrace();
                        }
                        send(changeConfigurationRequest);
//                        ChangeConfigurationRequest changeConfigurationRequest = new ChangeConfigurationRequest();
//                        try {
//                            changeConfigurationRequest.setKey("FreevendIdTag");
//                            changeConfigurationRequest.setValue("true");
//                        } catch (PropertyConstraintException e) {
//                            e.printStackTrace();
//                        }
//                        send(changeConfigurationRequest);
                    }
                }, delay * 1000
        );
    }

    public void sendGetConfig(Integer delay) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        GetConfigurationRequest getConfigurationRequest = new GetConfigurationRequest();
                        send(getConfigurationRequest);
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            getConfigurationRequest.setKey(new String[]{"ChargeBoxName"});
                        } catch (PropertyConstraintException e) {
                            e.printStackTrace();
                        }
                        send(getConfigurationRequest);
//                        ChangeConfigurationRequest changeConfigurationRequest = new ChangeConfigurationRequest();
//                        try {
//                            changeConfigurationRequest.setKey("ChargeBoxName");
//                        } catch (PropertyConstraintException e) {
//                            e.printStackTrace();
//                        }
//                        try {
//                            changeConfigurationRequest.setValue("test");
//                        } catch (PropertyConstraintException e) {
//                            e.printStackTrace();
//                        }
//                        send(changeConfigurationRequest);
                    }
                }, delay * 1000
        );
    }

    public Map<UUID, SessionInformation> getSessionList() {
        return sessionList;
    }

    public void setSessionsListener(SessionsListener sessionsListener) {
        this.sessionsListener = sessionsListener;
    }

    public Optional<SessionInformation> getSessionInformation(UUID sessionUuid){
        return Optional.ofNullable(sessionList.get(sessionUuid));
    }
}