import config.Config;
import socket.Server;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ServerApp {
    /**
     * 服务端的主程序，启动一个服务端
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        Server.startServer(Config.port);
        TimeUnit.HOURS.sleep(5);
    }
}
