import java.io.*;
import java.net.*;

public class ChatClient1 {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8080);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            String userInput;

            Thread receiveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String message = in.readLine();
                            if (message != null) {
                                System.out.println("C1 Received: " + message);
                                out.flush();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receiveThread.start();
            while ((userInput = consoleInput.readLine()) != null) {
                out.println(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}