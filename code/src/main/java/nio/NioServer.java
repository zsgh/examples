package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * @author song.zh
 */
public class NioServer {

    public static void main(String[] args) {
        startServer();
    }

    public static void startServer() {


        try {
            //ServerSocketChannel只有一个
            ServerSocketChannel channel = ServerSocketChannel.open();
            Selector s1 = Selector.open();
            Selector s2 = Selector.open();
            Selector s3 = Selector.open();

            channel.bind(new InetSocketAddress(NIOConfig.PORT));

            //这里必须设置为false，否则就不是NIO了
            channel.configureBlocking(false);

            /**
             * 一个channel是可以注册多个selector的
             * 服务端的初始状态是OP_ACCEPT，而客户端是OP_CONNECT
             * 连接尚未建立之前，只能注册初始状态，这里是服务端，所以只能注册OP_ACCEPT
             */
            channel.register(s1, SelectionKey.OP_ACCEPT);
            channel.register(s2, SelectionKey.OP_ACCEPT);
            channel.register(s3, SelectionKey.OP_ACCEPT);


            handleSelector(s1);
            handleSelector(s2);
            handleSelector(s3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handleSelector(Selector selector) {
        new Thread(new SelectorHandler(selector)).start();
    }
}
