package example.com.gauss;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import example.com.gauss.utils.AES;
import example.com.gauss.utils.AndroidViewTools;
import example.com.gauss.utils.HTTPUtils;
import example.com.gauss.utils.MD5;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class motify_password_page extends AppCompatActivity {
    private EditText e1, e2, e3;
    private Button commit;
    private SharedPreferences pref = null;
    private SharedPreferences.Editor e = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidViewTools.setFont();
        setContentView(R.layout.activity_motify_password_page);
        AndroidViewTools.hide(this);
        reset_title();
        init_views();
        set_commit();
    }

    private void init_views() {
        e1 = findViewById(R.id.motify_password1);
        e2 = findViewById(R.id.motify_password2);
        e3 = findViewById(R.id.motify_password3);
        commit = findViewById(R.id.motify_commit);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        e = pref.edit();
    }

    private void reset_title() {
        Button btn = findViewById(R.id.title_back);
        btn.setText("返回");
        btn.setOnClickListener((v) -> {
            this.finish();
        });
    }

    private void commit(String e1_text, String e2_text) {
        new Thread(() -> {
            try {
                String query = "/api/login?username=" + AES.decode(pref.getString("login_user", "")) + "&passwordsalt=" + MD5.GetMD5Salt(e1_text);
                String code = HTTPUtils.getJsonStringByKey(settings.SOCKET + query, "code");
                if ("1".equals(code)) {
                    //修改密码
                    query = "/api/update?username=" + AES.decode(pref.getString("login_user", "")) + "&oldpasswordsalt=" + MD5.GetMD5Salt(e1_text) + "&newpasswordsalt=" + MD5.GetMD5Salt(e2_text);
                    code = HTTPUtils.getJsonStringByKey(settings.SOCKET + query, "code");
                    if ("1".equals(code)) {
                        Looper.prepare();
                        Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                        this.finish();
                        Looper.loop();
                    } else {
                        Looper.prepare();
                        Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
                        this.finish();
                        Looper.loop();
                    }
                } else {
                    Looper.prepare();
                    Toast.makeText(this, "原始密码输入错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (Exception e) {
                Looper.prepare();
                Toast.makeText(this, "网络请求失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }

    private void set_commit() {
        commit.setOnClickListener((v) -> {
            String e3_text = e3.getText().toString().trim();
            String e2_text = e2.getText().toString().trim();
            String e1_text = e1.getText().toString().trim();
            if (!"".equals(e3_text) && (!"".equals(e2_text))) {
                if (e3_text.equals(e2_text)) {
                    commit(e1_text, e2_text);
                } else {
                    Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
