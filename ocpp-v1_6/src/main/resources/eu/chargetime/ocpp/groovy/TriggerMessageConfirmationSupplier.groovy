package eu.chargetime.ocpp.groovy

import eu.chargetime.ocpp.JSONCommunicator
import eu.chargetime.ocpp.gui.ApplicationContext
import eu.chargetime.ocpp.model.SessionInformation
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageConfirmation
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TriggerMessageConfirmationSupplier implements ConfirmationSupplier<TriggerMessageRequest, TriggerMessageConfirmation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizeConfirmationSupplier.class)
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null)

    @Override
    TriggerMessageConfirmation getConfirmation(UUID sessionUuid, TriggerMessageRequest request) {
        SessionInformation unknownSession = new SessionInformation.Builder().Identifier("unknown").build()
        SessionInformation sessionInformation = ApplicationContext.INSTANCE.ocppServerService
                .getSessionInformation(sessionUuid).orElse(unknownSession)

        TriggerMessageConfirmation confirmation = new TriggerMessageConfirmation()
        confirmation.setStatus(TriggerMessageStatus.Accepted)

        LOGGER.debug("Responding to {} from client: {} body: {}", request.getClass().simpleName, sessionInformation
                .identifier, jsonCommunicator.packPayload(confirmation))
        return confirmation
    }
}
