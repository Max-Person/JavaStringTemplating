package com.github.max_person.templating;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrSubMethodCall {
    private final String string;
    private final int startIndex;
    private final int endIndex;

    private final Matcher m;

    public StrSubMethodCall(String string, int startIndex, int endIndex) {
        if(startIndex < 0 || startIndex > string.length() || endIndex < startIndex || endIndex > string.length()){
            throw new IndexOutOfBoundsException();
        }

        this.m = Pattern.compile(StrSubMethodReplacer.genericRegex()).matcher(string.substring(startIndex, endIndex));

        if(!m.matches()){
            throw new IllegalArgumentException();
        }

        this.string = string;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public String getString() {
        return string;
    }

    public int startIndex() {
        return startIndex;
    }

    public int endIndex() {
        return endIndex;
    }

    public String value(){
        return string.substring(startIndex, endIndex);
    }

    public String functionName(){
        return m.group("name");
    }

    public String argsSection(){
        return m.group("args");
    }

    public String replace(String newSubstring){
        StringBuilder b = new StringBuilder(string);
        b.replace(startIndex, endIndex, newSubstring);
        return b.toString();
    }

    static StrSubMethodCall findFunctionCall(String str){
        return findFunctionCall(str, 0);
    }

    static StrSubMethodCall findFunctionCall(String str, int start){
        if(start>=str.length()){
            return null;
        }

        Matcher start_m = Pattern.compile(StrSubMethodReplacer.genericStartRegex()).matcher(str);

        int foundStart = 0;

        if(start_m.find(start)){    //Если найдена открывающая последовательность
            foundStart = start_m.start();
            Matcher end_m = Pattern.compile(StrSubMethodReplacer.genericEndRegex()).matcher(str);

            if(end_m.find(foundStart)){     //Если после нее найдена закрывающая последовательность
                if(!start_m.find()){   //Если открывающих последовательностей больше нет, то вызов функции найден
                    return new StrSubMethodCall(str, foundStart, end_m.end());
                }
                else {  //Иначе, если есть еще открывающие последовательность..
                    if(start_m.start() > end_m.end()){ //..но они идут после найденной закрывающей, то вызов функции найден
                        return new StrSubMethodCall(str, foundStart, end_m.end());
                    }
                    else{   //..и они идут до найденной закрывающей
                        //То посчитать все открывающие последовательности идущие до найденной закрывающей
                        int count = 1;
                        start_m.region(start_m.end(), end_m.start());
                        while(start_m.find()){
                            count++;
                        }

                        //и отсчитать после найденной закрывающей последовательности еще столько же закрывающих.
                        //Последняя найденная и будет искомой. Если такой не окажется - вызов невалиден
                        end_m.region(end_m.end(), end_m.regionEnd());
                        while(count > 0){
                            if(!end_m.find()){
                                return null;
                            }
                            count--;
                        }
                        return new StrSubMethodCall(str, foundStart, end_m.end());
                    }
                }
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    @Override
    public String toString() {
        return this.value();
    }
}
