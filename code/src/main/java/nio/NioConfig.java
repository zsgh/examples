package nio;

/**
 * @Author： song.zh
 * @Date: 2019/1/3
 */
public class NioConfig {

    /**
     * 服务端启动的端口号，当然也是客户端连接的端口号
     */
    public static final Integer PORT = 5000;

    /**
     * 处理线程休眠的时间，即多长时间处理一次就绪事件
     */
    public static final Integer SLEEP_MILLIS = 1500;

    /**
     * 字符编码
     */
    public static final String CHARSET = "UTF-8";

    /**
     * 同时开启的线程数量
     */
    public static final Integer THREAD_NUM = 3;

    /**
     * 缓存大小
     */
    public static final Integer BUFFER_SIZE = 200;
}
