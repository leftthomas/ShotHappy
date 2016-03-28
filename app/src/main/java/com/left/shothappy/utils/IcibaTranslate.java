package com.left.shothappy.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * 金山词霸翻译引擎
 */
public class IcibaTranslate {

    //开发者id
    private static final String appId = "32EC9EECC5DBCCC4DA62D8EDAB254D85";

    //金山词霸API URL
    private static final String address = "http://dict-co.iciba.com/api/dictionary.php";

    //传英文单词进来
    public static String translate(String w) throws Exception {

        String request = address + "?w=" + w + "&key=" + appId;

        URL url = new URL(request);

        URLConnection con = url.openConnection();
        con.connect();
        InputStreamReader reader = new InputStreamReader(con.getInputStream());
        BufferedReader bufread = new BufferedReader(reader);
        StringBuffer buff = new StringBuffer();
        String line;
        while ((line = bufread.readLine()) != null) {
            buff.append(line);
        }
        // 对字符进行解码
        String back = buff.toString();
        reader.close();
        bufread.close();

        return back;
    }

}
