package threadpoolexp;
import java.io.*;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {
    public static void main(String [] args) throws Exception {
        // 自定义线程池参数
        int corePoolSize = 3;
        int maximumPoolSize = 15;
        long keepAliveTime = 200;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        // 创建自定义线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, new ArrayBlockingQueue<Runnable>(5));
        // 初始化与客户端之间的socket管道为空
        Socket clientSocket = null;
        // 向OS注册服务，创建监听socket对象，监听8189端口的握手请求
        ServerSocket listenSocket = new ServerSocket(8189);
        // 在终端打印提示信息
        System.out.println("Server listening at port 8189.");
        int count = 0;
        // 这里for只起到循环处理新通信socket的作用，循环次数只要大于maximumPoolSize即可，不影响结果
        // 用while(1)会导致'listenSocket' is never closed，但不影响编译运行
        // 要解决报错可以改成for
        while(true) {
            // 取一个连接记录，关于此记录创建socket
            clientSocket = listenSocket.accept();
            // 连接数+1
            count ++;
            // 在终端打印当前总连接数
            System.out.println("The total number of clients is " + count + '.');
            // 实例化服务线程serverThread服务当前的通信socket对象，将clientSocket作为状态参数传入该线程
            ServerThread serverThread = new ServerThread(clientSocket);
            // 由线程池（以某种方式）调度一个线程对当前通信socket对象进行服务
            executor.execute(serverThread);
        }
        // 关闭监听socket对象
        // listenSocket.close();
        // 关闭线程池executor对象
        // executor.shutdown();
    }
}


// 定义服务线程类，继承父类 java.lang.Thread
class ServerThread extends Thread {
    Socket clientSocket = null;

    // 定义属性
    ServerThread(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    // 线程主函数
    public void run() {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        OutputStream os = null;
        PrintWriter pw = null;
        try {
            // 定义输出输出流
            is = clientSocket.getInputStream();
            os = clientSocket.getOutputStream();
            br = new BufferedReader(new InputStreamReader(is));  // 向上封装数据流
            pw = new PrintWriter(os);  // Convert characters into bytes
            String info = null;
            // 读输入流，以行为单位
            while ((info = br.readLine())!= null) {
                System.out.println("Message from client: " + info);
                // 若接收到quit，则关闭连接，退出程序
                if(info.equals("quit")) {
                    clientSocket.close();
                    System.exit(0);
                }
                pw.println(info); // 写入缓冲区
                pw.flush();  // 冲刷数据
            }
        }
        catch (IOException e) {
            e.printStackTrace(); // 处理异常
        }
        finally {
            try {
                if(pw != null)
                    pw.close();  // 关闭各种连接通道和输入输出流
                if(os != null)
                    os.close();
                if(br != null)
                    br.close();
                if(isr != null)
                    isr.close();
                if(clientSocket != null)
                clientSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();   // 处理异常
            }
        }
    }
}