import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.awt.*;
import java.util.*;

public class Publisher {
	// 预定义类内变量
	private static String brokerURL = "tcp://localhost:61616";
    private static ConnectionFactory factory;
    private Connection connection;
    private Session session;
    private Topic topic;
    private MessageProducer producer;
    private Random random = new Random();
    
    // 构造函数：初始化成员属性
    public Publisher(String topicName) throws JMSException {
    	factory = new ActiveMQConnectionFactory(brokerURL);
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        topic = session.createTopic(topicName);  // 新建主题
        producer = session.createProducer(topic);// 生产者绑定一个主题
    }
    
    public void close() throws JMSException {
        if (connection != null) {
            connection.close();
        }
    }
	
    public void sendNum(double mu ,double sigma) throws JMSException {
        double num = Math.sqrt(sigma) * random.nextGaussian() + mu;
        Message message = session.createTextMessage(String.valueOf(num));
        producer.send(message);
//        System.out.println("A number has been sent!");
    }
    
    public void sendAnalysis(int num, int N, double value, double mean, double var, double min, double max) throws JMSException {
    	// 将均值、方差、最小值、最大值封装为字符串消息
    	String msg = String.valueOf(num)+" "+String.valueOf(N)+" "+String.format("%.4f",value)+" "+String.format("%.4f",mean)+" "+String.format("%.4f",var)+" "+String.format("%.4f",min)+" "+String.format("%.4f",max);
        Message message = session.createTextMessage(msg);
        producer.send(message);
//        System.out.println("Analysis of current numbers has been sent!");
    }
    
    public static void main(String[] args) throws JMSException, AWTException{
    	// 实例化随机数生成器
    	Publisher publisher1 = new Publisher("RandGaussian");
        // 读取用户输入的高斯分布均值和方差
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please input mu and sigma:");
        double mu = scanner.nextInt();
        double sigma = scanner.nextInt();
        Robot robot = new Robot();
        // 不断调用sendMessage函数将生成的随机数打包成消息发送至消息队列
        for(int i = 0; i < 1000000; i++){
        	publisher1.sendNum(mu, sigma);
            robot.delay(100);
        }
        scanner.close();
        publisher1.close();
    }
}