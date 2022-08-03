// MyRMIServer
package Server;
import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import InterFace.*;

// 服务器类
public class RMIServer {
	// RMI服务器IP地址
    public static final String RMI_HOST = "127.0.0.1";
    // RMI服务端口
    public static final int RMI_PORT = 9527;
    // RMI服务名称
    public static final String RMI_NAME = "rmi://" + RMI_HOST + ":" + RMI_PORT + "/BookInfoManageSystem";
	// main方法：注册实现类到注册中心
    public static void main(String[] args) throws Exception {
        try {
            LocateRegistry.createRegistry(RMI_PORT);  // 创建注册中心监听来自端口9527的请求
            Naming.rebind(RMI_NAME, new BookSystemImpl());  // 将rmi服务名绑定至远程对象
            System.out.println("RMI服务启动成功，服务地址为" + RMI_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
