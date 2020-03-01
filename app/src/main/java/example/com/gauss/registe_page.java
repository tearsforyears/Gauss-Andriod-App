package example.com.gauss;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import example.com.gauss.DTO.User;
import example.com.gauss.utils.AndroidViewTools;
import example.com.gauss.utils.HTTPUtils;
import example.com.gauss.utils.MD5;
import example.com.gauss.utils.PhoneCodeUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.util.function.LongUnaryOperator;

public class registe_page extends AppCompatActivity {
    private EditText registe_user_name;
    private EditText registe_password;
    private EditText registe_password_confirm;
    private EditText registe_phone;
    private EditText registe_phone_code;
    private EditText registe_email;
    private EditText registe_sign;

    private static Button registe_phone_code_btn;
    private Button registe_btn;


    private String phone_code; //临时保存下验证码
    private static final int TIMER = 1;
    private static final int DEFAULT_TIMER = 2;

    @SuppressLint("HandlerLeak")
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIMER:
                    //UI Timer 效果
                    registe_phone_code_btn.setText("再次发送验证码" + (int) msg.obj);
                    break;
                case DEFAULT_TIMER:
                    registe_phone_code_btn.setText("获取短信验证码");
                    registe_phone_code_btn.setBackgroundColor(0x66ff7700);
                    registe_phone_code_btn.setClickable(true);
                    break;
                default:
                    break;
            }
        }
    };

    private void set_timer() {
        new Thread(() -> {
            long begin_time = System.currentTimeMillis();
            long count;

            while ((count = (System.currentTimeMillis() - begin_time) / 1000) <= 10) {
                Message message = new Message();
                message.what = TIMER;
                message.obj = 10 - (int) count;
//                Log.d("" + (int) message.obj, "set_phone_code: ");
                handler.sendMessage(message); //发送异步处理信号
                try {
                    Thread.sleep(100); //定时器的扫描间隔
                } catch (Exception e) {
                    //do nothing
                }
            }
            Message message = new Message();
            message.what = DEFAULT_TIMER;
            handler.sendMessage(message);
        }).start();
    }

    private void set_phonecode_sender(String url) {
        new Thread(() -> {
            try {
                HTTPUtils.getContent(url); //只是为了发起请求 内容就不需要了
                Looper.prepare();
                Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();
                Looper.loop();
            } catch (Exception e) {
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(this, "发送短信失败请稍候重试", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }

    private void set_phone_code() {
        registe_phone_code_btn.setOnClickListener((v) -> {
            String phone = registe_phone.getText().toString();
            if (!"".equals(phone) && phone.matches("1(?:3\\d|4[4-9]|5[0-35-9]|6[67]|7[013-8]|8\\d|9\\d)\\d{8}")) {
                //UI变色
                registe_phone_code_btn.setBackgroundColor(0x00ff7700);
                //禁止点击
                registe_phone_code_btn.setClickable(false);
                //手机号正确 可以发送验证码
                String code = PhoneCodeUtil.genRandomString();
                //验证码判断留在set_registe中所以要把验证码保存起来
                phone_code = code;
                String url = PhoneCodeUtil.getRequestUrl(phone, code);

                //发送短信
                set_phonecode_sender(url);

                //UI 计时器效果
                set_timer();
            } else {
                Toast.makeText(this, "手机号不正确", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean judge_password() {
        String psw = registe_password.getText().toString().trim();
        String psw_confirm = registe_password_confirm.getText().toString().trim();
        if (!"".equals(psw) && !"".equals(psw_confirm)) {
            if (psw.equals(psw_confirm)) {
                return true;
            }
        }
        return false;
    }

    private boolean judge_username() throws Exception {
        String username = registe_user_name.getText().toString().trim();
        String query = "/api/exists?username=" + username;
        String str = HTTPUtils.getJsonStringByKey(settings.SOCKET + query, "code");
        if (!"".equals(username)) {
            if ("1".equals(str)) { //后端接口:如果存在数据库存在用户名 返回code:1
                return false;
            }
        }
        return true;
    }

    private boolean judge_email() {
        if ("".equals(registe_email.getText().toString().trim())) {
            return true; //电子邮箱可以为空
        }
        if (registe_email.getText().toString().trim().matches("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$")) {
            return true;
        }
        return false;
    }

    private boolean judge_phonecode() {
        //debug used
        if ("1314527".equals(registe_phone_code.getText().toString().trim())) {
            return true;
        }
        if (phone_code != null && !"".equals(registe_phone_code.getText().toString().trim())) {
            if (phone_code.equals(registe_phone_code.getText().toString().trim())) {
                return true;
            }
        }
        return false;
    }

    private void registe_to_database() {
        //此方法需要用到网络故在线程中进行判断
        new Thread(() -> {
            try {
                if (judge_username()) {
                    //可以注册,封装信息发送数据库
                    User u = new User();
                    u.setUsername(registe_user_name.getText().toString().trim());
                    u.setPasswordSalt(MD5.GetMD5Salt(registe_password.getText().toString().trim()));
                    u.setPhone(registe_phone.getText().toString().trim());
                    u.setSign(registe_sign.getText().toString().trim());
                    u.setEmail(registe_email.getText().toString().trim());
                    String url = settings.SOCKET + "/api/insert";
                    String res = HTTPUtils.getContentByJsonQuery(url, u.toString());
                    if ("1".equals(JSON.parseObject(res).getString("code"))) {
                        Looper.prepare();
                        Toast.makeText(this, "用户创建成功", Toast.LENGTH_SHORT).show();
                        this.finish();
                        Looper.loop();
                    } else {
                        Looper.prepare();
                        Toast.makeText(this, "用户创建失败", Toast.LENGTH_SHORT).show();
                        this.finish();
                        Looper.loop();
                    }
                } else {
                    Looper.prepare();
                    Toast.makeText(this, "用户名重复", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (Exception e) {
                Looper.prepare();
                Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }

    private void set_registe() {
        registe_btn.setOnClickListener((v) -> {
            if (judge_email()) {
                if (judge_password()) {
                    if (judge_phonecode()) {
                        registe_to_database();
                    } else {
                        Toast.makeText(this, "验证码不正确", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "邮箱格式错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidViewTools.setFont();
        setContentView(R.layout.activity_registe_page);
        AndroidViewTools.hide(this);
        init_views();
        set_phone_code();
        set_registe();
    }

    private void init_views() {
        registe_user_name = (EditText) findViewById(R.id.registe_user_name);
        registe_password = (EditText) findViewById(R.id.registe_password);
        registe_password_confirm = (EditText) findViewById(R.id.registe_password_confirm);
        registe_phone = (EditText) findViewById(R.id.registe_phone);
        registe_phone_code = (EditText) findViewById(R.id.registe_phone_code);
        registe_email = (EditText) findViewById(R.id.registe_email);
        registe_sign = (EditText) findViewById(R.id.registe_sign);

        registe_phone_code_btn = (Button) findViewById(R.id.registe_phone_code_btn);
        registe_btn = (Button) findViewById(R.id.registe_btn);

        registe_phone.setInputType(InputType.TYPE_CLASS_PHONE);
        registe_phone_code.setInputType(InputType.TYPE_CLASS_NUMBER);
        registe_email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
