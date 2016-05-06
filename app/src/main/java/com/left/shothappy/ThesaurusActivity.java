package com.left.shothappy;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.left.shothappy.adapters.ThesaurusAdapter;
import com.left.shothappy.views.DictionaryFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 词库的页面
 */
public class ThesaurusActivity extends AppCompatActivity {

    private TabLayout tab_FindFragment_title;                            //定义TabLayout
    private ViewPager vp_FindFragment_pager;                             //定义viewPager
    private FragmentPagerAdapter fAdapter;                               //定义adapter

    private List<Fragment> list_fragment;                                //定义要装fragment的列表
    private List<String> list_title;                                     //tab名称列表

    private DictionaryFragment animalFragment;             //动物fragment
    private DictionaryFragment fruitFragment;              //水果fragment
    private DictionaryFragment vegetableFragment;       //蔬菜fragment


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thesaurus);
        initControls();
    }


    /**
     * 初始化各控件
     */
    private void initControls() {

        tab_FindFragment_title = (TabLayout) findViewById(R.id.tab_fragment_title);
        vp_FindFragment_pager = (ViewPager) findViewById(R.id.vp_fragment_pager);

        //初始化各fragment
        animalFragment = new DictionaryFragment();
        animalFragment.setType(getString(R.string.animals));
        fruitFragment = new DictionaryFragment();
        fruitFragment.setType(getString(R.string.fruits));
        vegetableFragment = new DictionaryFragment();
        vegetableFragment.setType(getString(R.string.vegetables));

        //将fragment装进列表中
        list_fragment = new ArrayList<>();
        list_fragment.add(animalFragment);
        list_fragment.add(fruitFragment);
        list_fragment.add(vegetableFragment);

        //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
        list_title = new ArrayList<>();
        list_title.add(getString(R.string.animals_dictionary));
        list_title.add(getString(R.string.fruits_dictionary));
        list_title.add(getString(R.string.vegetables_dictionary));

        //设置TabLayout的模式
        tab_FindFragment_title.setTabMode(TabLayout.MODE_FIXED);
        //为TabLayout添加tab名称
        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(0)));
        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(1)));
        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(2)));

        fAdapter = new ThesaurusAdapter(getSupportFragmentManager(), list_fragment, list_title);

        //viewpager加载adapter
        vp_FindFragment_pager.setAdapter(fAdapter);
        //设置缓存view 的个数（实际有3个，缓存2个+正在显示的1个）
        vp_FindFragment_pager.setOffscreenPageLimit(2);
        //TabLayout加载viewpager
        tab_FindFragment_title.setupWithViewPager(vp_FindFragment_pager);
    }

}
