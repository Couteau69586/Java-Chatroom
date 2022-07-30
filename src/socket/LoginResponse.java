package socket;

/**
 * 登陆结果的返回
 */
public class LoginResponse {
    public boolean loginResult;
    public String message;

    public LoginResponse(boolean loginResult, String message) {
        this.loginResult = loginResult;
        this.message = message;
    }
}
