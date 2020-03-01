package example.com.gauss.DTO;

import android.widget.EditText;

public class User {
    /**
     * 这个类只是为了传输数据,真正的Object在后端
     */
    private String username;
    private String passwordsalt;
    private String phone;
    private String email;
    private String sign;
    private int rights = 1; // 默认值1是普通用户

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"username\":");
        sb.append("\"");
        sb.append(this.username);
        sb.append("\",");
        sb.append("\"passwordsalt\":");
        sb.append("\"");
        sb.append(this.passwordsalt);
        sb.append("\",");
        sb.append("\"phone\":");
        sb.append("\"");
        sb.append(this.phone);
        sb.append("\",");
        sb.append("\"email\":");
        sb.append("\"");
        sb.append(this.email);
        sb.append("\",");
        sb.append("\"sign\":");
        sb.append("\"");
        sb.append(this.sign);
        sb.append("\",");
        sb.append("\"rights\":");
        sb.append("\"");
        sb.append(this.rights);
        sb.append("\",");
        sb.append("}");
        return sb.toString();
    }

    public User() {

    }

    public User(String username, String passwordsalt, String phone, String email, String sign) {
        this.username = username;
        this.passwordsalt = passwordsalt;
        this.phone = phone;
        this.email = email;
        this.sign = sign;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordSalt(String password) {
        this.passwordsalt = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRights(int rights) {
        this.rights = rights;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRights() {
        return rights;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordSalt() {
        return passwordsalt;
    }

    public String getPhone() {
        return phone;
    }

    public String getSign() {
        return sign;
    }

    public String getUsername() {
        return username;
    }


}

