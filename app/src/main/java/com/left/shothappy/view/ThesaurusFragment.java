package com.left.shothappy.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.left.shothappy.R;
import com.left.shothappy.utils.IcibaTranslate;

/**
 * 词库的页面
 */
public class ThesaurusFragment extends Fragment {

    /**
     * 接收到网络请求回复的数据之后通知UI更新
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("status");
        }
    };
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
                IcibaTranslate.generateDictionary(getActivity(), getActivity().getAssets().open("animal.txt"), "animal.xml");
                Snackbar.make(getView(), "success", Snackbar.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(getView(), getString(R.string.error_network), Snackbar.LENGTH_SHORT).show();
            }
            data.putString("status", "翻译失败，请检查网络");
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };
    private Button download;
    private Button test;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thesaurus, container, false);
        download = (Button) view.findViewById(R.id.download);
        test = (Button) view.findViewById(R.id.test);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(networkTask).start();
            }
        });
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    IcibaTranslate.go(getActivity(), "animal.xml");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
