package eu.chargetime.ocpp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import eu.chargetime.ocpp.feature.profile.ServerCoreEventHandler;
import eu.chargetime.ocpp.model.core.AuthorizationStatus;
import eu.chargetime.ocpp.model.core.AuthorizeConfirmation;
import eu.chargetime.ocpp.model.core.AuthorizeRequest;
import eu.chargetime.ocpp.model.core.BootNotificationConfirmation;
import eu.chargetime.ocpp.model.core.BootNotificationRequest;
import eu.chargetime.ocpp.model.core.DataTransferConfirmation;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import eu.chargetime.ocpp.model.core.DataTransferStatus;
import eu.chargetime.ocpp.model.core.HeartbeatConfirmation;
import eu.chargetime.ocpp.model.core.HeartbeatRequest;
import eu.chargetime.ocpp.model.core.IdTagInfo;
import eu.chargetime.ocpp.model.core.MeterValuesConfirmation;
import eu.chargetime.ocpp.model.core.MeterValuesRequest;
import eu.chargetime.ocpp.model.core.RegistrationStatus;
import eu.chargetime.ocpp.model.core.StartTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StartTransactionRequest;
import eu.chargetime.ocpp.model.core.StatusNotificationConfirmation;
import eu.chargetime.ocpp.model.core.StatusNotificationRequest;
import eu.chargetime.ocpp.model.core.StopTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StopTransactionRequest;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.UUID;

public class OcppEventHandler implements ServerCoreEventHandler {
    private static JSONCommunicator jsonCommunicator = new JSONCommunicator(null);

    @Override
    public AuthorizeConfirmation handleAuthorizeRequest(UUID sessionIndex, AuthorizeRequest request) {

        System.out.println(request.getClass().getSimpleName() + " - " + jsonCommunicator.packPayload(request));
        // ... handle event
        IdTagInfo idTagInfo = new IdTagInfo();
        idTagInfo.setExpiryDate(new GregorianCalendar(2020, 1, 1));
        idTagInfo.setStatus(AuthorizationStatus.Accepted);
        AuthorizeConfirmation authorizeConfirmation = new AuthorizeConfirmation();
        authorizeConfirmation.setIdTagInfo(idTagInfo);

        return authorizeConfirmation;
    }

    @Override
    public BootNotificationConfirmation handleBootNotificationRequest(UUID sessionIndex, BootNotificationRequest request) {

        System.out.println(request.getClass().getSimpleName() + " - " + jsonCommunicator.packPayload(request));
        // ... handle event
        BootNotificationConfirmation bootNotificationConfirmation = new BootNotificationConfirmation();
        bootNotificationConfirmation.setCurrentTime(Calendar.getInstance());
        try {
            bootNotificationConfirmation.setInterval(180);
        } catch (PropertyConstraintException e) {
            e.printStackTrace();
        }
        bootNotificationConfirmation.setStatus(RegistrationStatus.Accepted);
        return bootNotificationConfirmation;// returning null means unsupported feature
    }

