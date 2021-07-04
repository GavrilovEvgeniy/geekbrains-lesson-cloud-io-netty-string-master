package gb.cloud.client.service.impl.utils;

import gb.cloud.client.MainApplication;
import gb.cloud.client.controller.MainController;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ClientProperties {

    public static String pathClientProps = "cloud-client/src/main/resources/client.properties";

    public static void loadProperties() {
        FileInputStream fis;
        Properties property = new Properties();

        try {
            fis = new FileInputStream(pathClientProps);
            property.load(fis);

            MainApplication.cloudDirectory = property.getProperty("cld.directory");
            MainApplication.localDirectory = property.getProperty("local.directory");
            MainApplication.bufferLength = property.getProperty("buffer.length");
            MainApplication.serverHost = property.getProperty("server.host");
            MainApplication.serverPort = property.getProperty("server.port");

            MainController.log.info(MainApplication.cloudDirectory + " " + MainApplication.localDirectory + " " + MainApplication.bufferLength + " " + MainApplication.serverHost + " " + MainApplication.serverPort);
            System.out.println(MainApplication.cloudDirectory + " " + MainApplication.localDirectory + " " + MainApplication.bufferLength + " " + MainApplication.serverHost + " " + MainApplication.serverPort);

        } catch (IOException e) {
            MainController.log.error("Error, file not found.");
        }
    }


}