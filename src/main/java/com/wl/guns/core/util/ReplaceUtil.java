package com.wl.guns.core.util;

/**
 * 字符串替换特殊字符工具类
 * <p>
 * Created by zx
 * Date 2019/03/20 16:21
 */
public class ReplaceUtil {

    public static String replace(String value) {
        return value.replace("& #40;", "(").
                replace("& #41;", ")").
                replace("& #39;", "'").
                replace("& lt;","〈").
                replace("& gt;","〉");
    }

    public static String replaceNotice(String value) {
        return value.replaceAll("<o:p>", "")
                .replaceAll("</o:p>", "");
    }
}
