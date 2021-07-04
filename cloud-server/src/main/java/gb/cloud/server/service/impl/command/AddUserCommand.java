package gb.cloud.server.service.impl.command;

import gb.cloud.server.Main;
import gb.cloud.server.service.CommandService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

import gb.cloud.server.service.impl.workwithdb.WorkWithDB;

public class AddUserCommand implements CommandService {

    public static String privateCloudDir = "Cloud\\___private___";

    private static String makePrivateDir(String userName, String resultAdd) {
        String userDirName = privateCloudDir + userName;
        File userDir = new File(userDirName);
        if (resultAdd.equals("Success")) {
            if (!userDir.exists()) userDir.mkdir();
            return userDirName;
        }
        if (!userDir.exists()) userDir.mkdir();
        return resultAdd;
    }

    @Override
    public String processCommand(String command) throws IOException, SQLException, ClassNotFoundException {

        final int requirementCountCommandParts = 3;

        String[] actualCommandParts = command.split("=", requirementCountCommandParts);
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command add is not correct");
        }

        Main.log.info(Arrays.toString(actualCommandParts));

        String resultS = WorkWithDB.addToDataBase(actualCommandParts[1], actualCommandParts[2]);

        return makePrivateDir(actualCommandParts[1], resultS);
    }

    @Override
    public String getCommand() {
        return "add";
    }
}
