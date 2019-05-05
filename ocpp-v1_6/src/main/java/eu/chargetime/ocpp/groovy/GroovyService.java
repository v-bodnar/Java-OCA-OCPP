package eu.chargetime.ocpp.groovy;

import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import groovy.lang.GroovyClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class GroovyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyService.class);
    private static final String LITHOS_HOME = Optional.ofNullable(System.getenv("LITHOS_HOME")).orElse("/home/bmterra/lithos");
    private static final Path SCRIPTS_FOLDER = Paths.get(LITHOS_HOME, "ocpp", "groovy");
    private static final boolean USE_SCRIPTS_FOLDER = true; //set to false to debug scripts from resources
    private final Map<Class<? extends Request>, ConfirmationSupplier<Request, Confirmation>> confirmationSuppliers =
            new HashMap<>();

    private GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

    public void loadGroovyScripts() {
        try {
            if (!SCRIPTS_FOLDER.toFile().exists()) {
                Files.createDirectories(SCRIPTS_FOLDER);
            }
            createGroovyFilesFromResources();
            loadConfirmationSuppliers();
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Could not load groovy scripts", e);
        }
    }

    private void createGroovyFilesFromResources() throws IOException, URISyntaxException {
        URL innerResourceFolderUrl = ClassLoader.getSystemClassLoader().getResource("eu/chargetime/ocpp/groovy");
        Path innerResourceFolder =
                Paths.get(Optional.ofNullable(innerResourceFolderUrl.toURI())
                        .orElseThrow(() -> new IOException(String.format("Could not create URI to file %s", innerResourceFolderUrl))));
        try (Stream<Path> stream = Files.walk(innerResourceFolder)) {
            stream.filter(path -> path.toString().endsWith("groovy"))
                    .forEach(path -> createGroovyFile(path,
                            SCRIPTS_FOLDER.resolve(path.getFileName())));
        }
    }

    private void createGroovyFile(Path source, Path destination) {
        if (!destination.toFile().exists() && source.toFile().isFile()) {
            LOGGER.debug("Creating groovy file: {}", destination);
            try (FileChannel src = new FileInputStream(source.toFile()).getChannel();
                 FileChannel dest = new FileOutputStream(destination.toFile()).getChannel()) {
                dest.transferFrom(src, 0, src.size());
                loadGroovyClass(source, destination);
            } catch (IOException e) {
                LOGGER.error(String.format("Could not create scripts file %s", source.getFileName()), e);
            }
        } else if (source.toFile().isFile()) {
            LOGGER.debug("File already exists {}", destination);
            loadGroovyClass(source, destination);
        }
    }

    private void loadGroovyClass(Path source, Path destination){
        try {
            if(USE_SCRIPTS_FOLDER) {
                groovyClassLoader.parseClass(destination.toFile());
            }else {
                groovyClassLoader.parseClass(source.toFile());
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Could not load script %s", source.getFileName()), e);
        }
    }

    private void loadConfirmationSuppliers() {
        Arrays.stream(groovyClassLoader.getLoadedClasses())
                .filter(aClass -> aClass.getGenericInterfaces().length != 0
                        && aClass.getGenericInterfaces()[0] instanceof ParameterizedType
                        && ((ParameterizedType) aClass.getGenericInterfaces()[0]).getRawType().equals(ConfirmationSupplier.class)
                )
                .forEach(aClass -> {
                    try {
                        confirmationSuppliers.put((Class<? extends Request>) ((ParameterizedType) aClass.getGenericInterfaces()[0]).getActualTypeArguments()[0],
                                (ConfirmationSupplier) aClass.newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        LOGGER.error(String.format("Could not instantiate Confirmation supplier: %s", aClass), e);
                    }
                });
    }

    public <T extends Confirmation> T getConfirmation(UUID sessionUuid, Request request) {
        try {
            return (T) Optional.ofNullable(confirmationSuppliers.get(request.getClass()))
                    .orElse((sessionUuid1, request1) -> null)
                    .getConfirmation(sessionUuid, request);
        } catch (Exception e) {
            LOGGER.error("Error in groovy confirmation supplier", e);
            return null;
        }
    }

}
