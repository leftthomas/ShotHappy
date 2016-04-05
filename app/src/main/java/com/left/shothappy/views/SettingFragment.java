package com.left.shothappy.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.left.shothappy.LoginActivity;
import com.left.shothappy.R;
import com.left.shothappy.bean.Feedback;
import com.left.shothappy.bean.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;
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
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                builder.setTitle(R.string.change_password);
                builder.setCancelable(false);
                final View view_changepassword = LayoutInflater.from(getActivity()).inflate(R.layout.view_changepassword, null);
                builder.setView(view_changepassword);
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        EditText oldpassword = (EditText) view_changepassword.findViewById(R.id.old_password);
                        EditText newpassword = (EditText) view_changepassword.findViewById(R.id.new_password);
                        if (TextUtils.isEmpty(oldpassword.getText())) {
                            Snackbar.make(view, getString(R.string.in_oldpassword), Snackbar.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(newpassword.getText())) {
                            Snackbar.make(view, getString(R.string.in_newpassword), Snackbar.LENGTH_SHORT).show();
                        } else {
                            User.updateCurrentUserPassword(getActivity(), oldpassword.getText().toString(), newpassword.getText().toString(), new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    dialog.dismiss();
                                    Snackbar.make(view, getString(R.string.success_changepassword), Snackbar.LENGTH_LONG).show();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    dialog.dismiss();
                                    Snackbar.make(view, getString(R.string.fail_changepassword), Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
                builder.show();
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                builder.setTitle(R.string.feedback);
                builder.setCancelable(false);
                final View view_feedback = LayoutInflater.from(getActivity()).inflate(R.layout.view_feedback, null);
                builder.setView(view_feedback);
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        EditText content = (EditText) view_feedback.findViewById(R.id.content);
                        if (TextUtils.isEmpty(content.getText())) {
                            Snackbar.make(view, getString(R.string.in_feedback_content), Snackbar.LENGTH_SHORT).show();
                        } else {
                            User user = BmobUser.getCurrentUser(getActivity(), User.class);
                            Feedback post = new Feedback();
                            post.setContent(content.getText().toString());
                            //添加一对一关联
                            post.setUser(user);
                            post.save(getActivity(), new SaveListener() {

                                @Override
                                public void onSuccess() {
                                    dialog.dismiss();
                                    Snackbar.make(view, getString(R.string.success_feedback), Snackbar.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int code, String msg) {
                                    dialog.dismiss();
                                    Snackbar.make(view, getString(R.string.error_network), Snackbar.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                });
                builder.show();
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
