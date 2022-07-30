package socket;

import java.io.*;
import java.net.Socket;

/**
 * 聊天客户端的实现
 */
public class Client {
    /**
     * 输出流
     */
    private BufferedWriter bufferedWriter;
    /**
     * 输入流
     */
    private BufferedReader bufferedReader;

    /**
     * 客户端的初始化方法
     * @param port
     * @throws IOException
     */
    public Client(int port) throws IOException {
        Socket socket = new Socket("127.0.0.1", port);
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    /**
     * 用户登陆
     * @param userName
     * @param pass
     * @return
     * @throws IOException
     */
    public LoginResponse login(String userName, String pass) throws IOException  {
        String msg = String.format("login,%s,%s\n", userName, pass);
        bufferedWriter.write(msg);
        bufferedWriter.flush();

        String loginResult = "";
        while (loginResult.length() == 0) {
            loginResult = bufferedReader.readLine();
            if (!loginResult.startsWith("ping")) {
                String[] split = loginResult.split(",");
                return new LoginResponse(Boolean.parseBoolean(split[0]),split[1]);
            }else{
                loginResult = "";
            }
        }
        return null;
    }

    /**
     * 群发消息
     */
    public void sendMsgAll(String msg) throws IOException  {
        String sendMsgAllCmd = String.format("sendMsgAll,%s\n", msg);
        bufferedWriter.write(sendMsgAllCmd);
        bufferedWriter.flush();
    }

    /**
     * 私聊消息
     */
    public void sendCommandMsgAll(String msg) throws IOException  {
        String sendMsgAllCmd = String.format("%s\n", msg);
        bufferedWriter.write(sendMsgAllCmd);
        bufferedWriter.flush();
    }

    /**
     * 监听服务端消息
     */
    public void receiveFromServerMsg(){
        //开启一个线程进行监听
        new Thread(new Runnable() {
            @Override
            /*
            不断的循环服务端来的数据
             */
            public void run() {
                while (true) {
                    try {
                        String msg = bufferedReader.readLine();
                        if (msg!=null && msg.startsWith("ping")) {
                            continue;
                        }
                        if (msg != null) {
                            System.out.println(msg);
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}
