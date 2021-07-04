package gb.cloud.server.service;

import java.io.IOException;
import java.sql.SQLException;

public interface CommandDictionaryService {

    String processCommand(String command) throws IOException, SQLException, ClassNotFoundException;

}
