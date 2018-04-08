package com.znxk.charge.utils;

/**
 * Created by sj on 2015/11/16.
 */
public class UtilStringEmpty {

    /*判断String长度 是否为空*/
    public static  boolean isEmpty(String...strings){
        if (strings==null||strings.length==0  ){
            return  true;
        }
        for (String s:strings){
            if (s==null || s.trim().length()==0){
                return true;
            }
        }
        return  false;
    }
}
