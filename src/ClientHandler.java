import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {

    private final Server server;
    private static final String HOST = "localhost";
    private static final int PORT = 8000;
    private static int playerID = 0;
    private String playerIDScore;
    private Socket clientSocket;
    BufferedReader reader;
    BufferedWriter writer;

    public ClientHandler(Socket socket, Server server) throws IOException {
        clientSocket = socket;
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        playerID++;
        this.server = server;
        writer.write(playerID + "\n");
        writer.flush();
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    if (clientSocket.isClosed()) {
                        this.close();
                        break;
                    }

                    playerIDScore = String.valueOf(reader.readLine());
                    server.writeToAll(playerIDScore);
                } catch (IOException e) {
                    e.printStackTrace();
                    this.close();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void close() throws IOException {
        server.removeClient(this);
        playerID--;
        reader.close();
        writer.close();
    }
}
