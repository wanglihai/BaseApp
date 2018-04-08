package com.znxk.charge.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by w on 2018/3/29.
 */

public class UtilTypeFace {
    public static Typeface typeface;
    /**
     * 设置字体
     */
    public static void setTyprFace(final AppCompatActivity appCompatActivity) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(appCompatActivity.getAssets(), "fonts/hwxk.ttf");
        }
        LayoutInflaterCompat.setFactory(LayoutInflater.from(appCompatActivity), new LayoutInflaterFactory() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                AppCompatDelegate delegate = appCompatActivity.getDelegate();
                View view = delegate.createView(parent, name, context, attrs);
                if ( view!= null && (view instanceof TextView)) {
                    ((TextView) view).setTypeface(typeface);
                }
                if(view!=null && (view instanceof EditText)){
                    ((EditText) view).setTypeface(typeface);
                }
                if(view!=null && (view instanceof Button)){
                    ((Button) view).setTypeface(typeface);
                }
                if(view!=null && (view instanceof CheckBox)){
                    ((CheckBox) view).setTypeface(typeface);
                }
                return view;
            }
        });
    }
}
