package com.left.shothappy.views;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.left.shothappy.R;

/**
 * AR认知的页面
 */
public class ARFragment extends Fragment {

    static String key = "d5356f4fba54d722115519ad830267a5zb8JO6yVcvKqdMGZREIYgvtkTjIlmPiUibOw0ge9OsN5DjcVfrOJKpUGM1MwEavrkvcZuEVvKB78wbeIsscymKohIytJAzPWYRhcMlDD3q9oKr5uBTiVtUHizWuMpcxxo0LLtKRdwJE0rbTEnliocezla7mnTJXmN1PzwniC";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ar, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

}
