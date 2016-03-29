package com.left.shothappy.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.left.shothappy.LoginActivity;
import com.left.shothappy.R;
import com.left.shothappy.bean.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 设置的页面
 */
public class SettingFragment extends Fragment {

    private TextView pronunciation;
    private TextView change_password;
    private TextView feedback;
    private TextView about;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        pronunciation = (TextView) view.findViewById(R.id.pronunciation);
        change_password = (TextView) view.findViewById(R.id.change_password);
        feedback = (TextView) view.findViewById(R.id.feedback);
        about = (TextView) view.findViewById(R.id.about);
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pronunciation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User user = BmobUser.getCurrentUser(getActivity(), User.class);
                if (user != null) {
                    final AlertDialog.Builder builder =
                            new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(pronunciation.getText());
                    AlertDialog dialog;
                    String[] items = {"美音", "英音"};

                    int what;
                    if (user.isPronunciation())
                        what = 0;
                    else
                        what = 1;
                    builder.setSingleChoiceItems(items, what, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            User newUser = new User();
                            if (which == 1)
                                newUser.setPronunciation(false);
                            else
                                newUser.setPronunciation(true);
                            newUser.update(getActivity(), user.getObjectId(), new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onFailure(int code, String msg) {
                                    dialog.dismiss();
                                    Snackbar.make(view, getString(R.string.error_network), Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    dialog = builder.create();
                    builder.show();
                } else {
                    //缓存用户对象为空时， 打开登录界面
                    Snackbar.make(view, getString(R.string.userinfo_overdue), Snackbar.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                }
            }
        });
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                builder.setView(R.layout.view_about);
                builder.show();
            }
        });
    }
}
