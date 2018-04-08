package com.znxk.charge.ui.activity;

import android.widget.ImageView;
import android.widget.TextView;

import com.znxk.charge.R;
import com.znxk.charge.base.BaseActivity;
import com.znxk.charge.ui.Fragment.ChargeFragment;
import com.znxk.charge.ui.Fragment.FunctionFragment;
import com.znxk.charge.ui.Fragment.PersonalCenterFragment;
import com.znxk.charge.ui.views.tabview.TabView;
import com.znxk.charge.ui.views.tabview.TabViewChild;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends BaseActivity {
    private TabView mHomeTabView;

    @Override
    public int setRootView() {
        return R.layout.activity_home;
    }

    @Override
    public void initViews() {
        mHomeTabView = (TabView) findViewById(R.id.home_tabView);
    }

    @Override
    public void initDatas() {
        List<TabViewChild> tabViewChildList=new ArrayList<>();
        TabViewChild tabViewChild01=new TabViewChild(R.drawable.tab02_unsel,R.drawable.tab02_unsel,"功能",  new FunctionFragment());
        TabViewChild tabViewChild02=new TabViewChild(R.drawable.tab01_unsel,R.drawable.tab01_unsel,"收费",  new ChargeFragment());
        TabViewChild tabViewChild03=new TabViewChild(R.drawable.tab03_unsel,R.drawable.tab03_unsel,"个人中心",  new PersonalCenterFragment());
        tabViewChildList.add(tabViewChild01);
        tabViewChildList.add(tabViewChild02);
        tabViewChildList.add(tabViewChild03);
        //end add data
        mHomeTabView.setTabViewDefaultPosition(0);

        mHomeTabView.setTabViewChild(tabViewChildList,getSupportFragmentManager());
        mHomeTabView.setOnTabChildClickListener(new TabView.OnTabChildClickListener() {
            @Override
            public void onTabChildClick(int  position, ImageView currentImageIcon, TextView currentTextView) {
                // Toast.makeText(getApplicationContext(),"position:"+position,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void initListener() {

    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }
}
