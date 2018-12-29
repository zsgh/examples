package mq.activemq;

import javax.jms.*;


/**
 * @Author： song.zh
 * @Date: 2018/12/28
 * 消费者
 */
public class Consumer {

    private Session session = ActiveMqSessionFactory.getSession();


    /**
     * 处理topic消息
     *
     * 只能等待新的消息到来，以前的消息就丢失了
     */
    public void handleTopicMessage() {
        try {
            Topic topic = session.createTopic(ActiveMqConfig.TOPIC_NAME);
            handleMessage(topic);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    /**
     * 处理queue消息
     *
     * 可以处理积压在queue中的消息，和新来的消息
     */
    public void handleQueueMessage() {
        try {
            Queue queue = session.createQueue(ActiveMqConfig.QUEUE_NAME);
            handleMessage(queue);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息处理
     * @param destination
     */
    private void handleMessage(Destination destination) {
        try {
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener((Message message)-> System.out.println(message.toString()));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Consumer consumer = new Consumer();
        //消费topic消息
        consumer.handleTopicMessage();
        //消费queue消息
        consumer.handleQueueMessage();
    }
}
