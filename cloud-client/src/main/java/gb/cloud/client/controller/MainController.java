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

    public static String fullPath1 = "";
    public static String fullPath2 = "";

    public static String gotResult = "";
    public static String userName = "";

    public static String curCloudPath = MainApplication.cloudDirectory;
    public static String curLocalPath = MainApplication.localDirectory;

    public static final Logger log = Logger.getLogger(MainController.class);

    public static int loggedLen = 7;
    public static String fileType = "FILE";
    public static String slashS = "\\";
    public static int numMouseClicks = 2;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networkService = Factory.getNetworkService();
        createCommandResultHandler();

        showDir(localContent, curLocalPath);
        showDir(cloudContent, curCloudPath);
    }

    private void createCommandResultHandler() {
        new Thread(() -> {
            byte[] buffer = new byte[Integer.parseInt(MainApplication.bufferLength)];
            while (true) {
                int countReadBytes = networkService.readCommandResult(buffer);
                String resultCommand = new String(buffer, 0, countReadBytes);
                gotResult = resultCommand;
                Platform.runLater(() -> commandResultTextArea.appendText(resultCommand + System.lineSeparator()));
            }

        }).start();
    }

    public void sendCommand(ActionEvent actionEvent) {

        Date startTime = new Date();

        networkService.sendCommand(commandTextField.getText().trim());
        commandTextField.clear();

        waitCommandResult();

        Date endTime = new Date();
        long deltaTime = endTime.getTime() - startTime.getTime();
        log.info("Time for executing a command " + deltaTime + " ms");

        if (gotResult.startsWith("Logged")) {
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

        Date endTime = new Date();
        long deltaTime = endTime.getTime() - startTime.getTime();
        log.info("Time for downloading from Cloud " + deltaTime + " ms");

        showDir(localContent, curLocalPath);
    }

    public void btnUpload(ActionEvent actionEvent) {

        Date startTime = new Date();

        preparePath(localContent, curLocalPath, curCloudPath);
        networkService.sendCommand("tocloud=" + fullPath1 + "=" + fullPath2);

        waitCommandResult();
        gotResult = "";

        Date endTime = new Date();
        long deltaTime = endTime.getTime() - startTime.getTime();
        log.info("Time for uploading into Cloud " + deltaTime + " ms");

        showDir(cloudContent, curCloudPath);
    }

    private void showDir(ListView<String> content, String path) {

        Date startTime = new Date();

        networkService.sendCommand("ls=" + path + "=" + userName);

        waitCommandResult();
        Date endTime = new Date();

        long deltaTime = endTime.getTime() - startTime.getTime();
        log.info("Time for showing directory " + deltaTime + " ms");

        String In = gotResult;
        String[] cFiles = In.split(System.lineSeparator());

        content.setItems(FXCollections.observableArrayList(cFiles));
        content.getSelectionModel().selectFirst();

        gotResult = "";

    }

    private String changeDir(ListView<String> content, String curPath) {
        String changedPath;
        String currentItemSelected = content.getSelectionModel().getSelectedItem();
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

        if (mouseEvent.getClickCount() == numMouseClicks) {

            if (localContent.isFocused())
                curLocalPath = changeDir(localContent, curLocalPath);
            if (cloudContent.isFocused())
                curCloudPath = changeDir(cloudContent, curCloudPath);

        }
    }

}
