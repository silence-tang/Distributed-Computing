import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.awt.*;
import java.util.*;

public class Publisher {
	// Ԥ�������ڱ���
	private static String brokerURL = "tcp://localhost:61616";
    private static ConnectionFactory factory;
    private Connection connection;
    private Session session;
    private Topic topic;
    private MessageProducer producer;
    private Random random = new Random();
    
    // ���캯������ʼ����Ա����
    public Publisher(String topicName) throws JMSException {
    	factory = new ActiveMQConnectionFactory(brokerURL);
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        topic = session.createTopic(topicName);  // �½�����
        producer = session.createProducer(topic);// �����߰�һ������
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
    	// ����ֵ�������Сֵ�����ֵ��װΪ�ַ�����Ϣ
    	String msg = String.valueOf(num)+" "+String.valueOf(N)+" "+String.format("%.4f",value)+" "+String.format("%.4f",mean)+" "+String.format("%.4f",var)+" "+String.format("%.4f",min)+" "+String.format("%.4f",max);
        Message message = session.createTextMessage(msg);
        producer.send(message);
//        System.out.println("Analysis of current numbers has been sent!");
    }
    
    public static void main(String[] args) throws JMSException, AWTException{
    	// ʵ���������������
    	Publisher publisher1 = new Publisher("RandGaussian");
        // ��ȡ�û�����ĸ�˹�ֲ���ֵ�ͷ���
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please input mu and sigma:");
        double mu = scanner.nextInt();
        double sigma = scanner.nextInt();
        Robot robot = new Robot();
        // ���ϵ���sendMessage���������ɵ�������������Ϣ��������Ϣ����
        for(int i = 0; i < 1000000; i++){
        	publisher1.sendNum(mu, sigma);
            robot.delay(100);
        }
        scanner.close();
        publisher1.close();
    }
}