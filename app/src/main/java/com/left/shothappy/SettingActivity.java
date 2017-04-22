package com.left.shothappy;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.left.shothappy.bean.Feedback;
import com.left.shothappy.bean.User;
import com.left.shothappy.config.MyApplication;
import com.left.shothappy.utils.PicUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 设置的页面
 */
public class SettingActivity extends BaseActivity {

    private ImageView pronunciation;
    private ImageView change_password;
    private ImageView feedback;
    private ImageView about;
    private ImageView logout;
    private View view;
    private CircleImageView head_imageView;
    private TextView username_email;
    private User user;
    private Bitmap head;//头像Bitmap

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        pronunciation = (ImageView) findViewById(R.id.pronunciation);
        change_password = (ImageView) findViewById(R.id.change_password);
        feedback = (ImageView) findViewById(R.id.feedback);
        about = (ImageView) findViewById(R.id.about);
        logout = (ImageView) findViewById(R.id.logout);
        view = findViewById(R.id.setting_view);
        head_imageView = (CircleImageView) findViewById(R.id.imageView);
        username_email = (TextView) findViewById(R.id.username_email);

        username_email.setTypeface(MyApplication.typeFace);

        user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            //设置界面用户信息
            username_email.setText(user.getUsername() + "   " + user.getEmail());
            //显示图片的配置
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            //载入图片
            ImageLoader.getInstance().displayImage(user.getHead().getFileUrl(), head_imageView, options);
        } else {
            //缓存用户对象为空时， 打开登录界面
            Snackbar.make(view, getString(R.string.userinfo_overdue), Snackbar.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        head_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从相册里面取照片
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1);
            }
        });

        pronunciation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    final AlertDialog.Builder builder =
                            new AlertDialog.Builder(SettingActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(R.string.pronunciation);
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
                            newUser.update(user.getObjectId(), new UpdateListener() {

                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        dialog.dismiss();
                                    } else {
                                        dialog.dismiss();
                                        Snackbar.make(view, getString(R.string.error_network), Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    builder.show();
                } else {
                    //缓存用户对象为空时， 打开登录界面
                    Snackbar.make(view, getString(R.string.userinfo_overdue), Snackbar.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        });
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(SettingActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(R.string.change_password);
                builder.setCancelable(false);
                final View view_changepassword = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_changepassword, null);
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
                            BmobUser.updateCurrentUserPassword(oldpassword.getText().toString(), newpassword.getText().toString(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        dialog.dismiss();
                                        Snackbar.make(view, getString(R.string.success_changepassword), Snackbar.LENGTH_LONG).show();
                                    } else {
                                        dialog.dismiss();
                                        Snackbar.make(view, getString(R.string.fail_changepassword), Snackbar.LENGTH_LONG).show();
                                    }
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
                        new AlertDialog.Builder(SettingActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(R.string.feedback);
                builder.setCancelable(false);
                final View view_feedback = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_feedback, null);
                builder.setView(view_feedback);
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        EditText content = (EditText) view_feedback.findViewById(R.id.content);
                        if (TextUtils.isEmpty(content.getText())) {
                            Snackbar.make(view, getString(R.string.in_feedback_content), Snackbar.LENGTH_SHORT).show();
                        } else {
                            Feedback post = new Feedback();
                            post.setContent(content.getText().toString());
                            //添加一对一关联
                            post.setUser(user);
                            post.save(new SaveListener<String>() {

                                @Override
                                public void done(String objectId, BmobException e) {
                                    if (e == null) {
                                        dialog.dismiss();
                                        Snackbar.make(view, getString(R.string.success_feedback), Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        dialog.dismiss();
                                        Snackbar.make(view, getString(R.string.error_network), Snackbar.LENGTH_SHORT).show();
                                    }
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
                        new AlertDialog.Builder(SettingActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setView(R.layout.view_about);
                builder.show();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出登录
                BmobUser.logOut();   //清除缓存用户对象
                Intent intent = new Intent();
                intent.setAction("ExitApp");
                sendBroadcast(intent);
                //跳转至登录页
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    PicUtils.cropPhoto(data.getData(), this);//裁剪图片
                }
                break;
            case 3:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");
                    if (head != null) {
                        /**
                         * 上传服务器代码
                         */
                        final BmobFile bmobFile = new BmobFile(PicUtils.saveBitmap2file(head, user));
                        bmobFile.uploadblock(new UploadFileListener() {

                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    //记得更新对应user的头像
                                    User newUser = new User();
                                    newUser.setHead(bmobFile);
                                    newUser.update(user.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                //用ImageView显示出来
                                                head_imageView.setImageBitmap(head);
                                            } else {
                                                Snackbar.make(view, getString(R.string.error_head_replace), Snackbar.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Snackbar.make(view, getString(R.string.error_head_replace), Snackbar.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onProgress(Integer value) {
                                // 返回的上传进度（百分比）
                            }
                        });
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
