package account;

import config.Config;

import java.util.Map;

/**
 * 账号服务
 */
public class UserService {
    /**
     * 获取所有用户
     */
    public static Map<String,String> getAllUsers(){
        return Config.USERS;
    }
}
