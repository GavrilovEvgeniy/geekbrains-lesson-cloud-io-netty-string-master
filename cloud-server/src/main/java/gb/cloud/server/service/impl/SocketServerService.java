package gb.cloud.server.service.impl;

import gb.cloud.server.Main;
import gb.cloud.server.factory.Factory;
import gb.cloud.server.service.ServerService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServerService implements ServerService {

    private static SocketServerService instance;

    public static SocketServerService getInstance() {
        if (instance == null) {
            instance = new SocketServerService();
        }

        return instance;
    }

    @Override
    public void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Main.log.info("Server started on port " + port);

            while(true) {
                Socket clientSocket = serverSocket.accept();
                Factory.getClientService(clientSocket).startIOProcess();
                Main.log.info("New client connected");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Main.log.info("Server finished successfully");
    }

}
