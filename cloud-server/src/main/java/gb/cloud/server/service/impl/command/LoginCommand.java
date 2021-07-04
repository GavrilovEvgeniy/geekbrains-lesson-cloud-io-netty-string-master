package gb.cloud.server.service.impl.command;

import gb.cloud.server.Main;
import gb.cloud.server.service.CommandService;
import gb.cloud.server.service.impl.workwithdb.WorkWithDB;

import java.sql.SQLException;
import java.util.Arrays;

public class LoginCommand implements CommandService {

    @Override
    public String processCommand(String command) throws SQLException, ClassNotFoundException {

        final int requirementCountCommandParts = 3;

        String[] actualCommandParts = command.split("=", requirementCountCommandParts);
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command login is not correct");
        }
        Main.log.info(Arrays.toString(actualCommandParts));

        return WorkWithDB.findUserInDB(actualCommandParts[1], actualCommandParts[2]);

    }

    @Override
    public String getCommand() {
        return "login";
    }
}
