package com.huirong.ui.appsfrg.childmodel.mission;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huirong.R;
import com.huirong.base.BaseActivity;
import com.huirong.common.MyException;
import com.huirong.dialog.Loading;
import com.huirong.helper.UserHelper;
import com.huirong.inject.ViewInject;
import com.huirong.model.mission.MissionListModel;
import com.huirong.utils.LogUtils;

/**
 * 工作计划详情 发出
 * Created by sjy on 2017/2/25
 */

public class MissionSendDetailActivity extends BaseActivity {
    //back
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout layout_back;

    //
    @ViewInject(id = R.id.tv_title)
    TextView tv_title;

    //
    @ViewInject(id = R.id.tv_right)
    TextView tv_right;


    //标题
    @ViewInject(id = R.id.tv_msnTitle)
    TextView tv_msnTitle;

    //内容
    @ViewInject(id = R.id.tv_msnContent)//, click = "ContentExpended"
            TextView tv_msnContent;

    //任务类型
    @ViewInject(id = R.id.tv_msnType)
    TextView tv_msnType;

    //维修地点
    @ViewInject(id = R.id.tv_msnPlace)
    TextView tv_msnPlace;

    //维修维护人
    @ViewInject(id = R.id.tv_maintainMan)
    TextView tv_maintainMan;

    //联系方式
    @ViewInject(id = R.id.tv_repairWay)
    TextView tv_repairWay;

    //完成时间
    @ViewInject(id = R.id.tv_completeTime)
    TextView tv_completeTime;

    //备注
    @ViewInject(id = R.id.tv_remark, click = "RemarkExpended")
    TextView tv_remark;

    //实际完成时间
    @ViewInject(id = R.id.layout_completeTime2)
    LinearLayout layout_completeTime2;
    @ViewInject(id = R.id.tv_completeTime2)
    TextView tv_completeTime2;

    //完成说明
    @ViewInject(id = R.id.layout_remark2)
    LinearLayout layout_remark2;
    @ViewInject(id = R.id.tv_remark2, click = "Remark2Expended")
    TextView tv_remark2;


    private MissionListModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_apps_mission_send_detail);
        initMyView();
        setShow();
    }

    private void initMyView() {
        tv_title.setText(getResources().getString(R.string.workplan_title_send));
        tv_right.setText("");

        //获取跳转值
        Bundle bundle = getIntent().getExtras();
        model = (MissionListModel) bundle.getSerializable("MissionListModel");
        LogUtils.d("我派发的详情", model.toString());
        if (model.getShowState().contains("未阅读")) {

        }
        readThisNotice(model);

    }

    private void setShow() {
        if (model.getShowState().contains("已完成")) {
            layout_completeTime2.setVisibility(View.VISIBLE);
            tv_completeTime2.setText(model.getUpdetTime());

            layout_remark2.setVisibility(View.VISIBLE);
            tv_remark2.setText(model.getISremark());
        } else {
            layout_completeTime2.setVisibility(View.GONE);
            layout_remark2.setVisibility(View.GONE);
        }
        tv_msnTitle.setText(model.getMisssiontheme());
        tv_msnContent.setText(model.getMisssionContent());
        tv_msnType.setText(model.getMisssionType());
        tv_msnPlace.setText(model.getMisssionSite());
        tv_maintainMan.setText(model.getMaintainMan());
        tv_repairWay.setText(model.getContactWay());
        tv_completeTime.setText(model.getFinishtime());
        tv_remark.setText(model.getRemark());
    }


    /**
     * 调接口 未读-->已读
     *
     * @param model
     */
    private void readThisNotice(final MissionListModel model) {
        Loading.noDialogRun(this, new Runnable() {
            @Override
            public void run() {
                try {

                    UserHelper.postReadThisMission(MissionSendDetailActivity.this, model.getMaintainID());
                    LogUtils.d("已阅读", "成功");
                } catch (MyException e) {
                    e.printStackTrace();
                    LogUtils.d("已阅读", "失败异常=" + e.getMessage());
                }
            }
        });
    }


    /**
     * 备注显示扩展
     */
    private boolean isRemarkExpend = false;

    public void RemarkExpended(View view) {
        if (!isRemarkExpend) {
            tv_remark.setMinLines(0);
            tv_remark.setMaxLines(Integer.MAX_VALUE);
            isRemarkExpend = true;
        } else {
            tv_remark.setLines(3);
            isRemarkExpend = false;
        }

    }

    /**
     * 内容显示扩展
     */
    private boolean isContentExpend = false;

    public void ContentExpended(View view) {
        if (!isContentExpend) {
            tv_msnContent.setMinLines(0);
            tv_msnContent.setMaxLines(Integer.MAX_VALUE);
            isContentExpend = true;
        } else {
            //            tv_msnContent.setLines(20);
            tv_msnContent.setMaxLines(Integer.MAX_VALUE);
            isContentExpend = false;
        }

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
