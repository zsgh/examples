package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.*;

/**
 * @author song.zh
 */
public class NioServer {


    public static void main(String[] args) {
        startServer();
    }


    /**
     * 启动NIO server端
     */
    public static void startServer() {
        try {
            //ServerSocketChannel只有一个
            ServerSocketChannel channel = ServerSocketChannel.open();
            Selector s1 = Selector.open();
            Selector s2 = Selector.open();
            Selector s3 = Selector.open();

            channel.bind(new InetSocketAddress(NioConfig.PORT));

            //这里必须设置为false，否则就不是NIO了
            channel.configureBlocking(false);

            /**
             * 这里之所以注册3个selector，是为了说明：一个channel是可以注册多个selector的
             * 服务端的初始状态是OP_ACCEPT，而客户端是OP_CONNECT
             * 连接尚未建立之前，只能注册初始状态，这里是服务端，所以只能注册OP_ACCEPT
             */
            channel.register(s1, SelectionKey.OP_ACCEPT);
            channel.register(s2, SelectionKey.OP_ACCEPT);
            channel.register(s3, SelectionKey.OP_ACCEPT);


            ExecutorService nioServerPool = new ThreadPoolExecutor(NioConfig.THREAD_NUM, NioConfig.THREAD_NUM,
                    0L, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(NioConfig.THREAD_NUM), new ThreadPoolExecutor.DiscardOldestPolicy());

            for (int i=1; i<NioConfig.THREAD_NUM; i++) {
                nioServerPool.execute(new NioSelectorHandler(s1));
                nioServerPool.execute(new NioSelectorHandler(s2));
                nioServerPool.execute(new NioSelectorHandler(s3));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
