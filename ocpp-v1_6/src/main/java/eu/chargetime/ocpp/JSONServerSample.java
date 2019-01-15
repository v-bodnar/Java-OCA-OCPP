package eu.chargetime.ocpp;

import eu.chargetime.ocpp.feature.profile.ClientFirmwareManagementEventHandler;
import eu.chargetime.ocpp.feature.profile.ClientFirmwareManagementProfile;
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
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationConfirmation;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationConfirmation;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsConfirmation;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsRequest;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareConfirmation;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareRequest;
import eu.chargetime.ocpp.rest.WebServer;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;

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
public class JSONServerSample {
    private static JSONServer server;
    private static ServerCoreProfile core;
    private static Map<UUID, SessionInformation> sessionList = new HashMap<>();
    private static final ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(1);
    private static JSONCommunicator jsonCommunicator = new JSONCommunicator(null);

    public static void main(String... args) {
        try {
            new WebServer(8080).startServer();
            started();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void send(Request request) {
        for (Map.Entry<UUID, SessionInformation> entry : sessionList.entrySet()) {
            try {
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

    public static void sendToAll(Request request) throws NotConnectedException, OccurenceConstraintException, UnsupportedFeatureException {
        for (Map.Entry<UUID, SessionInformation> entry : sessionList.entrySet()) {
            server.send(entry.getKey(), request);
        }
    }

    public static void started() throws Exception {
        if (server != null) {
            return;
        }


        // The core profile is mandatory
        core = new ServerCoreProfile(new OcppEventHandler());
        server = new JSONServer(core);

        ClientFirmwareManagementEventHandler client = new ClientFirmwareManagementEventHandler() {
            @Override
            public GetDiagnosticsConfirmation handleGetDiagnosticsRequest(GetDiagnosticsRequest request) {
                System.out.println(request.getClass().getSimpleName() + " - " + jsonCommunicator.packPayload(request));
                return null;
            }

            @Override
            public DiagnosticsStatusNotificationConfirmation handleDiagnosticsStatusNotificationRequest(DiagnosticsStatusNotificationRequest request) {
                System.out.println(request.getClass().getSimpleName() + " - " + jsonCommunicator.packPayload(request));
                return new DiagnosticsStatusNotificationConfirmation();
            }

            @Override
            public FirmwareStatusNotificationConfirmation handleFirmwareStatusNotificationRequest(FirmwareStatusNotificationRequest request) {
                System.out.println(request.getClass().getSimpleName() + " - " + jsonCommunicator.packPayload(request));
                return null;
            }

            @Override
            public UpdateFirmwareConfirmation handleUpdateFirmwareRequest(UpdateFirmwareRequest request) {
                System.out.println(request.getClass().getSimpleName() + " - " + jsonCommunicator.packPayload(request));
                return null;
            }
        };

        server.addFeatureProfile(new ClientFirmwareManagementProfile(client));
        server.open("localhost", 8887, new ServerEvents() {
            @Override
            public void newSession(UUID sessionIndex, SessionInformation information) {

                // sessionIndex is used to send messages.
                System.out.println("New session " + sessionIndex + ": " + information.getIdentifier());
                sessionList.put(sessionIndex, information);
            }

            @Override
            public void lostSession(UUID sessionIndex) {
                System.out.println("Session " + sessionIndex + " lost connection");
                sessionList.remove(sessionIndex);
            }
        });
    }

    public void sendClearCacheRequest() throws Exception {

        // Use the feature profile to help create event
        ClearCacheRequest request = core.createClearCacheRequest();

        UUID sessionIndex = null;
        // Server returns a promise which will be filled once it receives a confirmation.
        // Select the distination client with the sessionIndex integer.
        server.send(sessionIndex, request).whenComplete((confirmation, throwable) -> System.out.println(confirmation));
    }

    public static void sendResetRequest(Integer delay) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("Sending ResetRequest");
                        ResetRequest resetRequest = new ResetRequest();
                        resetRequest.setType(ResetType.Soft);
                        send(resetRequest);
                    }
                }, delay * 1000
        );
    }

    public static void sendGetDiagnostics(Integer delay) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("Sending getDiagnosticsRequest");
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


    public static void sendChangeAvailability(Integer delay, AvailabilityType availabilityType) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("Sending ChangeAvailability" + availabilityType);
                        ChangeAvailabilityRequest changeAvailabilityRequest = new ChangeAvailabilityRequest();
                        try {
                            changeAvailabilityRequest.setConnectorId(1);
                        } catch (PropertyConstraintException e) {
                            System.out.println("Should not happen");
                        }
                        changeAvailabilityRequest.setType(availabilityType);
                        send(changeAvailabilityRequest);
                    }
                }, delay * 1000
        );
    }

    public static void sendConfig(Integer delay) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("Sending ChangeConfiguration");
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

    public static void sendGetConfig(Integer delay) {
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
}