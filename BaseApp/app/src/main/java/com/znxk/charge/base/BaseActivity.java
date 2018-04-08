package com.znxk.charge.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.znxk.charge.R;
import com.znxk.charge.constant.Constants;
import com.znxk.charge.interfaces.DialogOnKeyDownListener;
import com.znxk.charge.listeners.NoDoubleClickListener;
import com.znxk.charge.ui.views.PorgressDialog;
import com.znxk.charge.utils.DensityUtils;
import com.znxk.charge.utils.ToolUtils;
import com.znxk.charge.utils.UtilStringEmpty;


/**
 * Created by w on 2017/11/16.
 */

public abstract class BaseActivity extends AppCompatActivity {
    /** 是否允许全屏 **/
    private boolean mAllowFullScreen = false;
    /** 是否沉浸状态栏 **/
    private boolean isSetStatusBar =false;
    /**
     * 退出时之前的界面进入动画,可在finish();前通过改变它的值来改变动画效果
     */
    protected int enterAnim = R.anim.fade;
    /**
     * 退出时该界面动画,可在finish();前通过改变它的值来改变动画效果
     */
    protected int exitAnim = R.anim.right_push_out;
    /**
     * 该Activity实例，命名为mActivitySelf是因为大部分方法都只需要mActivitySelf，写成mActivitySelf使用更方便
     * @warn 不能在子类中创建
     */
    protected BaseActivity mActivitySelf = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        UtilTypeFace.setTyprFace(this);/*设置字体*/
        super.onCreate(savedInstanceState);
        //判断是否允许全屏
        if (mAllowFullScreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        //是否沉浸式标题栏
        if (isSetStatusBar) {
            steepStatusBar();
        }
        ActivityControl.add(this);
        mActivitySelf = this;
        isAlive=true;
        setContentView(setRootView());
        initViews();
        initDatas();
        initListener();
    }


