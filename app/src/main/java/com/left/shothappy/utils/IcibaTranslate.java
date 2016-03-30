package com.left.shothappy.utils;

import android.content.Context;

import com.left.shothappy.bean.Dict;
import com.left.shothappy.bean.Pos_acceptation;
import com.left.shothappy.bean.Ps_pron;
import com.left.shothappy.bean.Sent;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        Dict dict = parseXml(con.getInputStream());

        return dict;
    }

    //解析xml生成对应的dict实例
    private static Dict parseXml(InputStream xml) throws DocumentException {
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

    /**
     * 生成词典，保存到目录下，用来做离线翻译词典
     */
    public static void generateDictionary(Context context, InputStream indexfile, String filename) throws IOException, DocumentException {
        Document doc = DocumentHelper.createDocument();
        //增加根节点
        Element dicts = doc.addElement("dicts");
        InputStreamReader inputStreamReader = new InputStreamReader(indexfile, "utf-8");
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line;
        SAXReader saxReader = new SAXReader();
        while ((line = reader.readLine()) != null) {
            String request = address + "?w=" + line + "&key=" + appId;
            URL url = new URL(request);
            URLConnection con = url.openConnection();
            con.connect();
            InputStream returnword = con.getInputStream();
            Document dict = saxReader.read(returnword);
            //获得根节点下的节点信息
            List<Element> elements = dict.getRootElement().elements();
            Element parent = dict.getRootElement();
            for (Element element : elements) {
                //将dict下的节点添加到根节点下
                parent.add(element.detach());
            }
            dicts.add(parent);
        }
        //实例化输出格式对象
        OutputFormat format = OutputFormat.createPrettyPrint();
        //设置输出编码
        format.setEncoding("UTF-8");
        //创建需要写入的File对象
        FileOutputStream out = context.openFileOutput(filename, Context.MODE_PRIVATE);

        // 获得用户公共的文档目录
//        File file = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DOCUMENTS), filename);
//        if (!file.exists()) {
//            file.createNewFile();
//        }
//        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);


        //生成XMLWriter对象，构造函数中的参数为需要输出的文件流和格式
        XMLWriter writer = new XMLWriter(out, format);
        //开始写入，write方法中包含上面创建的Document对象
        writer.write(doc);
        writer.close();
        //开始下载音频文件
        downloadOfflineAudio(context, filename);
    }


    /**
     * 离线词典xml download完成之后要对词典解析，去网络下载音频文件，替换相应xml节点音频文件路径
     * 下载音频文件比较耗时，可以和下载解释分开
     * @param context
     * @param filename
     * @throws DocumentException
     */
    private static void downloadOfflineAudio(Context context, String filename) throws IOException, DocumentException {
        String name = filename.substring(0, filename.indexOf(".xml")) + "_audios";
        String address = context.getFilesDir().getParentFile().getPath() + "/" + name + "/";
        File file = new File(address);
        if (!file.exists()) {
            file.mkdir();
        }

        FileInputStream in = context.openFileInput(filename); //获得输入流
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(in);
        //实例化输出格式对象
        OutputFormat format = OutputFormat.createPrettyPrint();
        //设置输出编码
        format.setEncoding("UTF-8");
        // 获取根元素－－dicts
        Element root = document.getRootElement();
        // 获取所有dict
        List dicts = root.elements("dict");

        for (int i = 0; i < dicts.size(); i++) {
            //对于每一个dict都要替换音频文件
            List prons = ((Element) (dicts.get(i))).elements("pron");
            for (int j = 0; j < prons.size(); j++) {
                Element pron = (Element) (prons.get(j));
                //获取到音频文件url，去网络下载文件到本地
                String weburl = pron.getText();
                URL url = new URL(weburl);
                URLConnection con = url.openConnection();
                con.connect();
                InputStream is = con.getInputStream();
                String new_path = address + weburl.replace("/", "");
                File audio = new File(new_path);
                if (!audio.exists())
                    audio.createNewFile();
                FileOutputStream fos = new FileOutputStream(audio);
                byte[] buf = new byte[1024];
                while ((is.read(buf)) != -1) {
                    fos.write(buf);
                    //通知UI
                }
                //记得更新相应标签，需要写回去
                pron.setText(new_path);
                //创建需要写入的File对象
                FileOutputStream out = context.openFileOutput(filename, Context.MODE_PRIVATE);
                //生成XMLWriter对象，构造函数中的参数为需要输出的文件流和格式
                XMLWriter writer = new XMLWriter(out, format);
                //开始写入，write方法中包含上面创建的Document对象
                writer.write(document);

                writer.close();
                fos.close();
                is.close();
            }
        }
        //关闭
        in.close();
    }

    /**
     * 暂时做测试用
     *
     * @param context
     * @param filename
     * @throws IOException
     */
    public static void go(Context context, String filename) throws IOException, DocumentException {
        FileInputStream in = context.openFileInput(filename); //获得输入流
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(in);
        System.out.println(document.asXML());
        //关闭
        in.close();
    }
}
