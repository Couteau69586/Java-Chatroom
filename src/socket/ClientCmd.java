package socket;

import config.Config;
import socket.LoginResponse;
import socket.Client;

import java.io.IOException;
import java.util.Scanner;

/**
 * 客户端主程序
 */
public class ClientCmd {

    /**
     * 真正的客户端实现逻辑
     * @throws IOException
     */
    public static void clientCmd() throws IOException {
        /**
         * 创建一个客户端
         */
        Client client = new Client(Config.port);
        Scanner scanner = new Scanner(System.in);

        String userName ;
        System.out.println("客户端与服务端连接成功，请输入用户名密码进行登陆");
        while (true) {

            System.out.println("请输入用户名:");
            System.out.print(">");
            userName = scanner.nextLine();

            System.out.print(">");
            System.out.println("请输入密码:");
            String pass = scanner.nextLine();

            LoginResponse login = client.login(userName, pass);
            if (login.loginResult) {
                break;
            }else{
                System.out.println("登陆失败，原因="+login.message);
            }
        }
        System.out.println("恭喜你，"+userName+"登陆成功");
        System.out.println("请输入命令");
        System.out.println("1.群聊:普通字符串");
        System.out.println("2.私聊:@+用户,消息");


        //开启一个线程监听服务端消息
        client.receiveFromServerMsg();
        while (true) {
            System.out.print(">");
            String cmd = scanner.nextLine();
            //退出
            if (cmd.startsWith("quit")) {
                System.out.println("退出");
                System.exit(0);
                //私聊
            }else if (cmd.startsWith("@+")) {
                client.sendCommandMsgAll(cmd);
                //群聊
            }else{
                client.sendMsgAll(cmd);
            }
        }
    }
}
