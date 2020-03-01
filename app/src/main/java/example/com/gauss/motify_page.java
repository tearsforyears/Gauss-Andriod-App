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

public class motify_page extends AppCompatActivity {
    private Button commit;
    private SharedPreferences pref = null;
    private SharedPreferences.Editor e = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidViewTools.setFont();
        setContentView(R.layout.activity_motify_page);
        AndroidViewTools.hide(this);
        reset_title();
        init_views();
        commit();
    }

    private void commit() {
        commit.setOnClickListener((v) -> {
            new Thread(() -> {
                try {
                    String email = ((EditText) findViewById(R.id.mtf_value1)).getText().toString().trim();
                    String sign = ((EditText) findViewById(R.id.mtf_value2)).getText().toString().trim();
                    String query = "/api/update?username=" + AES.decode(pref.getString("login_user", ""))
                            + "&email=" + email + "&sign=" + sign;
                    String code = HTTPUtils.getJsonStringByKey(settings.SOCKET + query, "code");
                    if ("1".equals(code)) {
                        Looper.prepare();
                        Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
                        this.finish();
                        Looper.loop();
                    } else {
                        Looper.prepare();
                        Toast.makeText(this, "提交失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (Exception e) {
                    Looper.prepare();
                    Toast.makeText(this, "网络请求失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }).start();
        });

    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void init_views() {
        commit = findViewById(R.id.mtf_commit);
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
}
