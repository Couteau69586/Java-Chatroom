package socket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;

public class Util {
    /**
     * 判断socket是否关闭
     *
     * @param socket
     * @return
     */
    public static boolean isClose(Socket socket) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write("ping" + new Date().getTime()+"\n");
            bufferedWriter.flush();
            return false;
        } catch (IOException e) {
//            e.printStackTrace();
            return true;
        }
    }
}
