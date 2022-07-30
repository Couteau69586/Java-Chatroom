package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * socket服务端
 */
public class Server {

    /**
     * 所有的客户端连接
     */
    public static List<SocketWrapper> clientSocket=new ArrayList<>();
    /**
     * 记录所有的客户端连接
     */
    public static Map<String, SocketWrapper> loginUserMap = new ConcurrentHashMap<>();

    /**
     * 用于检测客户端输入
     */
    public static Scanner scanner = new Scanner(System.in);
    public static void startServer(int port) throws IOException {

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("服务端启动成功，监听端口="+port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        //服务端接受连接
                        Socket socket = serverSocket.accept();

                        SocketWrapper socketWrapper = new SocketWrapper(socket);
                        clientSocket.add(socketWrapper);
                        //监听客户端消息
                        socketWrapper.startMsgListener();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        /**
         * 开启一个线程，用于接收服务端广播的请求
         */
        new Thread(() -> {
            while (true) {
                System.out.println("请输入服务端广播消息，会通知到所有在线客户端");
                System.out.print(">");
                String cmd = scanner.nextLine();
                /**
                 * 退出系统
                 */
                if (cmd.equals("quit")) {
                    System.out.println("服务器关闭");
                    System.exit(0);
                }else  {
                    /**
                     * 通知所有客户端，进行广播
                     */
                    loginUserMap.values().stream().forEach(socketWrapper -> {
                        try {
                            socketWrapper.writeString("[服务端广播]"+cmd+"\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }).start();
    }
}
