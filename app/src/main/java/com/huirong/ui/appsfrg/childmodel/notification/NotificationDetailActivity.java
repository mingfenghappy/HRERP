package com.huirong.ui.appsfrg.childmodel.notification;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huirong.R;
import com.huirong.base.BaseActivity;
import com.huirong.common.MyException;
import com.huirong.dialog.Loading;
import com.huirong.helper.UserHelper;
import com.huirong.inject.ViewInject;
import com.huirong.model.NotificationListModel;

/**
 * 通知详情
 * Created by sjy on 2017/2/25.
 */

public class NotificationDetailActivity extends BaseActivity {
    //back
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout layout_back;

    //
    @ViewInject(id = R.id.tv_title)
    TextView tv_title;

    //
    @ViewInject(id = R.id.tv_right)
    TextView tv_right;

    //通知标题
    @ViewInject(id = R.id.tv_notice_title)
    TextView tv_notice_title;

    //时间
    @ViewInject(id = R.id.tv_time)
    TextView tv_time;

    //发件人
    @ViewInject(id = R.id.tv_to)
    TextView tv_name;


    //内容
    @ViewInject(id = R.id.tv_content)
    TextView tv_content;


    private NotificationListModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_apps_notification_notice_detail_common);
        initMyView();
        setShow();
    }

    private void initMyView() {
        tv_title.setText(getResources().getString(R.string.msg_msgTitle));
        tv_right.setText("");
        //获取跳转值
        Bundle bundle = getIntent().getExtras();
        model = (NotificationListModel) bundle.getSerializable("NotificationListModel");
        readThisNotice(model);
    }

    private void setShow() {
        tv_notice_title.setText(model.getApplicationTitle());
        tv_time.setText(model.getCreateTime());
        tv_name.setText(model.getPublishDeptName()+"\t\t"+model.getEmployeeName());
        tv_content.setText(model.getAbstract());
    }

    //将状态标位 已读
    private void readThisNotice(final NotificationListModel model){
        Loading.noDialogRun(this, new Runnable() {
            @Override
            public void run() {
                try {
                    UserHelper.postReadThisNotification(NotificationDetailActivity.this
                            ,model.getApplicationID());
                    Log.d("SJY", "成功");
                } catch (MyException e) {
                    e.printStackTrace();
                    Log.d("SJY", "已读异常="+e.getMessage());
                }
            }
        });
    }
    /**
     * back
     *
     * @param view
     */
    public void forBack(View view) {
        this.finish();
    }
}
