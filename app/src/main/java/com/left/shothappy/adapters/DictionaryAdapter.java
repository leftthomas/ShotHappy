package com.left.shothappy.adapters;


import android.content.Context;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.left.shothappy.MainActivity;
import com.left.shothappy.R;
import com.left.shothappy.bean.Ps_pron;
import com.left.shothappy.bean.User;
import com.left.shothappy.utils.IcibaTranslate;

import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobUser;

/**
 * Created by left on 16/4/5.
 */
public class DictionaryAdapter extends BaseAdapter {
    private Context context;

    private LayoutInflater layoutInflater;

    private List<Map<String, Object>> list;

    private String source;
    private List<Ps_pron> prons;
    /**
     * 网络操作相关的子线程
     * 调用语音sdk与英文释义部分的网络请求
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // 在这里进行 http request.网络请求相关操作
            Message msg = new Message();
            Bundle data = new Bundle();
            try {
                prons = IcibaTranslate.getProns(source);
                data.putString("status", "true");//表示请求成功
            } catch (Exception e) {
                e.printStackTrace();
                data.putString("status", "发音失败，请检查网络");
            }
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };
    private AsyncPlayer player;
    /**
     * 接收到网络请求回复的数据之后通知UI更新
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("status");
            if (val.equals("true")) {
                // 播放声音
                String path;
                User user = BmobUser.getCurrentUser(context, User.class);
                //判断是美音还是英音
                if (user.isPronunciation()) {
                    path = prons.get(0).getPron();     //这里给一个歌曲的网络地址就行了
                } else {
                    path = prons.get(1).getPron();
                }
                Uri uri = Uri.parse(path);
                player.play(context, uri, false, AudioManager.STREAM_MUSIC);
            } else {
                Toast.makeText(context, val, Toast.LENGTH_SHORT).show();
            }

        }
    };

    //构造方法，参数list传递的就是这一组数据的信息
    public DictionaryAdapter(Context context, List<Map<String, Object>> list) {
        this.context = context;

        layoutInflater = LayoutInflater.from(context);

        this.list = list;
        player = new AsyncPlayer("audio");

    }

    //得到总的数量
    public int getCount() {
        return this.list != null ? this.list.size() : 0;
    }

    //根据ListView位置返回View
    public Map<String, Object> getItem(int position) {
        return this.list.get(position);
    }

    //根据ListView位置得到List中的ID
    public long getItemId(int position) {
        return position;
    }

    //根据位置得到View对象
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.view_dictionary_item, null);
        }

        //得到条目中的子组件
        TextView tv = (TextView) convertView.findViewById(R.id.wordTextView);
        final ImageView iv = (ImageView) convertView.findViewById(R.id.speakButton);
        final String word = list.get(position).get("word").toString();
        //从list对象中为子组件赋值
        tv.setText(word);
        tv.setTypeface(MainActivity.typeFace);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开启一个子线程，进行翻译与语音API请求，注意，不能直接在主线程操作
                source = word;
                new Thread(networkTask).start();
            }
        });
        return convertView;
    }
}
