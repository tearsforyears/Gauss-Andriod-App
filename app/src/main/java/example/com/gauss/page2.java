package example.com.gauss;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.zxing.util.Constant;
import example.com.gauss.DTO.Record;
import example.com.gauss.DTO.RecordAdapter;
import example.com.gauss.utils.AES;
import example.com.gauss.utils.AndroidViewTools;
import example.com.gauss.utils.HTTPUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.util.ArrayList;
import java.util.List;

public class page2 extends AppCompatActivity {
    private Button use;
    private Button _return;
    private SharedPreferences pref = null;
    private SharedPreferences.Editor e = null;
    private List<Record> records = new ArrayList<>();
    private String[] itemname_list;
    private static final int RENDER_ITEMS = 1;
    private static final int ITEMNAME_LIST_READY = 2;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RENDER_ITEMS:
                    set_item_adapter();
                    break;
                case ITEMNAME_LIST_READY:
                    set_use();
                    set_return();
                default:
                    break;
            }
        }
    };

    private int select;

    private void set_use() {
        use.setOnClickListener((v) -> {
            showSingleChoiceDialog(itemname_list, 0);
        });
    }

    private void set_return() {
        _return.setOnClickListener((v) -> {
            showSingleChoiceDialog(itemname_list, 1);
        });
    }


    private void showSingleChoiceDialog(String[] items, int state) {
        select = -1;
        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(page2.this);
        // 设置标题
        if (state == 0) {
            singleChoiceDialog.setTitle("使用");
        } else if (state == 1) {
            singleChoiceDialog.setTitle("归还");
        }
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select = which;
            }
        });
        singleChoiceDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (select != -1) {
                    // state 用来确定是发送
                    new Thread(() -> {
                        try {
                            String query = "/api/addrecord?username=" + AES.decode(pref.getString("login_user", "")) +
                                    "&itemname=" + itemname_list[select] + "&state=" + state;
                            String code = HTTPUtils.getJsonStringByKey(settings.SOCKET + query, "code");
                            if ("1".equals(code)) {
                                Looper.prepare();
                                Toast.makeText(page2.this, "增加记录成功", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            } else {
                                Looper.prepare();
                                Toast.makeText(page2.this, "增加记录失败", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (Exception e) {
                            Looper.prepare();
                            Toast.makeText(page2.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }).start();
                }
            }
        });
        singleChoiceDialog.show();
    }

    private void set_item_adapter() {
        RecyclerView recView = findViewById(R.id.record_list);
        LinearLayoutManager lm = new LinearLayoutManager(page2.this);
        recView.setLayoutManager(lm);
        //上面两行实现LinearLayout效果
        RecordAdapter far = new RecordAdapter(records);
        recView.setAdapter(far);
    }

    private void set_rec_View_values() {
        new Thread(() -> {
            try {
                String str = HTTPUtils.getDecodeContent(settings.SOCKET + "/api/getrecords");
                JSONArray items = JSON.parseArray(str);
                for (int i = 0; i < items.size(); i++) {
                    Record rec = new Record();
                    rec.setItemname(JSON.parseObject(items.getString(i)).getString("itemname"));
                    rec.setUsername(JSON.parseObject(items.getString(i)).getString("username"));
                    rec.setLast_use_time(JSON.parseObject(items.getString(i)).getString("last_use_time"));
                    rec.setState(Integer.parseInt(JSON.parseObject(items.getString(i)).getString("state")));
                    records.add(rec);
                }
                Message msg = new Message();
                msg.what = RENDER_ITEMS;
                handler.sendMessage(msg);
            } catch (Exception e) {
                Looper.prepare();
                Toast.makeText(this, "网络请求失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidViewTools.setFont();
        setContentView(R.layout.page2);
        AndroidViewTools.hide(this);
        bottom.setInitColor(1);
        init_views();
        reset_title();

        set_itemname_list();
        set_rec_View_values();
    }

    private void set_itemname_list() {
        new Thread(() -> {
            try {
                String str = HTTPUtils.getDecodeContent(settings.SOCKET + "/api/getitems");
                JSONArray items = JSON.parseArray(str);
                itemname_list = new String[items.size()];
                for (int i = 0; i < items.size(); i++) {
                    itemname_list[i] = JSON.parseObject(items.getString(i)).getString("itemname");
                }

                Message msg = new Message();
                msg.what = ITEMNAME_LIST_READY;
                handler.sendMessage(msg);
            } catch (Exception e) {
                Looper.prepare();
                Toast.makeText(this, "网络请求失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }

    private void reset_title() {
        use.setVisibility(View.VISIBLE);
        _return.setVisibility(View.VISIBLE);
    }

    private void init_views() {
        _return = findViewById(R.id.title_return);
        use = findViewById(R.id.title_use);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        e = pref.edit();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
            Toast.makeText(this, "" + scanResult, Toast.LENGTH_SHORT).show();
        }
    }
}
