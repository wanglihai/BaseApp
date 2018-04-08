package com.znxk.charge.ui.views;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.znxk.charge.R;
import com.znxk.charge.interfaces.DialogOnKeyDownListener;
import com.znxk.charge.utils.DensityUtils;


/**
 * Created by w on 2017/10/25.
 */

public class PorgressDialog extends Dialog {

    /*点击back键时的回调监听接口*/
    private DialogOnKeyDownListener dialogOnKeyDownListener;

    public void setDialogOnKeyDownListener(DialogOnKeyDownListener dialogOnKeyDownListener) {
        this.dialogOnKeyDownListener=dialogOnKeyDownListener;
    }

    public PorgressDialog(@NonNull Context context) {
        super(context);
    }

    public PorgressDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        try {
            View localView = LayoutInflater.from(context).inflate(R.layout.dialog_do_net, null);
            setContentView(localView);
            getWindow().setGravity(Gravity.CENTER);
            // 设置全屏
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.width = DensityUtils.dip2px(context, DensityUtils.px2dip(context, displayMetrics.widthPixels - 100)); // 设置宽度
            getWindow().setAttributes(lp);
            setCanceledOnTouchOutside(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected PorgressDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }



    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            dialogOnKeyDownListener.onKeyDownListener(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

}
