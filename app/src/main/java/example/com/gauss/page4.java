package example.com.gauss;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.util.Constant;
import example.com.gauss.DTO.User;
import example.com.gauss.utils.AES;
import example.com.gauss.utils.AndroidViewTools;
import example.com.gauss.utils.HTTPUtils;
import example.com.gauss.utils.IOUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class page4 extends AppCompatActivity {

    private SharedPreferences pref = null;
    private SharedPreferences.Editor e = null;
    private ImageView image;
    private TextView username;
    private TextView sign;
    private TextView rights;
    private Button motify;
    private Button password;

    private static final int GET_USER = 1;
    private static final int GET_IMAGE = 2;
    private static final int SET_IMAGE = 3;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_USER:
                    JSONObject jsonObj = JSON.parseObject((String) msg.obj);
                    set_image_src(jsonObj.getString("image_src"));
                    set_rights(jsonObj.getString("rights"));
                    username.setText(jsonObj.getString("username"));
                    sign.setText(jsonObj.getString("sign"));
                    break;
                case SET_IMAGE:
                    image.setImageBitmap((Bitmap) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };
    private static final String[] rights_name = new String[]{"游客", "普通用户", "管理员"};

    private void set_rights(String rights_value) {
        int right = Integer.parseInt(rights_value);
        rights.setText("身份:" + rights_name[right]);
    }

    private void set_image_src(String image_name) {
        //此处image为服务器内相对路径
        //请求下载然后显示出来(如果有的话)
        //image.setImageBitmap(null);
        if (image_name != null && !"".equals(image_name)) {
            new Thread(() -> {
                InputStream is = HTTPUtils.getInputStream(settings.SOCKET + "/api/getimage?image=" + image_name);
                Bitmap bm = BitmapFactory.decodeStream(is);
                Message msg = new Message();
                msg.what = SET_IMAGE;
                msg.obj = bm;
                handler.sendMessage(msg);
            }).start();
        }
    }

    private void set_password_page() {
        password.setOnClickListener((v) -> {
            startActivity(new Intent(this, motify_password_page.class));
        });
    }

    private void set_motify_page() {
        motify.setOnClickListener((v) -> {
            startActivity(new Intent(this, motify_page.class));
        });
    }

    private void set_image() {
        image.setOnClickListener((v) -> {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, GET_IMAGE); // 2 means the pictures

        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidViewTools.setFont();
        setContentView(R.layout.page4);
        AndroidViewTools.hide(this);
        bottom.setInitColor(3);

        init_views();
        set_image();
        set_view_values();
        set_password_page();
        set_motify_page();
    }

    /**
     * send a request to get the user
     */
    private void set_view_values() {
        new Thread(() -> {
            try {
                String user_name = AES.decode(pref.getString("login_user", ""));
                String url = settings.SOCKET + "/api/getuser?username=" + user_name;
                String json = HTTPUtils.getDecodeContent(url);
                Message msg = new Message();
                msg.what = GET_USER;
                msg.obj = json;
                handler.sendMessage(msg);
            } catch (Exception e) {
                Looper.prepare();
                Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private String getUriPath(Uri uri) {
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        String path = null;
        if (c.getCount() != 0) {
            if (c.moveToFirst()) {
                path = c.getString(c.getColumnIndex("_data"));
            }
        }
        IOUtils.closeQ(c);
        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == GET_IMAGE) {
                Uri uri = data.getData();
                try {
                    Bitmap bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    image.setImageBitmap(bm);
                    new Thread(() -> {
                        try {
                            String str = HTTPUtils.uploadFile(settings.SOCKET + "/api/upload?username=" + AES.decode(pref.getString("login_user", "")), getUriPath(uri));
                            if ("1".equals(JSON.parseObject(str).getString("code"))) {
                                Looper.prepare();
                                Toast.makeText(this, "文件上传成功", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            } else {
                                Looper.prepare();
                                Toast.makeText(this, "文件上传失败", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (Exception e) {
                            Looper.prepare();
                            Toast.makeText(this, "文件上传失败", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }).start();
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "文件没找到", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == Constant.REQ_QR_CODE) {
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
                //将扫描出的信息显示出来
                Toast.makeText(this, scanResult, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void init_views() {
        image = findViewById(R.id.page4_image);
        username = findViewById(R.id.page4_username);
        sign = findViewById(R.id.page4_sign);
        rights = findViewById(R.id.page4_rights);
        motify = findViewById(R.id.page4_motify);
        password = findViewById(R.id.page4_password);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        e = pref.edit();
    }
}
