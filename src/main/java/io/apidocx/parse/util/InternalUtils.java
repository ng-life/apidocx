package io.apidocx.parse.util;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 内部工具类
 */
public class InternalUtils {


    private static final Gson gson = new Gson();
    static Pattern humpPattern = Pattern.compile("[A-Z]");

    static final String DASH = "-";

    public static <T> T clone(T t) {
        return gson.fromJson(gson.toJson(t), (Class<T>) t.getClass());
    }

    /**
     * 驼峰转化
     */
    public static String camelToLine(String camelCase, String split) {
        if (Strings.isNullOrEmpty(split)) {
            split = DASH;
        }
        Matcher matcher = humpPattern.matcher(camelCase);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, split + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        String result = sb.toString();
        if (result.startsWith(split)) {
            result = result.substring(split.length());
        }
        return result;
    }

}
