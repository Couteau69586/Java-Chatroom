package socket;

import account.UserService;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 服务端每一个客户端都对应一个客户端的socket
 */
public class SocketWrapper {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;

    public SocketWrapper(Socket socket) throws IOException {
        this.socket = socket;
        /**
         * 获取输入输出字节流
         */
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        /**
         * 启动一个线程，用于在服务端检测用户是否已下线
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (Util.isClose(socket)) {
                        if (userName != null) {
                            Server.loginUserMap.remove(userName);
                            String info = "【用户下线】" + userName;
                            System.out.println(info);
                            break;
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }


    /**
     * 针对每个客户端的连接来创建一个线程用以处理请求
     */
    public void startMsgListener() {
        new Thread(() -> {
            try {
                while (true) {
                    String msg = readString();
                    if (msg == null || msg.trim().length() == 0) {
                        continue;
                    }
                    //登录
                    if (msg.startsWith("login")) {
                        Map<String, String> allUsers = UserService.getAllUsers();
                        String[] split = msg.split(",");
                        String requestUserName = split[1];
                        String requestPwd = split[2];
                        if (!allUsers.containsKey(requestUserName)) {
                            bufferedWriter.write("false,用户不存在\n");
                            bufferedWriter.flush();
                            continue;
                        }
                        if (!requestPwd.equals(allUsers.get(requestUserName))) {
                            bufferedWriter.write("false,密码错误\n");
                            bufferedWriter.flush();
                            continue;
                        }
                        bufferedWriter.write("true,登陆成功\n");
                        bufferedWriter.flush();
                        this.userName = requestUserName;
                        String info = "【用户上线】" + userName;
                        System.out.println(info);
                        Server.loginUserMap.put(requestUserName, this);
                        continue;
                        //如果来自客户端的群发消息命令
                    } else if (msg.startsWith("sendMsgAll")) {
                        String realMsg = msg.replace("sendMsgAll,", "");
                        Server.loginUserMap.entrySet().forEach(entry -> {
                            SocketWrapper socket = entry.getValue();
                            try {
                                socket.writeString(String.format("[%s]:%s\n", this.userName, realMsg));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        //私聊消息
                    } else if (msg.startsWith("@+")) {
                        String realMsg = msg.replace("@+", "");
                        String[] split = realMsg.split(",");
                        String toUserName = split[0];
                        String sendRealMsg = realMsg.substring(realMsg.indexOf(",") + 1);
                        Server.loginUserMap.entrySet().forEach(entry -> {
                            String userName = entry.getKey();
                            if (toUserName.equals(userName)) {
                                SocketWrapper socket = entry.getValue();
                                try {
                                    socket.writeString(String.format("[%s向你发送一条私聊消息]:%s\n", this.userName, sendRealMsg));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                        });
                    }

                    if (Util.isClose(socket)) {
                        if (userName != null) {
                            Server.loginUserMap.remove(userName);
                            String info = "【用户下线】" + userName;
                            System.out.println(info);
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                if (userName != null) {
                    Server.loginUserMap.remove(userName);
                    String info = "【用户下线】" + userName;
                    System.out.println(info);
                }

            }
        }).start();
    }

    /**
     * 从客户端读字符串
     *
     * @return
     * @throws IOException
     */
    public String readString() throws IOException {
        return bufferedReader.readLine();
    }

    /**
     * 往客户端写一个字符串
     *
     * @param msg
     * @throws IOException
     */
    public void writeString(String msg) throws IOException {
        bufferedWriter.write(msg);
        bufferedWriter.flush();
    }

}
