package nio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author： song.zh
 * @Date: 2019/1/3
 */
public class NioSelectorHandler implements Runnable {

    private Selector selector;

    private String threadName;

    public NioSelectorHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        threadName = Thread.currentThread().getName();
        threadName = threadName.replace("pool-1", "Server-POOL");
        System.out.println("线程:"+threadName+"，已启动！");

        while (!Thread.currentThread().isInterrupted()) {
            //一定间隔再处理，不然cpu有点受不了
            try {
                Thread.sleep(NioConfig.SLEEP_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                //阻塞，直到有关心的事件就绪
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //避免多线程环境下,遍历iterator时出现异常(HashMap.HashIterator.next()||.remove())
            synchronized (selector) {
                Set<SelectionKey> publicKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = publicKeys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (key.isAcceptable()) {
                        handleAccept(key);
                    }
                    if (key.isReadable()) {
                        handleRead(key);
                    }
                    if (key.isWritable()) {
                        handleWrite(key);
                    }
                }
            }
        }
        try {
            selector.close();
        } catch (IOException e) {
            //nothing to do
        }
    }

    /**
     * 处理接受就绪事件， 只会出现一次
     * @param key
     */
    void handleAccept(SelectionKey key) {
        //**只有在accept时，才可以强转为ServerSocketChannel， 其他阶段只能转为SocketChannel
        ServerSocketChannel serverSC = (ServerSocketChannel) key.channel();
        try {
            //只有ServerSocketChannel才有accept方法，而且只能accept一次
            SocketChannel clientSC = serverSC.accept();
            if (clientSC == null) {
                return;
            }
            clientSC.write(ByteBuffer.wrap(new String("我是服务端: " + threadName +", 已接收你的连接请求！").getBytes(NioConfig.CHARSET)));
            clientSC.configureBlocking(false).register(selector, SelectionKey.OP_READ
                    | SelectionKey.OP_WRITE, "anything you want to attach");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理读就绪事件
     *
     * @param key
     */
    void handleRead(SelectionKey key) {
        //每次只读BUFFER_SIZE个byte，直到将客户端的消息全部读完，只要还有未读完的消息，handleRead()就会被触发
        ByteBuffer buffer = ByteBuffer.allocate(NioConfig.BUFFER_SIZE);
        try {
            ((SocketChannel) key.channel()).read(buffer);
            System.out.println("server线程："+threadName+",  客户端说:" + getString(buffer));
        } catch (IOException e) {
            System.out.println("请检查连接是否已断开");
            e.printStackTrace();
        }
    }

    /**
     * 处理写就绪事件
     *
     * @param key
     */
    void handleWrite(SelectionKey key) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(NioConfig.BUFFER_SIZE);
            buffer.put(("我来自服务端线程:"+threadName).getBytes(NioConfig.CHARSET));
            buffer.flip();
            ((SocketChannel) key.channel()).write(buffer);
        } catch (IOException e) {
            System.out.println("请检查连接是否已断开");
            e.printStackTrace();
        }
    }

    /**
     * 读取buffer中的内容
     *
     * @param buffer
     * @return
     */
    public static String getString(ByteBuffer buffer) throws UnsupportedEncodingException {
        //注意这里的处理细节，只取了有内容的部分
        byte[] bytes = Arrays.copyOfRange(buffer.array(), 0, buffer.position());
        return new String(bytes, NioConfig.CHARSET);
    }
}
