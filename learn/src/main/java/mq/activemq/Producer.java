package mq.activemq;

import javax.jms.*;
import java.time.Instant;

/**
 * @Author： song.zh
 * @Date: 2018/12/28
 * 生产者
 */
public class Producer {

    Session session = ActiveMqSessionFactory.getSession();

    /**
     * 发送消息
     * @param destination 目的地
     * @param msgContent 消息内容
     */
    public void send(Destination destination, String msgContent) {
        try {
            MessageProducer producer = session.createProducer(destination);
            while(true) {
                Thread.sleep(1000);
                TextMessage message = session.createTextMessage(msgContent +" "+ Instant.now());
                producer.send(message);
                session.commit();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送topic消息
     */
    public void sendToTopic() {
        try {
            Topic topic = session.createTopic(ActiveMqConfig.TOPIC_NAME);
            send(topic, "i'm topic message:");
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送queue消息
     */
    public void sendToQueue() {
        try {
            Queue queue = session.createQueue(ActiveMqConfig.QUEUE_NAME);
            send(queue, "i am queue msg:");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Producer producer = new Producer();
        producer.sendToTopic();
        producer.sendToQueue();
    }
}
