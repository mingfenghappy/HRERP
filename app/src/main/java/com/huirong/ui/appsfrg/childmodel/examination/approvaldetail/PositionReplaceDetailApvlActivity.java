package com.huirong.ui.appsfrg.childmodel.examination.approvaldetail;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huirong.R;
import com.huirong.application.MyApplication;
import com.huirong.base.BaseActivity;
import com.huirong.common.MyException;
import com.huirong.dialog.Loading;
import com.huirong.helper.UserHelper;
import com.huirong.inject.ViewInject;
import com.huirong.model.MyApprovalModel;
import com.huirong.model.approvaldetailmodel.PositionReplaceApvlModel;
import com.huirong.utils.PageUtil;

/**
 * 审批 调动详情
 * Created by sjy on 2017/1/16.
 */

public class PositionReplaceDetailApvlActivity extends BaseActivity {
    //back
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout layout_back;

    //
    @ViewInject(id = R.id.tv_title)
    TextView tv_title;

    //
    @ViewInject(id = R.id.tv_right)
    TextView tv_right;

    //申请人
    @ViewInject(id = R.id.tv_ApprovalPerson)
    TextView tv_ApprovalPerson;

    //部门
    @ViewInject(id = R.id.tv_approvaldept)
    TextView tv_approvaldept;

    //公司
    @ViewInject(id = R.id.tv_approvalCo)
    TextView tv_approvalCo;

    //申请时间
    @ViewInject(id = R.id.tv_approvalTime)
    TextView tv_approvalTime;
    //未审批bottom
    @ViewInject(id = R.id.laytout_decide)
    LinearLayout laytout_decide;

    //驳回
    @ViewInject(id = R.id.btn_refulse, click = "forRefulse")
    Button btn_refulse;

    //批准
    @ViewInject(id = R.id.btn_commit, click = "toForCommit")
    Button btn_commit;

    //转交
    @ViewInject(id = R.id.btn_transfer, click = "forTransfer")
    Button btn_transfer;

    //审批bottom
    @ViewInject(id = R.id.laytout_copy)
    LinearLayout laytout_copy;

    //抄送
    @ViewInject(id = R.id.btn_copytp, click = "forCopyto")
    Button btn_copytp;

    //标题
    @ViewInject(id = R.id.tv_positionReplace_title)
    TextView tv_positionReplace_title;

    //调动日期
    @ViewInject(id = R.id.tv_positionReplace_date)
    TextView tv_positionReplace_date;

    //决定人
    @ViewInject(id = R.id.tv_positionReplace_decider)
    TextView tv_positionReplace_decider;

    //原部门
    @ViewInject(id = R.id.tv_positionReplace_orgDept)
    TextView tv_positionReplace_orgDept;

    //原岗位
    @ViewInject(id = R.id.tv_positionReplace_orgPosition)
    TextView tv_positionReplace_orgPosition;

    //新部门
    @ViewInject(id = R.id.tv_positionReplace_newDept)
    TextView tv_positionReplace_newDept;

    //新岗位
    @ViewInject(id = R.id.tv_positionReplace_newPosition)
    TextView tv_positionReplace_newPosition;
    //说明
    @ViewInject(id = R.id.tv_reason, click = "ReasonExpended")
    TextView tv_reason;

    //备注
    @ViewInject(id = R.id.tv_remark, click = "RemarkExpended")
    TextView tv_remark;
    private MyApprovalModel myApprovalModel;
    private PositionReplaceApvlModel model;

    //常量
    public static final int POST_SUCCESS = 21;
    public static final int POST_FAILED = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_apps_examination_positionreplace_d2);
        tv_title.setText(getResources().getString(R.string.positionReplace));
        tv_right.setText("");

        Bundle bundle = this.getIntent().getExtras();
        myApprovalModel = (MyApprovalModel) bundle.getSerializable("MyApprovalModel");
        Log.d("SJY", "详情MyApprovalModel");
        bottomType();
        //
        getDetailData();
        MyApplication.getInstance().addACT(this);
    }

    private void setShow() {
        //
        tv_ApprovalPerson.setText(model.getEmployeeName());
        tv_approvaldept.setText(model.getDepartmentName());
        tv_approvalCo.setText(model.getStoreName());
        tv_approvalTime.setText(model.getApplicationCreateTime());

        //
        tv_positionReplace_title.setText(model.getApplicationTitle());
        tv_positionReplace_date.setText(model.getTransferDate());
        tv_positionReplace_decider.setText(model.getHandlerEmployeeName());
        tv_positionReplace_orgDept.setText(model.getOriDepartmentName());
        tv_positionReplace_orgPosition.setText(model.getOriPostName());
        tv_positionReplace_newDept.setText(model.getNewDepartmentName());
        tv_positionReplace_newPosition.setText(model.getNewPostName());
        tv_reason.setText(model.getTransferReason());
        tv_remark.setText(model.getRemark());

    }

    private void bottomType() {
        //
        if (myApprovalModel.getApprovalStatus().contains("1")) {

            laytout_decide.setVisibility(View.GONE);
            laytout_copy.setVisibility(View.VISIBLE);

        } else {
            laytout_decide.setVisibility(View.VISIBLE);
            laytout_copy.setVisibility(View.GONE);
        }
    }

    private void getDetailData() {

        Loading.run(this, new Runnable() {
            @Override
            public void run() {
                //泛型
                try {
                    PositionReplaceApvlModel model1 = new UserHelper<>(PositionReplaceApvlModel.class)
                            .approvalDetailPost(PositionReplaceDetailApvlActivity.this,
                                    myApprovalModel.getApplicationID(),
                                    myApprovalModel.getApplicationType());
                    sendMessage(POST_SUCCESS, model1);
                } catch (MyException e) {
                    e.printStackTrace();
                    sendMessage(POST_FAILED, e.getMessage());
                }
            }
        });
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case POST_SUCCESS:
                model = (PositionReplaceApvlModel) msg.obj;
                setShow();
                break;
            case POST_FAILED:
                PageUtil.DisplayToast((String) msg.obj);
                break;
            default:
                break;
        }
    }

    //驳回
    public void forRefulse(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("MyApprovalModel", myApprovalModel);
        startActivity(CommonDisagreeActivity.class, bundle);
    }

    //同意
    public void toForCommit(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("MyApprovalModel", myApprovalModel);
        startActivity(CommonAgreeActivity.class, bundle);
    }

    //转交
    public void forTransfer(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("MyApprovalModel", myApprovalModel);
        startActivity(CommonTransfertoActivity.class, bundle);
    }

    // 抄送
    public void forCopyto(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("MyApprovalModel", myApprovalModel);
        startActivity(CommonCopytoCoActivity.class, bundle);
    }

    /**
     * back
     *
     * @param view
     */
    public void forBack(View view) {
        this.finish();
    }

    private boolean isExpend = false;

    public void ReasonExpended(View view) {
        if (!isExpend) {
            tv_reason.setMinLines(0);
            tv_reason.setMaxLines(Integer.MAX_VALUE);
            isExpend = true;
        } else {
            tv_reason.setLines(3);
            isExpend = false;
        }

    }

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
}
