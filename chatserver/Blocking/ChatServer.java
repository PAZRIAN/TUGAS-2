import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Menunggu Client...");


            ExecutorService executorService = Executors.newFixedThreadPool(2);

            while (true) {
                Socket clientSocket1 = serverSocket.accept();
                System.out.println("Client 1 terhubung.");

                Socket clientSocket2 = serverSocket.accept();
                System.out.println("Client 2 terhubung.");

                executorService.submit(new ClientHandler(clientSocket1, clientSocket2));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private final BufferedReader in1;
    private final PrintWriter out1;
    private final BufferedReader in2;
    private final PrintWriter out2;


    public ClientHandler(Socket clientSocket1, Socket clientSocket2) throws IOException {
        this.in1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
        this.out1 = new PrintWriter(clientSocket1.getOutputStream(), true);
        this.in2 = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));
        this.out2 = new PrintWriter(clientSocket2.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message1 = in1.readLine();
                if (message1 != null) {
                    System.out.println("Received: " + message1);

                    out2.println(message1);
                    String message2 = in2.readLine();
                    if (message2 != null) {
                        System.out.println("Recived: " + message2);
                        out1.println(message2);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
          } 
     }
}