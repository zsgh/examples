package mq.activemq;


/**
 * @Author： song.zh
 * @Date: 2018/12/28
 */
public class ActiveMqConfig {

    /**
     * ActiveMQ 连接配置，简单起见，不使用配置文件了
     */
    static final String USERNAME = "user";
    static final String PASSWORD = "user";
    static final String BROKEN_URL = "tcp://10.106.201.27:61616";

    static final String TOPIC_NAME = "TEST.TOPIC";
    static final String QUEUE_NAME = "TEST.QUEUE";
}
