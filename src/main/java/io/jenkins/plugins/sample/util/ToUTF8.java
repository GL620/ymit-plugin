package io.jenkins.plugins.sample.util;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * Created by phy on 2018/11/1.
 */
public class ToUTF8 {
    public static String execute(String str) {
        if (isEmpty(str)) {
            return "";
        }
        try {
            if (str.equals(new String(str.getBytes("GB2312"), "GB2312"))) {
                str = new String(str.getBytes("GB2312"), "UTF-8");
                return str;
            }
        } catch (Exception exception) {
        }
        try {
            if (str.equals(new String(str.getBytes("ISO-8859-1"), "ISO-8859-1"))) {
                str = new String(str.getBytes("ISO-8859-1"), "UTF-8");
                return str;
            }
        } catch (Exception exception1) {
        }
        try {
            if (str.equals(new String(str.getBytes("GBK"), "GBK"))) {
                str = new String(str.getBytes("GBK"), "UTF-8");
                return str;
            }
        } catch (Exception exception3) {
        }
        return str;
    }

}
