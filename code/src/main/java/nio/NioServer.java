package nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NioServer {
    public static void main(String[] args) {
        startServer();
    }

    public static void startServer() {
        try {
            ServerSocketChannel channel = ServerSocketChannel.open();
            Selector selector = Selector.open();
            channel.bind(new InetSocketAddress(InetAddress.getLocalHost(), 5000));
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_ACCEPT);
            handleSelector(selector);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void  handleSelector(Selector selector) {
        Set<SelectionKey> keySet = selector.selectedKeys();
        Iterator<SelectionKey> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
            if (key.isAcceptable()) {
                System.out.println("may i be accepted ?");
                try {
                    channel.accept().configureBlocking(false).register(selector, SelectionKey.OP_READ);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (key.isReadable()) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                try {
                    ((SocketChannel)key.channel()).read(buffer);
                    System.out.println("buffer size:"+getString(buffer));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (key.isWritable()) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                buffer.put("i'm message from server!".getBytes());
                buffer.flip();
                try {
                    ((SocketChannel)key.channel()).write(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (key.isConnectable()) {
                System.out.println("connected!");
            }
        }
    }

    public static String getString(ByteBuffer buffer)
    {
        String string = "";
        try
        {
            for(int i = 0; i<buffer.position();i++){
                string += (char)buffer.get(i);
            }
            return string;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "";
        }
    }
}
