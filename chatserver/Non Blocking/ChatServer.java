import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ChatServer {

    private static final int PORT = 12345;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try {
            Selector selector = Selector.open();

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server is running.");

            while (true) {
                selector.select();

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    if (key.isAcceptable()) {
                        
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                        SocketChannel clientChannel = serverChannel.accept();
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);

                        System.out.println("Koneksi baru diterima dari " + clientChannel.getRemoteAddress());
                    } else if (key.isReadable()) {
                        
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                        int bytesRead = clientChannel.read(buffer);

                        if (bytesRead == -1) {
                           
                            System.out.println("Koneksi ditutup oleh " + clientChannel.getRemoteAddress());
                            clientChannel.close();
                        } else if (bytesRead > 0) {
                           
                            buffer.flip();
                            byte[] data = new byte[buffer.remaining()];
                            buffer.get(data);

                            for (SelectionKey otherKey : selector.keys()) {
                                if (otherKey.isValid() && otherKey.channel() instanceof SocketChannel) {
                                    SocketChannel otherChannel = (SocketChannel) otherKey.channel();
                                    if (otherChannel != clientChannel) {
                                        otherChannel.write(ByteBuffer.wrap(data));
                                    }
                                }
                            }
                        }
                    }

                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}