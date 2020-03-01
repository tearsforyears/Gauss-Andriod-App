package example.com.gauss;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import example.com.gauss.utils.AES;
import example.com.gauss.utils.AndroidViewTools;
import example.com.gauss.utils.HTTPUtils;
import example.com.gauss.utils.MD5;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class login_page extends AppCompatActivity {
    private CheckBox remember_ckb;
    private Button login;
    private Button registe;
    private EditText password;
    private EditText username;

    private SharedPreferences pref = null;
    private SharedPreferences.Editor e = null;


    private boolean user_database(String userName, String passWord) {
        String str = null;
        try {
            String query = "/api/login?username=" + userName + "&passwordsalt=" + MD5.GetMD5Salt(passWord);

            str = HTTPUtils.getJsonStringByKey(settings.SOCKET + query, "code");
            if (userName != null && passWord != null) {
                if ("1".equals(str)) { //后端接口:如果存在数据库且密码正确 返回code:1
                    e.putString("login_user", AES.encode(userName));
                    e.apply();
                    return true;
                }
            }
        } catch (Exception e) {
            Looper.prepare();
            Toast.makeText(login_page.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
        return false;
    }

    private void set_password() {
        if (pref.getBoolean("ischecked", false)) {
            try {
                remember_ckb.setChecked(true);
                username.setText(pref.getString("user", null));
                String str = AES.decode(pref.getString("pass", null));
                password.setText(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            e.clear();
        }
    }

    private void login() {
        login.setOnClickListener((v) -> {
            new Thread(() -> {
                String user_name = username.getText().toString().trim();
                String pass_word = password.getText().toString().trim();
                if (user_database(user_name, pass_word)) {
                    // set password remember
                    if (remember_ckb.isChecked()) {
                        try {
                            e.putBoolean("ischecked", true);
                            e.putString("user", user_name);
                            e.putString("pass", AES.encode(pass_word));
                            e.apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        e.putBoolean("ischecked", false);
                        e.putString("user", null);
                        e.putString("pass", null);
                        e.apply();
                    }
                    startActivity(new Intent(this, page1.class));
                    finish();
                } else {
                    Looper.prepare();
                    Toast.makeText(login_page.this, "用户名或者密码错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }).start();
        });
    }

    private void registe() {
        registe.setOnClickListener((v) -> {
            startActivity(new Intent(login_page.this, registe_page.class));
        });
    }

    private void is_login_or_change() {
        new Thread(() -> {
            if (!"".equals(pref.getString("login_user", ""))) {
                try {
                    if ("200".equals(HTTPUtils.getContent(settings.SOCKET + "/api/ping"))) {
                        startActivity(new Intent(this, page1.class));
                        this.finish();
                    } //测试服务器连通性
                } catch (Exception e) {
                    Looper.prepare();
                    Toast.makeText(this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全局字体
        AndroidViewTools.setFont();
        setContentView(R.layout.activity_login_page);
        init_views();
        is_login_or_change(); //实现登陆状态记忆 技术选用 session
        getPermission();
        set_password(); //设置密码存取
        login(); // 注册事件
        registe(); // 注册注册按钮事件

    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(login_page.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(login_page.this, new String[]{android.Manifest.permission.CAMERA}, 1);
        }
        if (ContextCompat.checkSelfPermission(login_page.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(login_page.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                } else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    Toast.makeText(login_page.this, "请手动打开相机权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里已经获取到了
                } else {
                    //获取存储权限
                    Toast.makeText(login_page.this, "请手动打开存储权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void init_views() {
        remember_ckb = (CheckBox) findViewById(R.id.remember_ckb);
        login = (Button) findViewById(R.id.login);
        registe = (Button) findViewById(R.id.registe);
        password = (EditText) findViewById(R.id.password);
        username = (EditText) findViewById(R.id.user_name);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        e = pref.edit();

        AndroidViewTools.hide(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