    @Override
    public DataTransferConfirmation handleDataTransferRequest(UUID sessionIndex, DataTransferRequest request) {

        // ... handle event
        if (request.getMessageId().equalsIgnoreCase("GetPrice.req")) {
            DataTransferConfirmation confirmation = new DataTransferConfirmation();

            JsonObject levelPrice = new JsonObject();
            levelPrice.addProperty("power", 10);
            levelPrice.addProperty("priceValue", 10);
            levelPrice.addProperty("initialPriceValue", 1);
            levelPrice.addProperty("initialDuration", 10);
            levelPrice.addProperty("authAmount", 100);
            levelPrice.addProperty("rate", "Wh");
            JsonArray levelPrices = new JsonArray();
            levelPrices.add(levelPrice);

            JsonObject intervalPrice = new JsonObject();
            intervalPrice.addProperty("intervalStart", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
            intervalPrice.addProperty("intervalEnd", ZonedDateTime.now(ZoneOffset.UTC).plusYears(1).format(DateTimeFormatter.ISO_INSTANT));
            intervalPrice.add("levelPrice", levelPrices);
            JsonArray intervalPrices = new JsonArray();
            intervalPrices.add(intervalPrice);

            JsonObject connectorPrice = new JsonObject();
            connectorPrice.addProperty("connectorId", 1);
            connectorPrice.add("intervalPrice", intervalPrices);
            JsonObject connectorPrice2 = new JsonObject();
            connectorPrice2.addProperty("connectorId", 2);
            connectorPrice2.add("intervalPrice", intervalPrices);
            JsonObject connectorPrice3 = new JsonObject();
            connectorPrice3.addProperty("connectorId", 3);
            connectorPrice3.add("intervalPrice", intervalPrices);
            JsonArray connectorPrices = new JsonArray();
            connectorPrices.add(connectorPrice);
            connectorPrices.add(connectorPrice2);
            connectorPrices.add(connectorPrice3);

            JsonObject getPriceConfirmation = new JsonObject();
            getPriceConfirmation.addProperty("currency", "EU");
            getPriceConfirmation.addProperty("firstName", "Vasia");
            getPriceConfirmation.addProperty("lastName", "Pupkin");
            getPriceConfirmation.addProperty("planName", "planName");
            getPriceConfirmation.addProperty("planDescription", "planDescription");
            getPriceConfirmation.add("connectorPrice", connectorPrices);

            //confirmation.setData("{\"currency\":\"EU\",\"firstName\":\"Mateusz\",\"lastName\":\"Zebracki\",\"planName\":\"planName\",\"planDescription\":\"description\",\"ConnectorPrice\":\"\"}");
            confirmation.setData(getPriceConfirmation.toString());
            confirmation.setStatus(DataTransferStatus.Accepted);

            return confirmation; // returning null means unsupported feature
        } else {
            return null;
        }
    }

    @Override
    public HeartbeatConfirmation handleHeartbeatRequest(UUID sessionIndex, HeartbeatRequest request) {

        System.out.println(request.getClass().getSimpleName() + " - " + jsonCommunicator.packPayload(request));
        // ... handle event

        return null; // returning null means unsupported feature
    }

    @Override
    public MeterValuesConfirmation handleMeterValuesRequest(UUID sessionIndex, MeterValuesRequest request) {

        System.out.println(request.getClass().getSimpleName() + " - " + jsonCommunicator.packPayload(request));
        // ... handle event

        return new MeterValuesConfirmation(); // returning null means unsupported feature
    }

    @Override
    public StartTransactionConfirmation handleStartTransactionRequest(UUID sessionIndex, StartTransactionRequest request) {

        System.out.println(request.getClass().getSimpleName() + " - " + jsonCommunicator.packPayload(request));
        // ... handle event
        IdTagInfo idTagInfo = new IdTagInfo();
        idTagInfo.setExpiryDate(new GregorianCalendar(2020, 1, 1));
        idTagInfo.setStatus(AuthorizationStatus.Accepted);
        StartTransactionConfirmation startTransactionConfirmation = new StartTransactionConfirmation();
        startTransactionConfirmation.setTransactionId(new Random().nextInt());
        startTransactionConfirmation.setIdTagInfo(idTagInfo);

        return startTransactionConfirmation; // returning null means unsupported feature
    }

    @Override
    public StatusNotificationConfirmation handleStatusNotificationRequest(UUID sessionIndex, StatusNotificationRequest request) {

        System.out.println(request.getClass().getSimpleName() + " - " + jsonCommunicator.packPayload(request));
        // ... handle event

        return new StatusNotificationConfirmation(); // returning null means unsupported feature
    }

    @Override
    public StopTransactionConfirmation handleStopTransactionRequest(UUID sessionIndex, StopTransactionRequest request) {

        System.out.println(request.getClass().getSimpleName() + " - " + jsonCommunicator.packPayload(request));
        // ... handle event

        return null; // returning null means unsupported feature
    }
}
