package com.bzu.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xsc on 2019/6/5.
 */
public class StringUtil {
    /**
     * 在一个字符串中根据指定的正则表达式匹配字符串，将匹配到的字符串放入一个List中。
     * @param input 字符串
     * @param regex 正则表达式
     * @return 匹配到的字符串
     */
    private List<String> findStr(String input, String regex) {
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
}
