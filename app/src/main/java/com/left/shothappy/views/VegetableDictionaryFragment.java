package com.left.shothappy.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.left.shothappy.R;

/**
 * Created by left on 16/3/30.
 */
public class VegetableDictionaryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vegetables, container, false);

        return view;
    }
}
