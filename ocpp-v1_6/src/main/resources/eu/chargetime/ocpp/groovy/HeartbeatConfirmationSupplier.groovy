package eu.chargetime.ocpp.groovy

import eu.chargetime.ocpp.JSONCommunicator
import eu.chargetime.ocpp.gui.ApplicationContext
import eu.chargetime.ocpp.model.SessionInformation
import eu.chargetime.ocpp.model.core.HeartbeatConfirmation
import eu.chargetime.ocpp.model.core.HeartbeatRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class HeartbeatConfirmationSupplier implements ConfirmationSupplier<HeartbeatRequest, HeartbeatConfirmation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatConfirmationSupplier.class)
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null)

    @Override
    HeartbeatConfirmation getConfirmation(UUID sessionUuid, HeartbeatRequest request) {
        SessionInformation unknownSession = new SessionInformation.Builder().Identifier("unknown").build()
        SessionInformation sessionInformation = ApplicationContext.INSTANCE.ocppServerService
                .getSessionInformation(sessionUuid).orElse(unknownSession)
        HeartbeatConfirmation confirmation = new HeartbeatConfirmation()
        confirmation.setCurrentTime(Calendar.getInstance())
        LOGGER.debug("Responding to {} from client: {} body: {}", request.getClass().simpleName, sessionInformation
                .identifier, jsonCommunicator.packPayload(confirmation))
        return confirmation
    }
}
