package gb.cloud.client;

import gb.cloud.client.controller.MainController;
import gb.cloud.client.service.impl.utils.ClientProperties;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    public static String cloudDirectory;
    public static String localDirectory;
    public static String bufferLength;
    public static String serverHost;
    public static String serverPort;

    public static String formPath = "view/mainWindow.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception {

        ClientProperties.loadProperties();

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(formPath));
        Parent parent = loader.load();
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("Cloud Client");
        primaryStage.setResizable(true);

        MainController controller = loader.getController();
        primaryStage.setOnCloseRequest((event) -> controller.shutdown());
        primaryStage.show();
    }
}
