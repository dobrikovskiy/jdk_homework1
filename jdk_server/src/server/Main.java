package server;

import server.client.Client;
import server.server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        new Client(server);
        new Client(server);
    }
}