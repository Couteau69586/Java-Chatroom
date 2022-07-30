package config;

import java.util.HashMap;
import java.util.Map;

/**
 * 程序配置参数
 */
public class Config {
    /**
     * 监听的端口号
     */
    public static final int port = 15000;


    /**
     * 所有的登陆用户
     */
    public static Map<String, String> USERS = new HashMap<>();

    static {
        USERS.put("Couteau", "Lhy69586");
        USERS.put("Hjcowo", "123456");
        USERS.put("user3", "pass3");
        USERS.put("user4", "pass4");
    }
}
