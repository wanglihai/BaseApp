package com.znxk.charge.base;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/10/21.
 */
public class ActivityControl {

    private static List<BaseActivity> mActivities = new ArrayList<>();

    public static void add(BaseActivity baseActivity) {
        try {
            mActivities.add(baseActivity);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void remove(BaseActivity baseActivity) {
        try {
            mActivities.remove(baseActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BaseActivity getActivity(Class activity) {
        try {
            for (BaseActivity baseActivity : mActivities) {
                if (baseActivity.getClass() == activity) {
                    return baseActivity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void killAll() {
        try {
            Iterator<BaseActivity> iterator = mActivities.iterator();
            while (iterator.hasNext()) {
                iterator.next().finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
