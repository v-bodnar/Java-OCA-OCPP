package eu.chargetime.ocpp.rest;

import eu.chargetime.ocpp.JSONServerSample;
import eu.chargetime.ocpp.NotConnectedException;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.ChangeAvailabilityRequest;
import eu.chargetime.ocpp.model.core.ChangeConfigurationRequest;
import eu.chargetime.ocpp.model.core.ResetRequest;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestAPI {
    private final Logger logger = LoggerFactory.getLogger(RestAPI.class);
    @POST
    @Path("send-reset-request")
    public Response sendResetRequest(ResetRequest resetRequest) {
        return sendRequest(resetRequest);
    }

    @POST
    @Path("send-get-diagnostics")
    public Response sendGetDiagnostics(GetDiagnosticsRequest getDiagnosticsRequest) {
        return sendRequest(getDiagnosticsRequest);
    }

    @POST
    @Path("send-change-availability-request")
    public Response sendChangeAvailabilityRequest(ChangeAvailabilityRequest changeAvailabilityRequest) {
        return sendRequest(changeAvailabilityRequest);
    }

    @POST
    @Path("send-change-configuration-request")
    public Response sendChangeConfigurationRequest(ChangeConfigurationRequest changeConfigurationRequest) {
        return sendRequest(changeConfigurationRequest);
    }

    private Response sendRequest(Request request) {
        try {
            JSONServerSample.sendToAll(request);
            return Response.ok().build();
        } catch (NotConnectedException | OccurenceConstraintException | UnsupportedFeatureException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }
}
