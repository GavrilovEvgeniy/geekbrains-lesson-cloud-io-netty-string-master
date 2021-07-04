package gb.cloud.server.service;

import java.io.IOException;
import java.sql.SQLException;

public interface CommandService {

    String processCommand(String command) throws IOException, SQLException, ClassNotFoundException;

    String getCommand();

}
