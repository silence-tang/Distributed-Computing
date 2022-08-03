package threadpoolexp;
import java.io.*;
import java.net.*;

public class EchoClient {
    public static void main(String[] args) throws Exception {
        String userInput = null;
        String echoMsg = null;
        // 用户从终端输入的数据流（支持以行为单位读入）
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        // 新建socket向服务器端发送连接请求
        Socket socket = new Socket("127.0.0.1", 8189);
        System.out.println("Connected to server.");
        // 定义input/output字节流
        InputStream inStream = socket.getInputStream();
        OutputStream outStream = socket.getOutputStream();
        // 将读入的字节流向上先封装为字符流，再封装为缓冲流，支持以行为单位接收数据
        BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
        PrintWriter out = new PrintWriter(outStream);

        // 从终端读取数据并向服务器发送通信信息
        while((userInput = stdIn.readLine()) != null) {
            // 写入缓冲区
            out.println(userInput);
            // 清空管道，送出信息
            out.flush();
            // 下面处理来自服务器的响应信息
            echoMsg = in.readLine();    // 读入来自服务器的响应
            // 打印至终端
            System.out.println(echoMsg);
        }
        // 上述处理完毕，关闭socket
        socket.close();
    }
}
