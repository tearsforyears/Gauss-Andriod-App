package example.com.gauss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class bottom extends LinearLayout {
    static Button[] btns;

    private static void setOthersNormal(Button[] btns, int index) {
        for (int i = 0; i < btns.length; i++) {
            if (i != index) {
                btns[i].setTextColor(0xaaff7700);
                btns[i].setBackgroundColor(0x22ffffff);
            }
        }
    }

    public static void setInitColor(int index) {
        btns[index].setBackgroundColor(0xaaff7700);
        btns[index].setTextColor(0xffffffff);
        setOthersNormal(btns, index);
    }


    public bottom(Context ctx, AttributeSet abs) {
        super(ctx, abs);
        LayoutInflater.from(ctx).inflate(R.layout.bottom, this);
        //需要动态加载LayoutInflate可以完成加载
        //inflate函数 接受布局文件的id 第二个参数是对象本身

        //然后在加载类的时候注册函数
        btns = new Button[4];
        btns[0] = (Button) findViewById(R.id.page1);
        btns[1] = (Button) findViewById(R.id.page2);
        btns[2] = (Button) findViewById(R.id.page3);
        btns[3] = (Button) findViewById(R.id.page4);
        Class[] to_clss = new Class[]{page1.class, page2.class, page3.class, page4.class};

        for (int i = 0; i < btns.length; i++) {
            if (ctx.getClass() == to_clss[i]) {
                //do nothing
            } else {
                Intent intent = new Intent(ctx, to_clss[i]);
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

                btns[i].setOnClickListener((v) -> {
                    ctx.startActivity(intent);
                    ((Activity) ctx).finish();
                    ((Activity) ctx).overridePendingTransition(-1, -1);

                });
            }
        }

    }
}
