package com.huirong.ui.appsfrg;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huirong.R;
import com.huirong.adapter.MissionListAdapter;
import com.huirong.base.BaseActivity;
import com.huirong.common.MyException;
import com.huirong.dialog.Loading;
import com.huirong.helper.UserHelper;
import com.huirong.inject.ViewInject;
import com.huirong.model.mission.MissionListModel;
import com.huirong.ui.appsfrg.childmodel.mission.AddMissionActivity;
import com.huirong.ui.appsfrg.childmodel.mission.MissionReceiveDetailActivity;
import com.huirong.ui.appsfrg.childmodel.mission.MissionSendDetailActivity;
import com.huirong.utils.LogUtils;
import com.huirong.utils.PageUtil;
import com.huirong.widget.NiceSpinner;
import com.huirong.widget.RefreshAndLoadListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.huirong.R.id.tv_title;


/**
 * 应用 任务
 * Created by sjy on 2016/12/2.
 */

public class MissionListActivity extends BaseActivity implements RefreshAndLoadListView.IReflashListener, RefreshAndLoadListView.ILoadMoreListener {

    //back
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout layout_back;

    //
    @ViewInject(id = tv_title)
    NiceSpinner niceSpinner;

    //
    @ViewInject(id = R.id.tv_right, click = "forAddMission")
    TextView tv_right;

    //list
    @ViewInject(id = R.id.myapprovalList)
    RefreshAndLoadListView myListView;

    private MissionListAdapter vAdapter;//记录适配

    private boolean ifLoading = false;//标记
    private boolean isAdd = false;//标记

    private int pageSize = 20;//spinner监听中修改该状态
    private String seeType = "1";//1 --》我派发的 2--》我负责的
    private String IMaxtime = null;
    private String IMinTime = null;
    //常量
    private static final int GET_MORE_DATA = -38;//上拉加载
    private static final int GET_NEW_DATA = -37;//
    private static final int GET_REFRESH_DATA = -36;//
    private static final int GET_NONE_NEWDATA = -35;//没有新数据
    private static final int REFRESH_NONE_NEWDATA = -34;//没有新数据
    private static final int LOAD_NONE_NEWDATA = -33;//没有新数据

    //spinner
    private List<String> spinnerData;
    private String myLastSelectState;//记录spinner上次选中的值
    private ArrayList<MissionListModel> listREC;//每次获取的收到的数据段
    private ArrayList<MissionListModel> listSED;//每次获取的发送的数据段

