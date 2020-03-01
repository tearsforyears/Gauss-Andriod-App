package example.com.gauss;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.zxing.util.Constant;
import example.com.gauss.utils.AES;
import example.com.gauss.utils.AndroidViewTools;
import example.com.gauss.utils.HTTPUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class page1 extends AppCompatActivity {
    EditText item_name, description, amount;
    Button commit;
    private SharedPreferences pref = null;
    private SharedPreferences.Editor e = null;

    private void set_commit() {
        commit.setOnClickListener((v) -> {
            String item_name_ = item_name.getText().toString().trim();
            String description_ = description.getText().toString().trim();
            String amount_ = amount.getText().toString().trim();
            new Thread(() -> {
                try {
                    if (!"".equals(item_name_) && !"".equals(description_) && !"".equals(amount_)) {
                        String query = "/api/additem?username=" + AES.decode(pref.getString("login_user", "")) +
                                "&itemname=" + item_name_ + "&description=" + description_ + "&amount=" + amount_;
                        String code = HTTPUtils.getJsonStringByKey(settings.SOCKET + query, "code");
                        if ("1".equals(code)) {
                            Looper.prepare();
                            Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        } else {
                            Looper.prepare();
                            Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    } else {
                        Looper.prepare();
                        Toast.makeText(this, "提交信息不能为空", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (Exception e) {
                    Looper.prepare();
                    Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }).start();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidViewTools.setFont();
        setContentView(R.layout.page1);
        AndroidViewTools.hide(this);
        bottom.setInitColor(0);

        init_views();
        set_commit();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void init_views() {
        item_name = findViewById(R.id.add_item_item_name);
        description = findViewById(R.id.add_item_description);
        amount = findViewById(R.id.add_item_amount);
        commit = findViewById(R.id.add_item_commit);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        e = pref.edit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
            Toast.makeText(this, scanResult, Toast.LENGTH_SHORT).show();
        }
    }
}
