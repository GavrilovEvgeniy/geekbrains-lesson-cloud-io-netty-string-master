package gb.cloud.server.service.impl.utils;

import gb.cloud.server.Main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ServerProperties {

    public static String nameServerProps = "cloud-server/src/main/resources/server.properties";

    private static final Properties property = new Properties();

    public static void loadProperties() {

        try {
            FileInputStream fis = new FileInputStream(nameServerProps);
            property.load(fis);

            Main.dbconnection = property.getProperty("cld.db_connection");
            Main.dblogin = property.getProperty("cld.db_login");
            Main.dbpassword = property.getProperty("cld.db_password");
            Main.serverport = property.getProperty("cld.server_port");

            Main.log.info(Main.dbconnection + " " + Main.dblogin + " " + Main.dbpassword + " " + Main.serverport);
            System.out.println(Main.dbconnection + " " + Main.dblogin + " " + Main.dbpassword + " " + Main.serverport);

        } catch (IOException e) {
            Main.log.error("Error, file not found.");
        }
    }


}
