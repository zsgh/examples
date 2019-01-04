package nio;

/**
 * @Author： song.zh
 * @Date: 2019/1/3
 */
public class NIOConfig {

    /**
     * 服务端启动的端口号，当然也是客户端连接的端口号
     */
    public static final Integer PORT = 5000;

    /**
     * 处理线程休眠的时间，即多长时间处理一次就绪事件
     */
    public static final Integer SLEEP_MILLIS = 1500;
}
