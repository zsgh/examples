package nio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author： song.zh
 * @Date: 2019/1/2
 */
public class IoClient {

    public static void main(String[] args) {

        ExecutorService nioClientPool = new ThreadPoolExecutor(10, 100,
                60L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new ThreadPoolExecutor.AbortPolicy());
        for (int i = 0; i< NioConfig.THREAD_NUM; i++) {

            //you also could choose the NioClient.
            nioClientPool.execute(new BioClient());
        }

    }

    /**
     * Blocking io client
     */
    static class BioClient implements Runnable {

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            threadName = threadName.replace("pool-1", "BioClient-POOL");
            System.out.println("线程:"+threadName+"，已启动！");
            try {
                Socket socket = new Socket("127.0.0.1", NioConfig.PORT);
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                ByteArrayOutputStream result = new ByteArrayOutputStream();

                while (!Thread.currentThread().isInterrupted()) {
                    printWriter.println("我来自传统客户端"+Instant.now());
                    printWriter.flush();
                    byte[] buffer = new byte[NioConfig.BUFFER_SIZE];
                    socket.getInputStream().read(buffer);
                    result.write(buffer);
                    String msg = result.toString(NioConfig.CHARSET);
                    result.reset();
                    System.out.println("client线程:"+threadName + ", server说: " + msg);
                    Thread.sleep(NioConfig.SLEEP_MILLIS);
                }
            } catch (IOException | InterruptedException e) {

            }
        }
    }


    /**
     * nio 客户端，跟服务端比较类似
     */
    static class NioClient implements Runnable {

        @Override
        public void run() {
            try {
                String threadName = Thread.currentThread().getName();
                threadName = threadName.replace("pool-1", "BioClient-POOL");
                System.out.println("client thread："+threadName+"已启动！");
                //获取socket通道, 注意这里是SocketChannel，而不是ServerSocketChannel
                SocketChannel channel = SocketChannel.open();
                channel.configureBlocking(false);
                //客户端使用connect()方法
                channel.connect(new InetSocketAddress(InetAddress.getLocalHost(), NioConfig.PORT));
                //客户端只能是SelectionKey.OP_CONNECT事件
                Selector selector = Selector.open();
                channel.register(selector, SelectionKey.OP_CONNECT);

                while (true) {
                    //选择注册过的io操作的事件(第一次为SelectionKey.OP_CONNECT)
                    try {
                        Thread.sleep(NioConfig.SLEEP_MILLIS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    selector.select();
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        //删除已选的key，防止重复处理
                        it.remove();
                        if (key.isConnectable()) {
                            SocketChannel channelConnect = (SocketChannel) key.channel();

                            //如果正在连接，则完成连接
                            if (channelConnect.isConnectionPending()) {
                                channelConnect.finishConnect();
                            }

                            channelConnect.configureBlocking(false);
                            //向服务器发送一条消息, 注意统一编码
                            channelConnect.write(ByteBuffer.wrap("初次见面多多关照！".getBytes(NioConfig.CHARSET)));
                            //连接成功后，注册接收服务器消息的事件
                            channelConnect.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        }
                        if (key.isReadable()) {
                            SocketChannel channelRead = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(NioConfig.BUFFER_SIZE);
                            channelRead.read(buffer);
                            System.out.println("client线程："+threadName+ " 服务端说: " + NioSelectorHandler.getString(buffer));
                        }
                        if (key.isWritable()) {
                            SocketChannel channelWrite = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(NioConfig.BUFFER_SIZE);
                            buffer.put(("我来自NIO客户端，线程："+threadName + "now："+Instant.now()).getBytes(NioConfig.CHARSET));
                            buffer.flip();
                            channelWrite.write(buffer);
                        }
                    }
                }
            } catch (IOException e) {

            }
        }
    }

}
