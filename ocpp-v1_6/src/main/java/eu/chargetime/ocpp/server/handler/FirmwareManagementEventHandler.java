package eu.chargetime.ocpp.server.handler;

import eu.chargetime.ocpp.JSONCommunicator;
import eu.chargetime.ocpp.feature.profile.ClientFirmwareManagementEventHandler;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationConfirmation;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationConfirmation;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsConfirmation;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsRequest;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareConfirmation;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareRequest;
import eu.chargetime.ocpp.server.OcppServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirmwareManagementEventHandler implements ClientFirmwareManagementEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FirmwareManagementEventHandler.class);
    private final JSONCommunicator jsonCommunicator = new JSONCommunicator(null);

    @Override
    public GetDiagnosticsConfirmation handleGetDiagnosticsRequest(GetDiagnosticsRequest request) {
        LOGGER.debug(request.getClass().getSimpleName() + " - " + jsonCommunicator.packPayload(request));
        return null;
    }

    @Override
    public DiagnosticsStatusNotificationConfirmation handleDiagnosticsStatusNotificationRequest(DiagnosticsStatusNotificationRequest request) {
        LOGGER.debug(request.getClass().getSimpleName() + " - " + jsonCommunicator.packPayload(request));
        return new DiagnosticsStatusNotificationConfirmation();
    }

    @Override
    public FirmwareStatusNotificationConfirmation handleFirmwareStatusNotificationRequest(FirmwareStatusNotificationRequest request) {
        LOGGER.debug(request.getClass().getSimpleName() + " - " + jsonCommunicator.packPayload(request));
        return null;
    }

    @Override
    public UpdateFirmwareConfirmation handleUpdateFirmwareRequest(UpdateFirmwareRequest request) {
        LOGGER.debug(request.getClass().getSimpleName() + " - " + jsonCommunicator.packPayload(request));
        return null;
    }
}
