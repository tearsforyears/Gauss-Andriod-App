package example.com.gauss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.google.zxing.activity.CaptureActivity;
import com.google.zxing.util.Constant;
import example.com.gauss.utils.AndroidViewTools;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class title extends LinearLayout {

    private void setBack(Context ctx) {
        ((Button) findViewById(R.id.title_back)).setOnClickListener((v) -> {
            SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
            e.putString("login_user", "");
            e.apply();
            ctx.startActivity(new Intent(ctx, login_page.class));
            ((Activity) ctx).finish();
            ((Activity) getContext()).finish();
        });
    }

    private void setList(Context ctx) {
        LinearLayout find_list = findViewById(R.id.title_find_list);

        Button find = findViewById(R.id.title_find);
        find.setOnClickListener((v) -> {
            find_list.setPadding(0, AndroidViewTools.getHeight(find) + 20, 0, 0);
            if (GONE == find_list.getVisibility()) {
                find_list.setVisibility(VISIBLE);
            } else if (VISIBLE == find_list.getVisibility()) {
                find_list.setVisibility(GONE);
            }
        });
    }

    private void setScan(Context ctx) {
        Button scan = findViewById(R.id.title_scan);
        scan.setOnClickListener((v) -> {
            Intent intent = new Intent(ctx, CaptureActivity.class);
            ((Activity) ctx).startActivityForResult(intent, Constant.REQ_QR_CODE);
        });
    }

    public title(Context ctx, AttributeSet abs) {
        super(ctx, abs);
        LayoutInflater.from(ctx).inflate(R.layout.title, this);
        //需要动态加载LayoutInflate可以完成加载
        //inflate函数 接受布局文件的id 第二个参数是对象本身
        //然后在加载类的时候注册函数

        setBack(ctx);
        setList(ctx);
        setScan(ctx);

    }
}
