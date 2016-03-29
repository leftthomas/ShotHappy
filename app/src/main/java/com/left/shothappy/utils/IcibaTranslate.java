package com.left.shothappy.utils;

import com.left.shothappy.bean.Pos_acceptation;
import com.left.shothappy.bean.Ps_pron;
import com.left.shothappy.bean.Sent;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * 金山词霸翻译引擎
 */
public class IcibaTranslate {

    //开发者id
    private static final String appId = "32EC9EECC5DBCCC4DA62D8EDAB254D85";

    //金山词霸API URL
    private static final String address = "http://dict-co.iciba.com/api/dictionary.php";

    //传英文单词进来
    public static Dict translate(String w) throws Exception {

        String request = address + "?w=" + w + "&key=" + appId;

        URL url = new URL(request);

        URLConnection con = url.openConnection();
        con.connect();
//        InputStreamReader reader = new InputStreamReader(con.getInputStream());
//        BufferedReader bufread = new BufferedReader(reader);
//        StringBuffer buff = new StringBuffer();
//        String line;
//        while ((line = bufread.readLine()) != null) {
//            buff.append(line);
//        }
//        reader.close();
//        bufread.close();

        Dict dict = parsexml(con.getInputStream());

        return dict;
    }

    //解析xml生成对应的dict实例
    private static Dict parsexml(InputStream xml) throws DocumentException {
        Dict dict = new Dict();

        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(xml);
        // 获取根元素
        Element root = document.getRootElement();
        // 获取特定名称的子元素
        Element key = root.element("key");
        dict.setKey(key.getText());

        List<Element> pss = root.elements("ps");
        List<Element> prons = root.elements("pron");
        List<Ps_pron> ps_prons = new ArrayList<>();
        for (int i = 0; i < pss.size(); i++) {
            Ps_pron ps_pron = new Ps_pron();
            ps_pron.setPs(pss.get(i).getText());
            ps_pron.setPron(prons.get(i).getText());
            ps_prons.add(ps_pron);
        }
        dict.setPs_prons(ps_prons);

        List<Element> poss = root.elements("pos");
        List<Element> acceptations = root.elements("acceptation");
        List<Pos_acceptation> pos_acceptations = new ArrayList<>();
        for (int i = 0; i < poss.size(); i++) {
            Pos_acceptation pos_acceptation = new Pos_acceptation();
            pos_acceptation.setPos(poss.get(i).getText());
            pos_acceptation.setAcceptation(acceptations.get(i).getText());
            pos_acceptations.add(pos_acceptation);
        }
        dict.setPos_acceptations(pos_acceptations);

        List<Element> sents = root.elements("sent");
        List<Sent> sentList = new ArrayList<>();
        for (int i = 0; i < sents.size(); i++) {
            Sent sent = new Sent();
            sent.setOrig(sents.get(i).element("orig").getText());
            sent.setTrans(sents.get(i).element("trans").getText());
            sentList.add(sent);
        }
        dict.setSents(sentList);
        return dict;
    }

}
