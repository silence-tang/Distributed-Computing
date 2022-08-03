// MyRMIServer
package Server;
import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import InterFace.*;

// ��������
public class RMIServer {
	// RMI������IP��ַ
    public static final String RMI_HOST = "127.0.0.1";
    // RMI����˿�
    public static final int RMI_PORT = 9527;
    // RMI��������
    public static final String RMI_NAME = "rmi://" + RMI_HOST + ":" + RMI_PORT + "/BookInfoManageSystem";
	// main������ע��ʵ���ൽע������
    public static void main(String[] args) throws Exception {
        try {
            LocateRegistry.createRegistry(RMI_PORT);  // ����ע�����ļ������Զ˿�9527������
            Naming.rebind(RMI_NAME, new BookSystemImpl());  // ��rmi����������Զ�̶���
            System.out.println("RMI���������ɹ��������ַΪ" + RMI_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
