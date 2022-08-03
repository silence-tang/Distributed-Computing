import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.ArrayList;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.LinePlot;


class MyListener2 implements MessageListener {
	// ���ڱ���
	private int count = 0;
    ArrayList<Double> list_value = new ArrayList<Double>();
    ArrayList<Double> list_avg = new ArrayList<Double>();
    ArrayList<Double> list_var = new ArrayList<Double>();
    ArrayList<Double> list_min = new ArrayList<Double>();
    ArrayList<Double> list_max = new ArrayList<Double>();
    ArrayList<String> list_name1 = new ArrayList<String>();
    ArrayList<String> list_name2 = new ArrayList<String>();
    ArrayList<String> list_name3 = new ArrayList<String>();
    ArrayList<String> list_name4 = new ArrayList<String>();
    ArrayList<String> list_name5 = new ArrayList<String>();
    ArrayList<Integer> axis = new ArrayList<Integer>();   
    public void onMessage(Message message) {
        TextMessage textmessage = (TextMessage)message;
        try {
            String msg = String.valueOf(textmessage.getText()); // ��ȡ�źŷ������
            String[] analysis = msg.split(" ");
            count++; 							   				// ���յ��ķ������+1
            // ��ʼ�����б�Ϊ��
            ArrayList<Integer> list_num_total = new ArrayList<Integer>();
            ArrayList<Double> list_value_total = new ArrayList<Double>();
            ArrayList<String> list_name_total = new ArrayList<String>();
            int num = Integer.valueOf(analysis[0]);				// �������ȡ����������
            int N = Integer.valueOf(analysis[1]);
            double value = Double.valueOf(analysis[2]);
            double avg = Double.valueOf(analysis[3]);
            double var = Double.valueOf(analysis[4]);
            double min = Double.valueOf(analysis[5]);
            double max = Double.valueOf(analysis[6]);
            // ʵʱ��ʾ�źŷ������
            System.out.println("Analysis "+count+" total nums��"+num+", for the past "+N+" nums, mean: "+String.format("%.4f",avg)+", variance: "+String.format("%.4f",var)+", for all nums, min: "+String.format("%.4f",min)+", max:"+String.format("%.4f",max));
            // ���ƹ�ȥһ��ʱ���ڵ�����ź�����ͼ�����湹�����ݱ����
            axis.add(num);
            list_value.add(value);
            list_avg.add(avg);
            list_var.add(var);
            list_min.add(min);
            list_max.add(max);     
            list_num_total.addAll(axis);
            list_num_total.addAll(axis);
            list_num_total.addAll(axis);
            list_num_total.addAll(axis);
            list_num_total.addAll(axis);
            list_value_total.addAll(list_value);
            list_value_total.addAll(list_avg);
            list_value_total.addAll(list_var);
            list_value_total.addAll(list_min);
            list_value_total.addAll(list_max);
            list_name1.add("Gaussian Signal Value");
            list_name2.add("Avg of Last "+N+" Nums");
            list_name3.add("Var of Last "+N+" Nums");
            list_name4.add("Global Min");
            list_name5.add("Global Max");
            list_name_total.addAll(list_name1);
            list_name_total.addAll(list_name2);
            list_name_total.addAll(list_name3);
            list_name_total.addAll(list_name4);
            list_name_total.addAll(list_name5);
            // �������ݱ�
            Table tab = Table.create("Gaussian Signal").addColumns(
            		DoubleColumn.create("Num", list_num_total),
            		DoubleColumn.create("Value", list_value_total),
            		StringColumn.create("ValueType", list_name_total));
            // ��������ͼ,ValueType�Ƿ�������
            Plot.show(LinePlot.create("Random Signal Line Chart", tab, "Num", "Value", "ValueType"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class Visualizer {
    public static void main(String[] args) throws JMSException {
    	String brokerURL = "tcp://localhost:61616";
		ConnectionFactory factory = null;
		Connection connection = null;
		Session session = null;
		Topic topic = null;
		MessageConsumer messageConsumer = null;
        MyListener2 listener2 = null;
        try {
            factory = new ActiveMQConnectionFactory(brokerURL);
            connection = factory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // �����źŷ������
            topic = session.createTopic("AnalysisRes");
            messageConsumer = session.createConsumer(topic);
            listener2 = new MyListener2();
            messageConsumer.setMessageListener(listener2);
            connection.start();
            System.out.println("Press any key to exit the visualizer..");
            System.in.read();   // Pause
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	connection.close();
        }
    }
}
