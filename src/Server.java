import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    static final int PORT = 8000;
    private List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private List<PrintWriter> clientWriters = Collections.synchronizedList(new ArrayList<>());
    public Server() {
        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started...");
            while (true) {
                clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);

                OutputStream out = clientSocket.getOutputStream();
                PrintWriter writer = new PrintWriter(out);
                clientWriters.add(writer);

                new Thread(client).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println("Server stopped...");
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public void writeToAll(String message) {
        try {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}