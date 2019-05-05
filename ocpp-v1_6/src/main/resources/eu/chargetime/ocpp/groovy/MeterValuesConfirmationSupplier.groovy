package eu.chargetime.ocpp.groovy

import eu.chargetime.ocpp.JSONCommunicator
import eu.chargetime.ocpp.gui.ApplicationContext
import eu.chargetime.ocpp.model.SessionInformation
import eu.chargetime.ocpp.model.core.MeterValuesConfirmation
import eu.chargetime.ocpp.model.core.MeterValuesRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MeterValuesConfirmationSupplier implements ConfirmationSupplier<MeterValuesRequest, MeterValuesConfirmation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeterValuesConfirmationSupplier.class)
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null)

    @Override
    MeterValuesConfirmation getConfirmation(UUID sessionUuid, MeterValuesRequest request) {
        SessionInformation unknownSession = new SessionInformation.Builder().Identifier("unknown").build()
        SessionInformation sessionInformation = ApplicationContext.INSTANCE.ocppServerService
                .getSessionInformation(sessionUuid).orElse(unknownSession)
        MeterValuesConfirmation confirmation = new MeterValuesConfirmation()
        LOGGER.debug("Responding to {} from client: {} body: {}", request.getClass().simpleName, sessionInformation
                .identifier, jsonCommunicator.packPayload(confirmation))
        return confirmation
    }
}
