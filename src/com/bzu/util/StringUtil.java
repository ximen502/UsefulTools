package com.bzu.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xsc on 2019/6/5.
 */
public class StringUtil {

    private StringUtil(){}

    private static class SUGet {
        private static StringUtil instance = new StringUtil();
    }

    public static StringUtil getInstance(){
        return SUGet.instance;
    }

    /**
     * 在一个字符串中根据指定的正则表达式匹配字符串，将匹配到的字符串放入一个List中。
     * @param input 字符串
     * @param regex 正则表达式
     * @return 匹配到的字符串
     */
    public List<String> findStr(String input, String regex) {
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            //System.out.print("Start index: " + matcher.start());
            //System.out.print(" End index: " + matcher.end() + " ");
            String group = matcher.group();
            //System.out.println(group);
            list.add(group);
            //System.out.println(matcher.group(1).replace("'", "\""));
        }

        return list;
    }

    /**
     * 将一个长字符串，按固定的长度拆分为若干个短字符串并换行
     * @param str 长字符串
     * @param charCount 固定的长度(小于等于长字符串)
     */
    public void splitStr(String str, int charCount) {
        StringBuilder sb = new StringBuilder();
        int start = 0;
        int len = charCount <= 0 ? 50 : charCount;
        for (int i = 0; i < str.length(); i++) {
            if (str.length() < len) {
                System.out.println(str.substring(start));
                break;
            } else {
                if (i % len == 0) {
                    String frag = str.substring(start, i);
                    sb.append(frag).append("\n");
                    start = i;
                    System.out.println(frag);
                    if (str.length() - start < len) {
                        String lastFrag = str.substring(start);
                        System.out.println(lastFrag);
                        sb.append(lastFrag);
                    }
                }
            }
        }
        System.out.println(sb.toString());
    }
}