    /**
     * 隐藏软件盘
     */
    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * 点击软键盘之外的空白处，隐藏软件盘
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (ToolUtils.isShouldHideKeyboard(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(mActivitySelf.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    /**
     * 显示软键盘
     */
    public void showInputMethod(){
        if (getCurrentFocus() != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInputFromInputMethod(getCurrentFocus().getWindowToken(),0);
        }
    }

    /**
     * 是否允许全屏开放一个方法供子类调用
     * @param allowFullScreen
     */
    public void setAllowFullScreen(boolean allowFullScreen) {
        this.mAllowFullScreen = allowFullScreen;
    }

    /**
     * 设置沉浸式标题栏
     */
    private void steepStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 是否设置沉浸状态栏，开放一个方法供子类调用
     * @param isSetStatusBar
     */
    public void setSteepStatusBar(boolean isSetStatusBar) {
        this.isSetStatusBar = isSetStatusBar;
    }

    /**在UI线程中运行，建议用这个方法代替runOnUiThread
     * @param action
     */
    public final void runUiThread(Runnable action) {
        if (isAlive() == false) {
            return;
        }
        runOnUiThread(action);
    }

    private boolean isAlive = false;


    public final boolean isAlive() {
        return isAlive && mActivitySelf != null;// & ! isFinishing();导致finish，onDestroy内runUiThread不可用
    }

    //添加fragment
    protected void addFragment(BaseFragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(getFragmentContentId(), fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    //移除fragment
    protected void removeFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    //返回键返回事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private PorgressDialog waitDialog;

    /**
     * 全局等待对话框
     */
    public void showWaitDialog(final DialogOnKeyDownListener dialogOnKeyDownListener) {
        runUiThread(new Runnable() {
            @Override
            public void run() {
                waitDialog=new PorgressDialog(mActivitySelf,R.style.custom_dialog);
                waitDialog.setDialogOnKeyDownListener(dialogOnKeyDownListener);
                waitDialog.show();
            }
        });
    }

    /**
     * 销毁进度弹框Dialog
     */
    public void dismissWaitDialog() {
        runUiThread(new Runnable() {
            @Override
            public void run() {
                if (waitDialog != null && waitDialog.isShowing()) {
                    waitDialog.dismiss();
                    waitDialog = null;
                }
            }
        });
    }




    private Dialog promptDialog;//提示弹框

    /**
     * 封装提示框
     * @param content
     */
    public void doShowSurePromptDialog(String content) {
        showPromptDialog(content, null, Constants.SURE, null, new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                dismissPromptDialog();
            }
        });
    }

    /**
     * 封装提示框
     * @param content
     */
    public void doShowPromptDialog(String content) {
        showPromptDialog(content, Constants.CANCEL, Constants.SURE, new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                dismissPromptDialog();
            }
        }, new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                dismissPromptDialog();
            }
        });
    }

    /**
     * 提示弹框
     * @param content
     * @param cancel
     * @param submit
     * @param tvCancelOnClickListener
     * @param tvSubmitlOnClickListener
     */
    public void showPromptDialog(final String content, final String cancel, final String submit, final NoDoubleClickListener tvCancelOnClickListener, final NoDoubleClickListener tvSubmitlOnClickListener){
        runUiThread(new Runnable() {
            @Override
            public void run() {
                View localView = LayoutInflater.from(mActivitySelf).inflate(R.layout.dialog_prompt, null);
                TextView tvSubmit = (TextView) localView.findViewById(R.id.tv_submit);
                TextView tvCancel = (TextView) localView.findViewById(R.id.tv_cancel);
                TextView tvContent = (TextView) localView.findViewById(R.id.tv_content);
                tvContent.setText(content);
                if (UtilStringEmpty.isEmpty(cancel)) {
                    tvCancel.setVisibility(View.GONE);
                }else {
                    tvCancel.setVisibility(View.VISIBLE);
                    tvCancel.setOnClickListener(tvCancelOnClickListener);
                    tvCancel.setText(cancel);
                }
                if (UtilStringEmpty.isEmpty(submit)) {
                    tvSubmit.setVisibility(View.GONE);
                }else {
                    tvSubmit.setVisibility(View.VISIBLE);
                    tvSubmit.setOnClickListener(tvSubmitlOnClickListener);
                    tvSubmit.setText(submit);
                }
                promptDialog = new Dialog(mActivitySelf, R.style.custom_dialog);
                promptDialog.setContentView(localView);
                promptDialog.getWindow().setGravity(Gravity.CENTER);
                promptDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                        if(keyCode== KeyEvent.KEYCODE_BACK){
                            if(promptDialog!=null&&promptDialog.isShowing()){
                                promptDialog.dismiss();
                                promptDialog=null;
                            }
                        }
                        return false;
                    }
                });
                // 设置全屏
                WindowManager windowManager = mActivitySelf.getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                WindowManager.LayoutParams lp = promptDialog.getWindow().getAttributes();
                lp.width = DensityUtils.dip2px(mActivitySelf, DensityUtils.px2dip(mActivitySelf, display.getWidth() - 100)); // 设置宽度
                promptDialog.getWindow().setAttributes(lp);
                promptDialog.setCanceledOnTouchOutside(false);
                promptDialog.show();
            }
        });
    }

    /**
     * 销毁提示弹框
     */
    public void dismissPromptDialog(){
        runUiThread(new Runnable() {
            @Override
            public void run() {
                if (promptDialog != null && promptDialog.isShowing()) {
                    promptDialog.dismiss();
                    promptDialog = null;
                }
            }
        });
    }


    /**打开新的Activity，向左滑入效果
     * @param intent
     */
    public void toActivity(Intent intent) {
        toActivity(intent, true);
    }
    /**打开新的Activity
     * @param intent
     * @param showAnimation
     */
    public void toActivity(Intent intent, boolean showAnimation) {
        toActivity(intent, -1, showAnimation);
    }
    /**打开新的Activity，向左滑入效果
     * @param intent
     * @param requestCode
     */
    public void toActivity(Intent intent, int requestCode) {
        toActivity(intent, requestCode, true);
    }

    /**打开新的Activity
     * @param intent
     * @param requestCode
     * @param showAnimation
     */
    public void toActivity(final Intent intent, final int requestCode, final boolean showAnimation) {
        runUiThread(new Runnable() {
            @Override
            public void run() {
                if (intent == null) {
                    return;
                }
                //fragment中使用mActivitySelf.startActivity会导致在fragment中不能正常接收onActivityResult
                if (requestCode < 0) {
                    startActivity(intent);
                } else {
                    startActivityForResult(intent, requestCode);
                }
                if (showAnimation) {
                    overridePendingTransition(R.anim.right_push_in, R.anim.hold);
                } else {
                    overridePendingTransition(R.anim.null_anim, R.anim.null_anim);
                }
            }
        });
    }

    //默认Activity关闭时是否伴随着动画
    private boolean isFinishActivityWithAnim=true;

    @Override
    public void finish() {
        super.finish();//必须写在最前才能显示自定义动画
        runUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishActivityWithAnim) {
                    if (enterAnim > 0 && exitAnim > 0) {
                        try {
                            overridePendingTransition(enterAnim, exitAnim);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /**
     * 设置Activity关闭时是否伴随着动画
     * @param isFinishWithAnim
     */
    public void setFinishActivityWithAnim(boolean isFinishWithAnim){
        isFinishActivityWithAnim=isFinishWithAnim;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityControl.remove(this);
        dismissPromptDialog();
        dismissWaitDialog();
//        waitDialog=null;
        isAlive=false;
//        promptDialog=null;
    }

    //标题栏操作
    public abstract int setRootView();
    //初始化View控件
    public abstract void initViews();
    //初始化数据
    public abstract void initDatas();
    public abstract void initListener();
//    /** View点击 **/
//    public abstract void widgetClick(View v);
    //    //布局文件ID
//    protected abstract int getContentViewId();
    //布局中Fragment的ID
    protected abstract int getFragmentContentId();
}
