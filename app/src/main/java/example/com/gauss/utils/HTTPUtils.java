package example.com.gauss.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class HTTPUtils {

    public static InputStream getInputStream(String url) {
        return getInputStream(url, "");
    }

    public static InputStream getInputStream(String url, String sessionID) {
        InputStream is = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            if (!"".equals(sessionID))
                conn.setRequestProperty("cookie", sessionID);

            if (conn.getResponseCode() == 200) {
                conn.connect();
                is = conn.getInputStream();
            } else {
                is = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return is;
    }

    /**
     * this function use to support the function get DeCodeContent
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static String getContent(String url) throws Exception {
        return getContent(url, "");
    }

    public static String getContent(String url, String sessid) throws Exception {
        InputStream is = null;
        try {
            is = getInputStream(url, sessid);

        } catch (Exception e) {
            throw new RuntimeException("do not get the inputStream");
        }
        String content = IOUtils.getContent(is);
        IOUtils.closeQ(is);
        return content;
    }


    /**
     * every thing send by server must be encrypt
     * so use this function to decode the content
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static String getDecodeContent(String url) throws Exception {
        return AES.decode(getContent(url));
    }

    public static String getDecodeContent(String url, String sessid) throws Exception {
        return AES.decode(getContent(url, sessid));
    }

    public static String getJsonStringByKey(String url, String key) throws Exception {
        return getJsonStringByKey(url, key, "");
    }

    public static String getJsonStringByKey(String url, String key, String sessid) throws Exception {
        String str = getDecodeContent(url, sessid);
        JSONObject json = JSONObject.parseObject(str);
        return json.getString(key);
    }

    public static String getContentByJsonQuery(String url, String json) throws Exception {
        return getContentByJsonQuery(url, json, "");
    }

    public static String getContentByJsonQuery(String url, String json, String sessid) throws Exception {
        JSONObject jo = JSON.parseObject(json);
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        for (String key : jo.keySet()) {
            sb.append(key);
            sb.append("=");
            sb.append(jo.getString(key));
            sb.append("&");
        }
//        System.out.println(url + sb.toString());
        return getDecodeContent(url + sb.toString(), sessid);
    }

    public static String getSessionID(String url) {
        String sessionID;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            String cookieValue = connection.getHeaderField("set-cookie");
            if (cookieValue != null) {
                sessionID = cookieValue.substring(0, cookieValue.indexOf(";"));
            } else {
                sessionID = "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            sessionID = "";
        }
        return sessionID.substring(8);
    }

    private static final String nextLine = "\r\n";
    private static final String twoHyphens = "--";
    private static final String boundary = "wk_file_2519775";

    public static String uploadFile(String url, String file_name) {
        File file = new File(file_name);
        HttpURLConnection conn = null;
        OutputStream out = null;
        FileInputStream in = null;
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("charset", "UTF-8");
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("Accept", "application/json");
            conn.connect();

            //获取http写入流
            out = new DataOutputStream(conn.getOutputStream());

            //分隔符头部
            String header = twoHyphens + boundary + nextLine;
            //分隔符参数设置
            header += "Content-Disposition: form-data;name=\"file\";" + "filename=\"" + file.getName() + "\"" + nextLine + nextLine;
            //写入输出流
            out.write(header.getBytes());

            //读取文件并写入
            in = new FileInputStream(file);
            byte[] bytes = new byte[2048];
            int length;
            while ((length = in.read(bytes)) != -1) {
                out.write(bytes, 0, length);
            }
            //文件写入完成后加回车
            out.write(nextLine.getBytes());

            //写入结束分隔符
            String footer = nextLine + twoHyphens + boundary + twoHyphens + nextLine;
            out.write(footer.getBytes());
            out.flush();

            //文件上传完成
            InputStream response = conn.getInputStream();
            InputStreamReader reader = new InputStreamReader(response);
            BufferedReader bfr = new BufferedReader(reader);
            while ((line = bfr.readLine()) != null) {
                sb.append(line);
            }
            line = AES.decode(sb.toString());
        } catch (Exception e) {
            System.out.println("上传出错");
        } finally {
            IOUtils.closeQ(in);
            IOUtils.closeQ(out);
            if (conn != null)
                conn.disconnect();
        }
        return line;
    }

}
