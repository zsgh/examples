package nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @Author： song.zh
 * @Date: 2019/1/2
 */
public class IOClient {

    public static void main(String[] args) throws IOException {
        init();
//        connect();
    }

    static void connect() {
        Socket socket= null;
        try {
            socket = new Socket("127.0.0.1",5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("连接成功");
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //下面这种写法，不用关闭客户端，服务器端也是可以收到的
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        printWriter.println("hi");
        printWriter.flush();
    }

    public static void init() throws IOException{
        //获取socket通道
        SocketChannel channel = SocketChannel.open();

        channel.configureBlocking(false);
        //获得通道管理器
        Selector selector= Selector.open();

        //客户端连接服务器，需要调用channel.finishConnect();才能实际完成连接。
        channel.connect(new InetSocketAddress(InetAddress.getLocalHost(), NIOConfig.PORT));
        //为该通道注册SelectionKey.OP_CONNECT事件
        channel.register(selector, SelectionKey.OP_CONNECT);


        while(true){
            //选择注册过的io操作的事件(第一次为SelectionKey.OP_CONNECT)
            try {
                Thread.sleep(NIOConfig.SLEEP_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            selector.select();
            Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
            while(ite.hasNext()){
                SelectionKey key = ite.next();
                //删除已选的key，防止重复处理
                ite.remove();
                if(key.isConnectable()){
                    SocketChannel channel1=(SocketChannel)key.channel();

                    //如果正在连接，则完成连接
                    if(channel1.isConnectionPending()){
                        channel1.finishConnect();
                    }

                    channel1.configureBlocking(false);
                    //向服务器发送消息
                    channel1.write(ByteBuffer.wrap(new String("i'm from client").getBytes()));

                    //连接成功后，注册接收服务器消息的事件
                    channel1.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
                    System.out.println("客户端连接成功");
                }else if(key.isReadable()){
                    SocketChannel channel2 = (SocketChannel)key.channel();

                    ByteBuffer buffer = ByteBuffer.allocate(25);
                    channel2.read(buffer);
                    String message = new String(buffer.array());

                    System.out.println("recevie message from server:, size:" + buffer.position() + " msg: " + message);
//
                } else if(key.isWritable()) {
                    SocketChannel channel2 = (SocketChannel)key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(500);
                    buffer.put("i'm from client".getBytes());
                    buffer.flip();
                    channel2.write(buffer);
                }
            }
        }
    }
}
