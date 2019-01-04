package nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.Iterator;

/**
 * @Author： song.zh
 * @Date: 2019/1/3
 */
public class SelectorHandler implements Runnable {

    private Selector selector;

    public SelectorHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            //一定间隔再处理，不然cpu受不了
            try {
                Thread.sleep(NIOConfig.SLEEP_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                //阻塞，直到有关心的事件就绪
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                //attachment等于register时，传入的内容， 这里 attachment='song'
                Object attachment = key.attachment();
                if (attachment != null) {
                    System.out.println(attachment.toString());
                }

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
            //只有ServerSocketChannel才有accept方法
            SocketChannel clientSC = serverSC.accept();
            clientSC.write(ByteBuffer.wrap(new String("i come from server").getBytes()));
            clientSC.configureBlocking(false).register(selector, SelectionKey.OP_READ
                    | SelectionKey.OP_WRITE, "song");
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
        //每次只读128个byte，直到将客户端的消息全部读完，只要还有未读完的消息，handleRead()就会被触发
        ByteBuffer buffer = ByteBuffer.allocate(128);
        try {
            ((SocketChannel) key.channel()).read(buffer);
            System.out.println(Instant.now() + ",the client say:" + getString(buffer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理写就绪事件
     *
     * @param key
     */
    void handleWrite(SelectionKey key) {
        ByteBuffer buffer = ByteBuffer.allocate(50);
        buffer.put("i'm message from server!".getBytes());
        buffer.flip();
        try {
            ((SocketChannel) key.channel()).write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取buffer中的内容
     *
     * @param buffer
     * @return
     */
    public String getString(ByteBuffer buffer) {
        String string = "";
        try {
            for (int i = 0; i < buffer.position(); i++) {
                string += (char) buffer.get(i);
            }
            return string;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
}
