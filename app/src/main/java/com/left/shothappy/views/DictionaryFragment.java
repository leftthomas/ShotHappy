package com.left.shothappy.views;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.left.shothappy.R;
import com.left.shothappy.adapters.DictionaryAdapter;
import com.left.shothappy.bean.Dict;
import com.left.shothappy.bean.User;
import com.left.shothappy.utils.IcibaTranslate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobUser;

/**
 * Created by left on 16/3/30.
 */
public class DictionaryFragment extends Fragment {

    private PullToRefreshListView mPullToRefreshListView;
    private CardView cardView;
    private MediaPlayer player;
    private List<Map<String, Object>> mItemList;
    private DictionaryAdapter adapter;
    private String type;//用来表明是哪一种词典，实例化fragment时记得一定要赋值
    private int index;//用来控制每次加载单词个数
    private Dict dict;
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

                if (cardView.getVisibility() == View.VISIBLE) {
                    cardView.setVisibility(View.INVISIBLE);
                } else {
                    setcard(dict);
                    cardView.setVisibility(View.VISIBLE);
                }
            } else {
                Snackbar.make(getView(), val, Snackbar.LENGTH_SHORT).show();
            }

        }
    };
    private User user;
    private String source;
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
                dict = IcibaTranslate.translate(source);
                data.putString("status", "true");//表示请求成功
            } catch (Exception e) {
                e.printStackTrace();
                data.putString("status", "翻译失败，请检查网络");
            }
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    //记得一定要调用
    public void setType(String name) {
        type = name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictionary, container, false);

        initData();
        user = BmobUser.getCurrentUser(getContext(), User.class);
        adapter = new DictionaryAdapter(getContext(), mItemList);
        //初始化控件
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
        cardView = (CardView) view.findViewById(R.id.cardview);
        final ListView mListView = mPullToRefreshListView.getRefreshableView();

        mListView.setAdapter(adapter);

        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                getData();
            }
        });
        mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                source = adapter.getItem(position - 1).get("word").toString();
                new Thread(networkTask).start();
            }
        });

        return view;
    }

    private void initData() {
        //初始化数据
        mItemList = new ArrayList<>();
        index = 0;
        String line;
        try {
            InputStream inputStream = getActivity().getAssets().open(type + ".txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                index++;
                //第一次就加载20个单词
                if (index <= 20) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("word", line);          //单词
                    mItemList.add(map);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //上拉加载更多单词
    private void getData() {
        String line;
        //游标，用来控制加载的单词从哪里开始
        int x = 0, t = 0;
        //用来判断是否全部加载过了
        boolean flag = false;
        try {
            InputStream inputStream = getActivity().getAssets().open(type + ".txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                x++;
                //这种情况，说明还有单词没加载进来，还可以加载
                if (x > index && t < 20) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("word", line);          //单词
                    mItemList.add(map);
                    t++;
                    flag = true;
                } else if (t >= 20) {
                    //已经加载了20个
                    break;
                }
            }
            //别忘了更新下这个值
            index += t;
            //通知程序数据集已经改变，如果不做通知，那么将不会刷新mListItems的集合
            adapter.notifyDataSetChanged();
            mPullToRefreshListView.postDelayed(new Runnable() {
                @Override
                public void run() {

                    mPullToRefreshListView.onRefreshComplete();
                }
            }, 200);
            //单词之前已经全部加载完了
            if (!flag) {
                Snackbar.make(getView(), getString(R.string.wordfinished), Snackbar.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对弹出的cardview中的各个控件值进行相应设置
     *
     * @param dict
     */
    private void setcard(final Dict dict) {
        TextView key = (TextView) cardView.findViewById(R.id.key);
        TextView ps1 = (TextView) cardView.findViewById(R.id.ps1);
        TextView ps2 = (TextView) cardView.findViewById(R.id.ps2);
        TextView pos = (TextView) cardView.findViewById(R.id.pos);
        TextView acceptation = (TextView) cardView.findViewById(R.id.acceptation);
        TextView orig = (TextView) cardView.findViewById(R.id.orig);
        TextView trans = (TextView) cardView.findViewById(R.id.trans);

        ImageView ps1sound = (ImageView) cardView.findViewById(R.id.ps1sound);
        ImageView ps2sound = (ImageView) cardView.findViewById(R.id.ps2sound);

        key.setText(dict.getKey());
        ps1.setText("美 [" + dict.getPs_prons().get(0).getPs() + "]");
        ps2.setText("英 [" + dict.getPs_prons().get(1).getPs() + "]");
        pos.setText(dict.getPos_acceptations().get(0).getPos());
        acceptation.setText(dict.getPos_acceptations().get(0).getAcceptation());
        orig.setText(dict.getSents().get(0).getOrig());
        trans.setText(dict.getSents().get(0).getTrans());

        ps1sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 播放美音
                String path = dict.getPs_prons().get(0).getPron();
                Uri uri = Uri.parse(path);
                player = MediaPlayer.create(getContext(), uri);
                player.start();
            }
        });
        ps2sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 播放英音
                String path = dict.getPs_prons().get(1).getPron();
                Uri uri = Uri.parse(path);
                player = MediaPlayer.create(getContext(), uri);
                player.start();
            }
        });
    }
}
