package mq.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

/**
 * @Author： song.zh
 * @Date: 2018/12/28
 */
public class ActiveMqSessionFactory {

    public static Session getSession() {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(
                    ActiveMqConfig.USERNAME,ActiveMqConfig.PASSWORD,ActiveMqConfig.BROKEN_URL);
            //从工厂中创建一个链接
            Connection connection  = factory.createConnection();
            //开启链接
            connection.start();
            //创建一个事务（这里通过参数可以设置事务的级别）
            Session session = connection.createSession(true,Session.SESSION_TRANSACTED);

            return session;
        } catch (JMSException e) {
            e.printStackTrace();
            return null;
        }
    }

}
