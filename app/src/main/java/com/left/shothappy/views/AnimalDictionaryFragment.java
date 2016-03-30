package com.left.shothappy.views;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.left.shothappy.R;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by left on 16/3/30.
 */
public class AnimalDictionaryFragment extends Fragment {

    private PullToRefreshListView mPullToRefreshListView;
    private LinkedList<String> mItemList;
    private ArrayAdapter<String> adapter;
    private String[] data = new String[]{"data1", "data2", "data3", "data4", "data5", "data6",
            "data1", "data2", "data3", "data4", "data5", "data6"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_animals, container, false);

        initData();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mItemList);
        //初始化控件
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);

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
                Snackbar.make(view, "aa", Snackbar.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void initData() {
        //初始化数据
        mItemList = new LinkedList<>();
        mItemList.addAll(Arrays.asList(data));

    }

    private void getData() {
        mPullToRefreshListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToRefreshListView.onRefreshComplete();
            }
        }, 300);

    }

}
