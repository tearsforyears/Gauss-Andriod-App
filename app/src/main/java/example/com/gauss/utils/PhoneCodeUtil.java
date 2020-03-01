package example.com.gauss.utils;

import android.util.Base64;
import example.com.gauss.settings;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static example.com.gauss.utils.IOUtils.closeQ;

public class PhoneCodeUtil {
    public static void main(String[] args) {
        send_msg("");
    }

    private static String genRandomString(int len) {
        StringBuilder randString = new StringBuilder();
        for (int i = 0; i < len; i++) {
            randString.append(Integer.toString((int) (Math.random() * 10)));
        }
        return randString.toString();
    }

    public static String genRandomString() {
        return genRandomString(4);
    }

    private static String send_msg(String phone) {
        StringBuilder sb = new StringBuilder();
        String code = genRandomString();
        try {
            String req_url = getRequestUrl(phone, code);
//            System.out.println(req_url);
            URL url = new URL(req_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("there is an error occur when sending a message");
        }
        return code;
    }

    public static String getRequestUrl(String phone, String code) {
        String accessKeyId = settings.ACCESSKEYID;
        String accessSecret = settings.ACCESSSECRET;
        String SignName = "Gauss";
        String templateParam = "{'code':'" + code + "'}"; //JSON格式字符串
        String templateCode = settings.TEMPLATENAME; // template code
        String url = null;
        try {
            url = getRequestUrl(accessKeyId, accessSecret, phone, SignName, templateParam, templateCode);
        } catch (Exception e) {
            System.out.println("url生成失败");
        }
        return url;
    }

    /*
        来源于阿里云api官方文档 调用api
     */
    private static String getRequestUrl(String accessKeyId, String accessSecret, String phoneNumber,
                                        String SignName, String templateParam, String templateCode) throws Exception {
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(new java.util.SimpleTimeZone(0, "GMT"));// 这里一定要设置GMT时区
        java.util.Map<String, String> paras = new java.util.HashMap<String, String>();
        // 1. 系统参数
        paras.put("SignatureMethod", "HMAC-SHA1");
        paras.put("SignatureNonce", java.util.UUID.randomUUID().toString());
        paras.put("AccessKeyId", accessKeyId);
        paras.put("SignatureVersion", "1.0");
        paras.put("Timestamp", df.format(new java.util.Date()));
        paras.put("Format", "JSON");
        // 2. 业务API参数
        paras.put("Action", "SendSms");
        paras.put("Version", "2017-05-25");
        paras.put("RegionId", "cn-hangzhou");
        paras.put("PhoneNumbers", phoneNumber);
        paras.put("SignName", SignName);
        paras.put("TemplateParam", templateParam);
        paras.put("TemplateCode", templateCode);
//        paras.put("OutId", "123");
        // 3. 去除签名关键字Key
        if (paras.containsKey("Signature"))
            paras.remove("Signature");
        // 4. 参数KEY排序
        java.util.TreeMap<String, String> sortParas = new java.util.TreeMap<String, String>();
        sortParas.putAll(paras);
        // 5. 构造待签名的字符串
        java.util.Iterator<String> it = sortParas.keySet().iterator();
        StringBuilder sortQueryStringTmp = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next();
            sortQueryStringTmp.append("&").append(specialUrlEncode(key)).append("=").append(specialUrlEncode(paras.get(key)));
        }
        String sortedQueryString = sortQueryStringTmp.substring(1);// 去除第一个多余的&符号
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append("GET").append("&");
        stringToSign.append(specialUrlEncode("/")).append("&");
        stringToSign.append(specialUrlEncode(sortedQueryString));
        String sign = sign(accessSecret + "&", stringToSign.toString());
        // 6. 签名最后也要做特殊URL编码
        String signature = specialUrlEncode(sign);
//        System.out.println(paras.get("SignatureNonce"));
//        System.out.println("\r\n=========\r\n");
//        System.out.println(paras.get("Timestamp"));
//        System.out.println("\r\n=========\r\n");
//        System.out.println(sortedQueryString);
//        System.out.println("\r\n=========\r\n");
//        System.out.println(stringToSign.toString());
//        System.out.println("\r\n=========\r\n");
//        System.out.println(sign);
//        System.out.println("\r\n=========\r\n");
//        System.out.println(signature);
//        System.out.println("\r\n=========\r\n");
        // 最终打印出合法GET请求的URL
        return "http://dysmsapi.aliyuncs.com/?Signature=" + signature + sortQueryStringTmp;
    }

    private static String specialUrlEncode(String value) throws Exception {
        return java.net.URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

    private static String sign(String accessSecret, String stringToSign) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
        mac.init(new javax.crypto.spec.SecretKeySpec(accessSecret.getBytes("UTF-8"), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        return new sun.misc.BASE64Encoder().encode(signData);
    }
}
