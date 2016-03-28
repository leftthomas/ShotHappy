package com.left.shothappy.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * 百度翻译引擎
 */
public class BaiduTranslate {

    //申请者开发者id
    private static final String appId = "20160328000016796";

    private static final String key = "3a8ro1ZNbZ_CeL6eWMyK";

    private static final String address = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    //随机数，用于生成md5值
    private static final Random random = new Random();

    public static String translate(String q, String from, String to) throws Exception {

        int salt = random.nextInt(10000);

        // 对appId+源文+随机数+token计算md5值
        StringBuilder md5String = new StringBuilder();
        md5String.append(appId).append(q).append(salt).append(key);
        String sign = md5(md5String.toString());

        // 对中文字符进行编码,否则传递乱码
        q = URLEncoder.encode(q, "utf-8");
        URL url = new URL(address + "?q=" + q + "&from="
                + from + "&to=" + to + "&appid=" + appId + "&salt=" + salt + "&sign=" + sign);

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
        String back = new String(buff.toString().getBytes("ISO-8859-1"),
                "UTF-8");
        String text = JsonToString(back);
        reader.close();
        bufread.close();

        return text;
    }


    /**
     * 获取json中翻译的内容
     *
     * @param jstring
     * @return
     */
    private static String JsonToString(String jstring) throws JSONException {
        JSONObject obj = new JSONObject(jstring);
        JSONArray array = obj.getJSONArray("trans_result");
        obj = array.getJSONObject(0);
        String word = obj.getString("dst");
        return word;
    }

    //md5加密
    private static String md5(String string) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] hash;
        hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}
