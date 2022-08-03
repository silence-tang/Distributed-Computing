import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

// ��Ϣ������
class MyListener1 implements MessageListener {
	// ���ڱ���
    private int N = 0;
    private int num = 0;
    private Double min = 0.0;
    private double max = 0.0;
    private ArrayList<Double> list = new ArrayList<Double>();
    // ���캯��������ʵ����ʱ�������N
    MyListener1(int N){
        this.N = N;
    }
    
    // ��Ϣ���ռ�������
    public void onMessage(Message message) {
        TextMessage textmessage = (TextMessage)message;
        try {
            double value = Double.valueOf(textmessage.getText());
            list.add(value);    // ����Ϣ���л�ȡһ��������������
            num++;  			// �����е���������+1
            double mean = 0;
            double var = 0;
            if(num > N){  		// ֻ�е������е����ָ�������Nʱ���ܽ��к�������
                for (int i = list.size() - N; i < list.size(); i++){
                    double tempNum = list.get(i);
                    mean += tempNum;
                }
                mean /= N;      // ���ֵ
                for (int i = list.size()-N; i < list.size(); i++ ){
                    double tempNum = list.get(i);
                    var += Math.pow((tempNum - mean), 2);
                }
                var /= N;      // �󷽲�
                min = Collections.min(list);     // ����ʷ������Сֵ
                max = Collections.max(list);     // ����ʷ�������ֵ
//                System.out.println("Total nums��"+num+", for the past "+N+" num, mean: "+String.format("%.4f",mean)+", variance: "+String.format("%.4f",var)+", for all nums, mean: "+String.format("%.4f",min)+", max:"+String.format("%.4f",max));
                // ʵ�����������publisher
                Publisher publisher2 = new Publisher("AnalysisRes");
                publisher2.sendAnalysis(num, N, value, mean, var, min, max);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class Analyzer {
    public static void main(String[] args) throws JMSException {
    	String brokerURL = "tcp://localhost:61616";
		ConnectionFactory factory = null;
		Connection connection = null;
		Session session = null;
		Topic topic = null;
		MessageConsumer messageConsumer = null;
        MyListener1 listener1 = null;
        try {
            factory = new ActiveMQConnectionFactory(brokerURL);
            connection = factory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic("RandGaussian");
            messageConsumer = session.createConsumer(topic);
            System.out.println("Please input N:");
            @SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
            int N = scanner.nextInt();
            listener1 = new MyListener1(N);
            messageConsumer.setMessageListener(listener1);
            connection.start();
            System.in.read();   // Pause
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	connection.close();
        }
    }
}