    private ArrayList<MissionListModel> listRECALL = new ArrayList<>();//记录收到的总数据
    private ArrayList<MissionListModel> listSEDALL = new ArrayList<>();//记录发送的总数据


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_apps_workplan_list);
        initMyView();
        initListener();
        getData();
    }

    //初始化
    private void initMyView() {
        tv_right.setText("添加任务");
        myListView.setIRefreshListener(this);//下拉刷新监听
        myListView.setILoadMoreListener(this);//加载监听
        vAdapter = new MissionListAdapter(this);// 上拉加载
        myListView.setAdapter(vAdapter);

        //spinner绑定数据
        spinnerData = new LinkedList<>(Arrays.asList("我派发的", "我负责的"));
        myLastSelectState = spinnerData.get(0);//默认为 我派发的
        niceSpinner.attachDataSource(spinnerData);//绑定数据
    }

    //监听
    private void initListener() {

        //spinner监听，筛选数据
        niceSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.d("任务", "spinner监听--" + spinnerData.get(position));

                //清空adapter的list长度 isHanging状态改变

                //如果选择状态没变，就不做处理，同时修改 sendType值
                if (!spinnerData.get(position).equals(myLastSelectState)) {
                    if (spinnerData.get(position).contains("我派发的")) {
                        seeType = "1";//我派发的
                        myLastSelectState = spinnerData.get(position);
                        tv_right.setText("添加任务");
                        LogUtils.d("任务", myLastSelectState + "--" + seeType);
                    } else {
                        seeType = "2";//我负责的
                        myLastSelectState = spinnerData.get(position);
                        tv_right.setText("");
                        LogUtils.d("任务", myLastSelectState + "--" + seeType);
                    }
                    getData();
                    //                    showSelectData(spinnerData.get(position).trim(), GET_NEW_DATA);//参数2必填GET_NEW_DATA
                } else {
                    return;
                }
            }
        });


        //		 点击一条记录后，跳转到登记时详细的信息
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int headerViewsCount = myListView.getHeaderViewsCount();//得到header的总数量
                int newPosition = position - headerViewsCount;//得到新的修正后的position

                MissionListModel MissionListModel = (MissionListModel) vAdapter.getItem(newPosition);//

                Bundle bundle = new Bundle();
                bundle.putSerializable("MissionListModel", MissionListModel);

                if (seeType.contains("1")) {
                    startActivity(MissionSendDetailActivity.class, bundle);//跳我派发界面
                    isAdd = true;
                } else {
                    startActivity(MissionReceiveDetailActivity.class, bundle);//我负责界面
                    isAdd = true;
                }
            }
        });
    }

    //
    private void getData() {

        Loading.run(MissionListActivity.this, new Runnable() {
            @Override
            public void run() {
                ifLoading = true;//
                try {
                    List<MissionListModel> visitorModelList = UserHelper.GetMissionList(
                            MissionListActivity.this,
                            "",//iMaxTime
                            "",
                            seeType);

                    if (visitorModelList == null || visitorModelList.size() < pageSize) {
                        vAdapter.IsEnd = true;
                    }

                    sendMessage(GET_NEW_DATA, visitorModelList);
                } catch (MyException e) {
                    LogUtils.e("异常：", e.toString());
                    sendMessage(GET_NONE_NEWDATA, e.getMessage());
                }
            }
        });
    }

    //RefreshListView.IReflashListener接口 下拉刷新
    @Override
    public void onRefresh() {
        if (IMaxtime == "") {
            sendMessage(GET_NONE_NEWDATA, "无最新数据");
            return;
        }
        Loading.noDialogRun(MissionListActivity.this, new Runnable() {

            @Override
            public void run() {
                ifLoading = true;//
                try {
                    List<MissionListModel> visitorModelList = UserHelper.GetMissionList(
                            MissionListActivity.this,
                            IMaxtime,//iMaxTime
                            "",
                            seeType);

                    LogUtils.d("SJY", "onRefresh--IMaxtime=" + IMaxtime);
                    if (visitorModelList == null || visitorModelList.size() < pageSize) {
                        vAdapter.IsEnd = true;
                    }

                    sendMessage(GET_REFRESH_DATA, visitorModelList);

                } catch (MyException e) {
                    sendMessage(REFRESH_NONE_NEWDATA, e.getMessage());
                }
            }

        });
    }

    // 上拉加载
    @Override
    public void onLoadMore() {
        if (IMinTime == "") {
            sendMessage(GET_NONE_NEWDATA, "无最新数据");
            return;
        }
        Loading.noDialogRun(MissionListActivity.this, new Runnable() {

            @Override
            public void run() {

                ifLoading = true;//
                try {
                    List<MissionListModel> visitorModelList = UserHelper.GetMissionList(
                            MissionListActivity.this,
                            "",//iMaxTime
                            IMinTime,
                            seeType);

                    LogUtils.d("SJY", "loadMore--min=" + IMaxtime);
                    if (visitorModelList == null || visitorModelList.size() < pageSize) {
                        vAdapter.IsEnd = true;
                    }

                    sendMessage(GET_MORE_DATA, visitorModelList);

                } catch (MyException e) {
                    sendMessage(LOAD_NONE_NEWDATA, e.getMessage());
                }
            }
        });

    }

    @Override
    protected void handleMessage(Message msg) {
        ArrayList<MissionListModel> list;
        switch (msg.what) {
            case GET_NEW_DATA:
                list = (ArrayList<MissionListModel>) msg.obj;//获取数据

                //重新获取数据，需要清空数据
                listRECALL.clear();
                listSEDALL.clear();

                SplitListState(list, GET_NEW_DATA);//筛选数据状态
                showSelectData(myLastSelectState, GET_NEW_DATA);//根据spinner值和数据状态 确定显示数据

                setIMinTime(list);
                setIMaxTime(list);
                LogUtils.d("SJY", "获取数据");
                ifLoading = false;
                break;


            case GET_REFRESH_DATA://刷新
                LogUtils.d("SJY", "刷新数据");
                list = (ArrayList<MissionListModel>) msg.obj;
                SplitListState(list, GET_REFRESH_DATA);//筛选数据状态
                showSelectData(myLastSelectState, GET_REFRESH_DATA);//根据spinner值和数据状态 确定显示数据

                setIMaxTime(list);
                ifLoading = false;
                break;


            case GET_MORE_DATA://加载
                LogUtils.d("SJY", "加载数据");
                list = (ArrayList<MissionListModel>) msg.obj;
                SplitListState(list, GET_MORE_DATA);//筛选数据状态
                showSelectData(myLastSelectState, GET_MORE_DATA);//根据spinner值和数据状态 确定显示数据

                setIMinTime(list);
                ifLoading = false;
                break;


            case GET_NONE_NEWDATA://没有获取新数据
                LogUtils.d("SJY", "无最新数据");
                sendToastMessage((String) msg.obj);
                vAdapter = new MissionListAdapter(this);// 上拉加载
                myListView.setAdapter(vAdapter);
                ifLoading = false;

                myListView.loadAndFreshComplete();//停止footerView动作
                break;

            case REFRESH_NONE_NEWDATA://没有获取新数据
                LogUtils.d("SJY", "无最新数据");
                sendToastMessage((String) msg.obj);

                ifLoading = false;

                myListView.loadAndFreshComplete();//停止footerView动作
                break;

            case LOAD_NONE_NEWDATA://没有获取新数据
                LogUtils.d("SJY", "无最新数据");
                sendToastMessage((String) msg.obj);

                ifLoading = false;

                myListView.loadAndFreshComplete();//停止footerView动作
                break;

            default:
                break;
        }
        super.handleMessage(msg);
    }

    public void setIMaxTime(ArrayList<MissionListModel> list) {
        IMaxtime = list.get(0).getCreateDate();
    }

    public void setIMinTime(ArrayList<MissionListModel> list) {
        IMinTime = list.get(list.size() - 1).getCreateDate();
    }


    /**
     * 筛选spinner状态下数据，并记录
     *
     * @param list  上拉下拉获取的数据记录 20条
     * @param STATE 具体上拉 下拉 获取 三个状态
     */
    private void SplitListState(List<MissionListModel> list, final int STATE) {
        if (list.size() <= 0) {
            return;
        }
        //每次来新数据，重新赋值spinner子状态
        listREC = new ArrayList<>();
        listSED = new ArrayList<>();

        /**
         * 状态
         * 逻辑：seeType
         *
         */
        if (seeType.contains("1")) {//我派发的
            listSED.addAll(list);
        } else if (seeType.contains("2")) {//我负责的
            listREC.addAll(list);
        }

        switch (STATE) {
            case GET_NEW_DATA:
                LogUtils.d("SJY", "GET_NEW_DATA筛选");
                //数据正常拼接
                //总数据拼接
                listRECALL.addAll(listREC);
                listSEDALL.addAll(listSED);

                break;


            case GET_REFRESH_DATA:
                LogUtils.d("SJY", "GET_REFRESH_DATA筛选");

                //数据插入 (已做拼接处理),使用：当切换spinner时，刷新了n个长度的数据可以直接显示
                //但是 spinner子状态下如何拼接数据？还有一种方式：每次刷新 清空子状态数据重新赋值？

                if (listREC.size() > 0) {
                    listRECALL.addAll(0, listREC);
                }

                if (listSED.size() > 0) {
                    listSEDALL.addAll(0, listSED);
                }

                break;


            case GET_MORE_DATA:
                LogUtils.d("SJY", "GET_MORE_DATA筛选");
                //总数据拼接
                listRECALL.addAll(listREC);
                listSEDALL.addAll(listSED);
                break;

            default:
                break;
        }

    }

    /**
     * 数据展示
     *
     * @param spinnerState spinner状态 很重要
     * @param STATE        上拉下拉状态
     */

    private void showSelectData(String spinnerState, final int STATE) {

        myLastSelectState = spinnerState;//记录spinner修改状态
        switch (spinnerState) {

            case "我负责的":

                if (STATE == GET_NEW_DATA) {
                    vAdapter.setEntityList(listRECALL);

                } else if (STATE == GET_REFRESH_DATA) {
                    vAdapter.insertEntityList(listREC);
                    myListView.loadAndFreshComplete();

                } else if (STATE == GET_MORE_DATA) {
                    vAdapter.addEntityList(listREC);
                    myListView.loadAndFreshComplete();

                } else if (STATE == GET_NONE_NEWDATA) {

                }

                break;

            case "我派发的":

                if (STATE == GET_NEW_DATA) {
                    vAdapter.setEntityList(listSEDALL);

                } else if (STATE == GET_REFRESH_DATA) {
                    vAdapter.insertEntityList(listSED);
                    myListView.loadAndFreshComplete();

                } else if (STATE == GET_MORE_DATA) {
                    vAdapter.addEntityList(listSED);
                    myListView.loadAndFreshComplete();

                } else if (STATE == GET_NONE_NEWDATA) {

                }

                break;

            default:
                PageUtil.DisplayToast("Spinne名称出错了！");
                break;

        }
    }

    /**
     * 添加工作计划
     *
     * @param view
     */
    public void forAddMission(View view) {
        startActivity(AddMissionActivity.class);
        isAdd = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAdd) {
            isAdd = false;
            getData();
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
