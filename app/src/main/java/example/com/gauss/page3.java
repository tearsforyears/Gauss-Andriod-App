package example.com.gauss;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.zxing.util.Constant;
import example.com.gauss.DTO.Item;
import example.com.gauss.DTO.ItemAdapter;
import example.com.gauss.utils.AndroidViewTools;
import example.com.gauss.utils.HTTPUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class page3 extends AppCompatActivity {
    List<Item> items_ = new ArrayList<>();
    private static final int RENDER_ITEMS = 1;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RENDER_ITEMS:
                    set_item_adapter();
                    break;
                default:
                    break;
            }
        }
    };

    private void set_item_adapter() {
        RecyclerView recView = findViewById(R.id.item_list);
        LinearLayoutManager lm = new LinearLayoutManager(page3.this);
        recView.setLayoutManager(lm);
        //上面两行实现LinearLayout效果
        ItemAdapter far = new ItemAdapter(items_);
        recView.setAdapter(far);
    }

    private void set_rec_View_values() {
        new Thread(() -> {
            try {
                String str = HTTPUtils.getDecodeContent(settings.SOCKET + "/api/getitems");
                JSONArray items = JSON.parseArray(str);
                for (int i = 0; i < items.size(); i++) {
                    Item item = new Item();
                    item.setItemname(JSON.parseObject(items.getString(i)).getString("itemname"));
                    item.setUsername(JSON.parseObject(items.getString(i)).getString("username"));
                    item.setAmount(Integer.parseInt(JSON.parseObject(items.getString(i)).getString("amount")));
                    item.setDescription(JSON.parseObject(items.getString(i)).getString("description"));
                    items_.add(item);
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
        setContentView(R.layout.page3);
        AndroidViewTools.hide(this);
        bottom.setInitColor(2);
        init_views();
        set_rec_View_values();
    }

    private void init_views() {

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
            Toast.makeText(this, scanResult + " ", Toast.LENGTH_SHORT).show();
        }
    }
}
