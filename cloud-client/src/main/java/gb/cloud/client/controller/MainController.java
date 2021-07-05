package gb.cloud.client.controller;

import gb.cloud.client.MainApplication;
import gb.cloud.client.factory.Factory;
import gb.cloud.client.service.NetworkService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.SneakyThrows;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

public class MainController implements Initializable {

    public TextField commandTextField;
    public TextArea commandResultTextArea;

    public NetworkService networkService;

    @FXML
    public ListView<String> cloudContent;
    @FXML
    public ListView<String> localContent;
    @FXML
    public Button btnDownload;
    @FXML
    public Button btnUpload;

    private static String fullPath1 = "";
    private static String fullPath2 = "";

    private static String gotResult = "";
    private static String userName = "";

    private static String curCloudPath = MainApplication.cloudDirectory;
    private static String curLocalPath = MainApplication.localDirectory;

    public static final Logger log = Logger.getLogger(MainController.class);

    private static final String localMarker = "LOCAL";
    private static final String cloudMarker = "CLOUD";
    private static final String slashS = "\\";

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networkService = Factory.getNetworkService();
        createCommandResultHandler();

        showDir(localContent, curLocalPath);
        showDir(cloudContent, curCloudPath);
    }

    private void updateListView(ListView<String> content, String[] cFs) {
        cFs = (String[]) ArrayUtils.remove(cFs, 0);
        content.setItems(FXCollections.observableArrayList(cFs));
        content.getSelectionModel().selectFirst();
    }

    private void createCommandResultHandler() {
        new Thread(() -> {
            byte[] buffer = new byte[Integer.parseInt(MainApplication.bufferLength)];
            while (true) {
                int countReadBytes = networkService.readCommandResult(buffer);
                String resultCommand = new String(buffer, 0, countReadBytes);
                gotResult = resultCommand;
                Platform.runLater(() -> commandResultTextArea.appendText(resultCommand + System.lineSeparator()));

                Platform.runLater(() -> {
                    String[] cFiles = resultCommand.split(System.lineSeparator());

                    if (cFiles[0].equals(localMarker)) {
                        updateListView(localContent, cFiles);
                    }
                    if (cFiles[0].equals(cloudMarker)) {
                        updateListView(cloudContent, cFiles);
                    }
                });
            }

        }).start();
    }

    public void sendCommand(ActionEvent actionEvent) {

        Date startTime = new Date();

        networkService.sendCommand(commandTextField.getText().trim());
        commandTextField.clear();

        waitCommandResult();

        long deltaTime = measureTime(startTime);
        log.info("Time for executing a command " + deltaTime + " ms");

        if (gotResult.startsWith("Logged")) {
            int loggedLen = 7;
            userName = gotResult.substring(loggedLen);
        }

        gotResult = "";

        showDir(localContent, curLocalPath);
        showDir(cloudContent, curCloudPath);
    }

    public void shutdown() {
        networkService.closeConnection();
    }

    private void waitCommandResult()  {
            long z = 0;
            final long bigdelta = 10000000;

            while (gotResult.equals("")) {
                z++;
                if (z == bigdelta) {
                    log.info("Waiting for server's answer " + new Date().getTime());
                    z = 0;
                }
            }
    }

    private long measureTime(Date startTime ) {
        Date endTime = new Date();
        return endTime.getTime() - startTime.getTime();
    }

    private void preparePath(ListView<String> content, String path1, String path2)  {
        String currentItemSelected = content.getSelectionModel().getSelectedItem();
        String onlyFileName = currentItemSelected.substring(0, currentItemSelected.indexOf(" | "));
        fullPath1 = path1 + slashS + onlyFileName;
        fullPath2 = path2 + slashS + onlyFileName;
    }

    public void btnDownload(ActionEvent actionEvent) {

        Date startTime = new Date();

        preparePath(cloudContent, curCloudPath, curLocalPath);
        networkService.sendCommand("tocloud=" + fullPath1 + "=" + fullPath2);

        waitCommandResult();
        gotResult = "";

        long deltaTime = measureTime(startTime);
        log.info("Time for downloading from Cloud " + deltaTime + " ms");

        showDir(localContent, curLocalPath);
    }

    public void btnUpload(ActionEvent actionEvent) {

        Date startTime = new Date();

        preparePath(localContent, curLocalPath, curCloudPath);
        networkService.sendCommand("tocloud=" + fullPath1 + "=" + fullPath2);

        waitCommandResult();
        gotResult = "";

        long deltaTime = measureTime(startTime);
        log.info("Time for uploading into Cloud " + deltaTime + " ms");

        showDir(cloudContent, curCloudPath);
    }

    private void showDir(ListView<String> content, String path) {
        networkService.sendCommand("ls=" + path + "=" + userName);
        waitCommandResult();
        gotResult = "";
    }

    private String changeDir(ListView<String> content, String curPath) {
        String changedPath;
        String currentItemSelected = content.getSelectionModel().getSelectedItem();
        String fileType = "FILE";
        if (currentItemSelected.contains(fileType))
            return null;
        String onlyFileName = currentItemSelected.substring(0, currentItemSelected.indexOf(" | "));

        if (onlyFileName.equals("..")) {
            int lastSlash = curPath.lastIndexOf(slashS);
            changedPath = curPath.substring(0, lastSlash);
        }
        else changedPath = curPath + slashS + onlyFileName;

        showDir(content, changedPath);
        return changedPath;
    }


    public void handle(javafx.scene.input.MouseEvent mouseEvent) {

        int numMouseClicks = 2;
        if (mouseEvent.getClickCount() == numMouseClicks) {

            if (localContent.isFocused())
                curLocalPath = changeDir(localContent, curLocalPath);
            if (cloudContent.isFocused())
                curCloudPath = changeDir(cloudContent, curCloudPath);

        }
    }

}
