package eu.chargetime.ocpp.gui;

import eu.chargetime.ocpp.rest.WebServer;
import eu.chargetime.ocpp.server.OcppServerService;

public class ApplicationContext {
    public static final ApplicationContext INSTANCE = new ApplicationContext();
    private OcppServerService ocppServerService = new OcppServerService();
    private WebServer webServer = new WebServer(9099);

    public OcppServerService getOcppServerService() {
        return ocppServerService;
    }

    public WebServer getWebServer() {
        return webServer;
    }
}
