package eu.chargetime.ocpp.gui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.chargetime.ocpp.PropertyConstraintException;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.AvailabilityType;
import eu.chargetime.ocpp.model.core.ChangeAvailabilityRequest;
import eu.chargetime.ocpp.model.core.ChangeConfigurationRequest;
import eu.chargetime.ocpp.model.core.ChargingProfile;
import eu.chargetime.ocpp.model.core.ClearCacheRequest;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import eu.chargetime.ocpp.model.core.GetConfigurationRequest;
import eu.chargetime.ocpp.model.core.MeterValue;
import eu.chargetime.ocpp.model.core.MeterValuesRequest;
import eu.chargetime.ocpp.model.core.RemoteStartTransactionRequest;
import eu.chargetime.ocpp.model.core.RemoteStopTransactionRequest;
import eu.chargetime.ocpp.model.core.ResetRequest;
import eu.chargetime.ocpp.model.core.ResetType;
import eu.chargetime.ocpp.model.core.SampledValue;
import eu.chargetime.ocpp.model.core.UnlockConnectorRequest;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatus;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.FirmwareStatus;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsRequest;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareRequest;
import eu.chargetime.ocpp.model.localauthlist.GetLocalListVersionRequest;
import eu.chargetime.ocpp.model.localauthlist.SendLocalListRequest;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Optional;

public class StubRequestsFactory {

    private static final Logger logger = LoggerFactory.getLogger(StubRequestsFactory.class);

    private static ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .registerModule(new JavaTimeModule());

    private static String NOT_SUPPORTED = "Request not supported";
    private static String REQUEST_CONSTRUCTION_ERROR = "Request construction error";


    public static String getStubRequest(Class<? extends Request> requestClass) {
        try {
            if (requestClass.equals(ChangeAvailabilityRequest.class)) {
                return getChangeAvailabilityRequest();
            } else if (requestClass.equals(ChangeConfigurationRequest.class)) {
                return getChangeConfigurationRequest();
            } else if (requestClass.equals(ClearCacheRequest.class)) {
                return getClearCacheRequest();
            } else if (requestClass.equals(DataTransferRequest.class)) {
                return getDataTransferRequest();
            } else if (requestClass.equals(GetConfigurationRequest.class)) {
                return getGetConfigurationRequest();
            } else if (requestClass.equals(MeterValuesRequest.class)) {
                return getMeterValuesRequest();
            } else if (requestClass.equals(RemoteStartTransactionRequest.class)) {
                return getRemoteStartTransactionRequest();
            } else if (requestClass.equals(RemoteStopTransactionRequest.class)) {
                return getRemoteStopTransactionRequest();
            } else if (requestClass.equals(ResetRequest.class)) {
                return getResetRequest();
            } else if (requestClass.equals(UnlockConnectorRequest.class)) {
                return getUnlockConnectorRequest();
            } else if (requestClass.equals(DiagnosticsStatusNotificationRequest.class)) {
                return getDiagnosticsStatusNotificationRequest();
            } else if (requestClass.equals(FirmwareStatusNotificationRequest.class)) {
                return getFirmwareStatusNotificationRequest();
            } else if (requestClass.equals(GetDiagnosticsRequest.class)) {
                return getGetDiagnosticsRequest();
            } else if (requestClass.equals(UpdateFirmwareRequest.class)) {
                return getUpdateFirmwareRequest();
            } else if (requestClass.equals(GetLocalListVersionRequest.class)) {
                return getGetLocalListVersionRequest();
            } else if (requestClass.equals(SendLocalListRequest.class)) {
                return getSendLocalListRequest();
            } else if(requestClass.equals(TriggerMessageRequest.class)){
                return getTriggerMessageRequest();
            }else {
                return NOT_SUPPORTED;
            }
        } catch (PropertyConstraintException | JsonProcessingException e) {
            logger.error(REQUEST_CONSTRUCTION_ERROR, e);
            return REQUEST_CONSTRUCTION_ERROR;
        }
    }

    private static String getChangeAvailabilityRequest() throws PropertyConstraintException, JsonProcessingException {
        ChangeAvailabilityRequest changeAvailabilityRequest = new ChangeAvailabilityRequest();
        changeAvailabilityRequest.setConnectorId(1);
        changeAvailabilityRequest.setType(AvailabilityType.Operative);
        return objectMapper.writeValueAsString(changeAvailabilityRequest);
    }

    private static String getChangeConfigurationRequest() throws PropertyConstraintException, JsonProcessingException {
        ChangeConfigurationRequest changeConfigurationRequest = new ChangeConfigurationRequest();
        changeConfigurationRequest.setKey("AuthorizationCacheEnabled");
        changeConfigurationRequest.setValue("false");
        return objectMapper.writeValueAsString(changeConfigurationRequest);
    }

    private static String getClearCacheRequest() throws JsonProcessingException {
        ClearCacheRequest clearCacheRequest = new ClearCacheRequest();
        return objectMapper.writeValueAsString(clearCacheRequest);
    }

    private static String getDataTransferRequest() throws PropertyConstraintException, JsonProcessingException {
        DataTransferRequest dataTransferRequest = new DataTransferRequest();
        dataTransferRequest.setData("Data message");
        dataTransferRequest.setMessageId("MessageId");
        dataTransferRequest.setVendorId("VendorId");
        return objectMapper.writeValueAsString(dataTransferRequest);
    }

    private static String getGetConfigurationRequest() throws PropertyConstraintException, JsonProcessingException {
        GetConfigurationRequest getConfigurationRequest = new GetConfigurationRequest();
        getConfigurationRequest.setKey(new String[]{"AuthorizationCacheEnabled"});
        return objectMapper.writeValueAsString(getConfigurationRequest);
    }

