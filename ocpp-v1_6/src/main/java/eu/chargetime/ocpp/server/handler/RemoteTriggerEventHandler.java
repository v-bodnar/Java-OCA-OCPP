package eu.chargetime.ocpp.server.handler;

import eu.chargetime.ocpp.JSONCommunicator;
import eu.chargetime.ocpp.feature.profile.ClientRemoteTriggerHandler;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageConfirmation;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteTriggerEventHandler implements ClientRemoteTriggerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteTriggerEventHandler.class);
    private final JSONCommunicator jsonCommunicator = new JSONCommunicator(null);


    @Override
    public TriggerMessageConfirmation handleTriggerMessageRequest(TriggerMessageRequest request) {
        LOGGER.debug("{} - {}", request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return null;
    }
}
