package example.com.gauss.utils;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import example.com.gauss.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class AndroidViewTools {
    public static void hide(AppCompatActivity a) {
        if (a.getSupportActionBar() != null)
            a.getSupportActionBar().hide();
    }

    public static void setFont() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/font1.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public static int getHeight(View v) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
        return v.getMeasuredHeight();

    }

    public static int getWidth(View v) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
        return v.getMeasuredWidth();
    }

    public static String mesure(View imageView) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        imageView.measure(w, h);
        int height = imageView.getMeasuredHeight();
        int width = imageView.getMeasuredWidth();
        return "{\"width\":\"" + width + "\",\"height\":\"" + height + "\"}";
    }
}