    private static String getMeterValuesRequest() throws PropertyConstraintException, JsonProcessingException {
        SampledValue sampledValue = new SampledValue();
        sampledValue.setValue("100");
        sampledValue.setPhase("N");
        MeterValue meterValue = new MeterValue();
        meterValue.setSampledValue(new SampledValue[]{sampledValue});
        meterValue.setTimestamp(Calendar.getInstance());
        MeterValuesRequest meterValuesRequest = new MeterValuesRequest();
        meterValuesRequest.setConnectorId(1);
        meterValuesRequest.setMeterValue(new MeterValue[]{meterValue});
        meterValuesRequest.setTransactionId(123456);
        return objectMapper.writeValueAsString(meterValuesRequest);
    }

    private static String getRemoteStartTransactionRequest() throws PropertyConstraintException, JsonProcessingException {
        RemoteStartTransactionRequest remoteStartTransactionRequest = new RemoteStartTransactionRequest();
        remoteStartTransactionRequest.setConnectorId(1);
        remoteStartTransactionRequest.setIdTag("idTag");
        remoteStartTransactionRequest.setChargingProfile(new ChargingProfile());
        return objectMapper.writeValueAsString(remoteStartTransactionRequest);
    }

    private static String getRemoteStopTransactionRequest() throws PropertyConstraintException, JsonProcessingException {
        RemoteStopTransactionRequest remoteStopTransactionRequest = new RemoteStopTransactionRequest();
        remoteStopTransactionRequest.setTransactionId(123456);
        return objectMapper.writeValueAsString(remoteStopTransactionRequest);
    }

    private static String getResetRequest() throws PropertyConstraintException, JsonProcessingException {
        ResetRequest resetRequest = new ResetRequest();
        resetRequest.setType(ResetType.Soft);
        return objectMapper.writeValueAsString(resetRequest);
    }

    private static String getUnlockConnectorRequest() throws PropertyConstraintException, JsonProcessingException {
        UnlockConnectorRequest unlockConnectorRequest = new UnlockConnectorRequest();
        unlockConnectorRequest.setConnectorId(1);
        return objectMapper.writeValueAsString(unlockConnectorRequest);
    }

    private static String getDiagnosticsStatusNotificationRequest() throws PropertyConstraintException, JsonProcessingException {
        DiagnosticsStatusNotificationRequest diagnosticsStatusNotificationRequest = new DiagnosticsStatusNotificationRequest();
        diagnosticsStatusNotificationRequest.setStatus(DiagnosticsStatus.Idle);
        return objectMapper.writeValueAsString(diagnosticsStatusNotificationRequest);
    }

    private static String getFirmwareStatusNotificationRequest() throws PropertyConstraintException, JsonProcessingException {
        FirmwareStatusNotificationRequest firmwareStatusNotificationRequest = new FirmwareStatusNotificationRequest();
        firmwareStatusNotificationRequest.setStatus(FirmwareStatus.Idle);
        return objectMapper.writeValueAsString(firmwareStatusNotificationRequest);
    }

    private static String getGetDiagnosticsRequest() throws PropertyConstraintException, JsonProcessingException {
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE, 1);
        Calendar stopDate = Calendar.getInstance();
        stopDate.add(Calendar.DATE, 2);
        GetDiagnosticsRequest getDiagnosticsRequest = new GetDiagnosticsRequest();
        getDiagnosticsRequest.setLocation("ftp://localhost/downloadFolder");
        getDiagnosticsRequest.setRetries(2);
        getDiagnosticsRequest.setRetryInterval(5);
        getDiagnosticsRequest.setStartTime(startDate);
        getDiagnosticsRequest.setStopTime(stopDate);
        return objectMapper.writeValueAsString(getDiagnosticsRequest);
    }

    private static String getUpdateFirmwareRequest() throws PropertyConstraintException, JsonProcessingException {
        UpdateFirmwareRequest updateFirmwareRequest = new UpdateFirmwareRequest();
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE, 1);
        updateFirmwareRequest.setLocation("ftp://localhost/downloadFolder");
        updateFirmwareRequest.setRetries(2);
        updateFirmwareRequest.setRetryInterval(5);
        updateFirmwareRequest.setRetrieveDate(startDate);
        return objectMapper.writeValueAsString(updateFirmwareRequest);
    }

    private static String getGetLocalListVersionRequest() throws PropertyConstraintException, JsonProcessingException {
        GetLocalListVersionRequest getLocalListVersionRequest = new GetLocalListVersionRequest();
        return objectMapper.writeValueAsString(getLocalListVersionRequest);
    }

    private static String getSendLocalListRequest() throws PropertyConstraintException, JsonProcessingException {
        SendLocalListRequest sendLocalListRequest = new SendLocalListRequest();
        return objectMapper.writeValueAsString(sendLocalListRequest);
    }

    private static String getTriggerMessageRequest() throws PropertyConstraintException, JsonProcessingException {
        TriggerMessageRequest triggerMessageRequest = new TriggerMessageRequest();
        triggerMessageRequest.setConnectorId(1);
        triggerMessageRequest.setRequestedMessage(TriggerMessageRequestType.Heartbeat);
        return objectMapper.writeValueAsString(triggerMessageRequest);
    }

    public static <T extends Request> Optional<T> toRequest(String request, Class<T> requestClass){
        try {
            return Optional.of(objectMapper.readValue(request, requestClass));
        } catch (IOException e) {
            logger.error("Request parsing error", e);
            return Optional.empty();
        }
    }

    public static String toJson(Request request){
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            logger.error("Request parsing error", e);
            return "";
        }
    }

}
