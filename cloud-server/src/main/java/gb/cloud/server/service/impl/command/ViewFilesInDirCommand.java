package gb.cloud.server.service.impl.command;

import gb.cloud.server.Main;
import gb.cloud.server.service.CommandService;

import java.io.File;
import java.util.Objects;

public class ViewFilesInDirCommand implements CommandService {

    public static String loggedName = "___";
    public static String levelUp = ".. | UP ";
    public static String cloudDir = "Cloud";
    public static String localDir = "C:\\";
    public static String privateDir = "___private___";
    public static int cloudLen = 5;
    public static int localLen = 4;
    public static int privateLen = 13;

    public String processCommand(String command) {
        final int requirementCountCommandParts = 3;

        String[] actualCommandParts = command.split("=", requirementCountCommandParts);
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }

        loggedName = actualCommandParts[2];
        return process(actualCommandParts[1]);
    }

    private static void addLevelUp(StringBuilder builder, String dir, String startDir,  int len) {
        if (dir.startsWith(startDir) & dir.length() > len) {
            builder.append(levelUp).append(System.lineSeparator());
        }
    }

    private static void showPrivateDir(File dir, StringBuilder builder, String logName) {
        for (File childFile : Objects.requireNonNull(dir.listFiles())) {
            String typeFile = getTypeFile(childFile);
            if (childFile.getName().startsWith(privateDir)) {
                String privateName = childFile.getName().substring(privateLen);

                if (logName.equals(privateName)) {

                    Main.log.info("Show private folder " + childFile.getName());

                    builder.append(childFile.getName()).append(" | ").append(typeFile).append(System.lineSeparator());
                }
            } else {
                builder.append(childFile.getName()).append(" | ").append(typeFile).append(System.lineSeparator());
            }

        }
    }

    private static String process(String dirPath) {
        File directory = new File(dirPath);

        if (!directory.exists()) {
            return "Directory is not exists";
        }

        StringBuilder builder = new StringBuilder();
        addLevelUp(builder, dirPath, cloudDir, cloudLen);
        addLevelUp(builder, dirPath, localDir, localLen);

        showPrivateDir(directory, builder, loggedName);

        return builder.toString();
    }

    private static String getTypeFile(File childFile) {
        return childFile.isDirectory() ? "DIR" : "FILE";
    }

    public String getCommand() {
        return "ls";
    }

}
