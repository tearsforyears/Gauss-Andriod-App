package example.com.gauss.utils;

import android.util.Log;

import java.io.*;

public class IOUtils {
    public static void closeQ(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {

            }
        }
    }

    /**
     * to get the content by inputstream
     * this method do not close the origin inputstream
     *
     * @param is for inputstream
     * @return inputstream content
     */
    public static String getContent(InputStream is) {
        if (is == null)
            return null;
        else {
            InputStreamReader isr = null;
            BufferedReader bfr = null;
            StringBuilder sb = new StringBuilder();
            try {
                isr = new InputStreamReader(is);
                bfr = new BufferedReader(isr);
                String line;
                while ((line = bfr.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeQ(bfr);
                closeQ(isr);
            }
            return sb.toString();
        }
    }
}
