package com.github.max_person.templating;

import com.github.drapostolos.typeparser.TypeParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrSubMethodReplacer{
    private final Method method;
    private final Object invoker;
    private final String name;

    public StrSubMethodReplacer(Method method, Object invoker) {
        this(method, invoker, method.getName());
    }

    public StrSubMethodReplacer(Method method, Object invoker, String name) {
        if(!canBeConstructed(invoker.getClass(), method)){
            throw new IllegalArgumentException("StrSubMethodReplacer cannot be constructed: either passed method isn't public, isn't annotated with @StrSubMethod or doesn't belong to the passed object's class");
        }
        this.method = method;
        this.invoker = invoker;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static boolean canBeConstructed(Class c, Method m){
        return Arrays.asList(c.getMethods()).contains(m) &&
                m.getAnnotation(StrSubMethod.class)!=null &&
                m.getGenericReturnType().equals(String.class);
    }

    boolean isAppropriate(StrSubMethodCall call, TypeParser argParser){
        if(!call.functionName().equals(this.name)){
            return false;
        }

        String args = call.argsSection();
        Matcher m = Pattern.compile(argsRegex()).matcher(args);
        if(!m.matches()){
            return false;
        }

        Type[] paramTypes = method.getParameterTypes();
        for(int i = 0; i < method.getParameterCount(); i++){
            String arg = m.group("arg" + i);
            try{
                argParser.parseType(arg, paramTypes[i]);
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }
    
    public String execute(StrSubMethodCall call, TypeParser argParser){
        if(isAppropriate(call, argParser)){
            String args = call.argsSection();
            Matcher m = Pattern.compile(argsRegex()).matcher(args);
            m.matches();
            Object[] params = new Object[method.getParameterCount()];
            Type[] paramTypes = method.getParameterTypes();
            for(int i = 0; i < method.getParameterCount(); i++){
                String arg = m.group("arg" + i);
                params[i] = argParser.parseType(arg, paramTypes[i]);
            }

            method.setAccessible(true);
            try {
                return (String) method.invoke(invoker, params);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public StrSubMethodReplacer asChild(String childName){
        return new StrSubMethodReplacer(this.method, this.invoker, childName + "." + this.name);
    }

    //-----------Regex construction--------

    static final String argSectionGroupName = "args";
    static final String nameSectionGroupName = "name";

    static String genericRegex(){
        return genericStartRegex() + genericArgsRegex() + genericEndRegex();
    }

    static String genericMemberNameRegex(){
        return "[a-zA-Z_$]\\w*";
    }

    static String genericNameRegex(){
        return "(?<" + nameSectionGroupName + ">(?:"+ genericMemberNameRegex() + "\\.)*" + genericMemberNameRegex() + ")";
    }

    static String genericStartRegex(){
        return "<" + genericNameRegex() + "\\(";
    }

    static String genericArgsRegex(){
        return "(?<" + argSectionGroupName +">.*?)";
    }

    static String genericEndRegex(){
        return "\\)>";
    }

    public String fullRegex(){
        return "<" + name + "\\(" + argsRegex() + "\\)>";
    }

    public String argsRegex(){
        String regex = "";
        String sep = ",\\s*";
        if(method.getParameterCount()>0)
        {
            for(int i = 0; i < method.getParameterCount(); i++){
                regex += "'(?<arg" + i + ">.*?)'";
                regex += sep;
            }
            regex = regex.substring(0, regex.length() - sep.length());
        }
        return regex;
    }

    public Matcher matcher(String str){
        Pattern p = Pattern.compile(this.fullRegex());
        return p.matcher(str);
    }
}
