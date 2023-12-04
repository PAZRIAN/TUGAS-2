import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ChatClient1{

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try {
           
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT));
            socketChannel.configureBlocking(false);

          
            Thread readerThread = new Thread(() -> {
                try {
                    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                    while (true) {
                        int bytesRead = socketChannel.read(buffer);
                        if (bytesRead > 0) {
                            buffer.flip();
                            byte[] data = new byte[buffer.remaining()];
                            buffer.get(data);
                            System.out.println("Pesan dari server: " + new String(data));
                            buffer.clear();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readerThread.start();         
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                if ("exit".equalsIgnoreCase(message)) {
                    break;
                }
                ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
                socketChannel.write(buffer);
            }
            
            socketChannel.close();
            System.exit(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}