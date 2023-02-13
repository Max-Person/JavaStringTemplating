package com.github.max_person.templating;

import com.github.drapostolos.typeparser.TypeParser;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class StrSubstitutionCore {
    private final Object wrapper;
    private final TypeParser parser;

    public StrSubstitutionCore(Object wrapper) {
        this(wrapper, TypeParser.newBuilder().build());
    }
    
    public StrSubstitutionCore(Object wrapper, TypeParser parser){
        if(wrapper instanceof StrSubstitutionCore){
            throw new IllegalArgumentException("StrSubstitutionCore is not to be wrapped in another StrSubstitutionCore");
        }
        this.wrapper = wrapper;
        this.parser = parser;
    }

    private Map<String, Object> strSubFields(){
        Map<String, Object> strSubFields = new HashMap<>();

        //Получить объекты-поля класса
        Field[] fields = wrapper.getClass().getFields();
        for(Field f: fields){
            f.setAccessible(true);
            StrSubField a = f.getAnnotation(StrSubField.class);
            if(a!=null){ // && f.get(wrapper) instanceof StrSubstitution
                String name = f.getName();
                if(!a.value().isEmpty()){
                    name = a.value();
                }
                if(!Pattern.compile(StrSubMethodReplacer.genericMemberNameRegex()).matcher(name).matches()){
                    throw new IllegalArgumentException("Invalid StrSubField name/alias: " + name);
                }
                if(strSubFields.containsKey(name)){
                    throw new IllegalArgumentException("StrSubField name/alias clashes with an already existing one: " + name);
                }
                try{
                    strSubFields.put(name, f.get(wrapper));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        //Получить объекты из геттеров внутри класса
        Method[] methods = wrapper.getClass().getMethods();
        for(Method m: methods){
            StrSubGetter a = m.getAnnotation(StrSubGetter.class);
            if(a != null && m.getParameterCount() == 0){
                String name = m.getName();
                if(!a.value().isEmpty()){
                    name = a.value();
                }
                if(!Pattern.compile(StrSubMethodReplacer.genericMemberNameRegex()).matcher(name).matches()){
                    throw new IllegalArgumentException("Invalid StrSubGetter name/alias: " + name);
                }
                if(strSubFields.containsKey(name)){
                    throw new IllegalArgumentException("StrSubMethod name/alias clashes with an already existing one: " + name);
                }
                try {
                    strSubFields.put(name, m.invoke(wrapper));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        return strSubFields;

    }

    //Рассматриваются только публичные методы, поля и геттеры
    private Map<String, StrSubMethodReplacer> knownSubstitutionMethods(){
        Map<String, StrSubMethodReplacer> strSubs = new HashMap<>();

        //Получить собственные методы подстановки
        Method[] methods = wrapper.getClass().getMethods();
        for(Method m: methods){
            if(StrSubMethodReplacer.canBeConstructed(wrapper.getClass(), m)){
                StrSubMethod a = m.getAnnotation(StrSubMethod.class);
                String name = m.getName();
                if(!a.value().isEmpty()){
                    name = a.value();
                }
                if(!Pattern.compile(StrSubMethodReplacer.genericMemberNameRegex()).matcher(name).matches()){
                    throw new IllegalArgumentException("Invalid StrSubMethod name/alias: " + name);
                }
                if(strSubs.containsKey(name)){
                    throw new IllegalArgumentException("StrSubMethod name/alias clashes with an already existing one: " + name);
                }
                strSubs.put(name, new StrSubMethodReplacer(m, wrapper, name));
            }
        }

        //Получить методы постановки для полей
        for(Map.Entry<String, Object> f: strSubFields().entrySet()){
            StrSubstitutionCore fieldSubCore = new StrSubstitutionCore(f.getValue());
            Map<String, StrSubMethodReplacer> fieldMethods = fieldSubCore.knownSubstitutionMethods();

            for(Map.Entry<String, StrSubMethodReplacer> method : fieldMethods.entrySet()){
                StrSubMethodReplacer childMethod = method.getValue().asChild(f.getKey());
                strSubs.put(childMethod.getName(), childMethod);
            }
        }
        return strSubs;
    }

    public String process(String parametrized){
        if(knownSubstitutionMethods().isEmpty()){
            return parametrized;
        }

        String str = parametrized;
        //System.out.println(str);

        StrSubMethodCall foundCall = StrSubMethodCall.findFunctionCall(str);
        Map<String, StrSubMethodReplacer> subMethods = knownSubstitutionMethods();
        while (foundCall != null){
            boolean invalid = true;
            if(subMethods.containsKey(foundCall.functionName())){
                StrSubMethodReplacer method = subMethods.get(foundCall.functionName());
                if(method.isAppropriate(foundCall, parser)){
                    str = foundCall.replace(method.execute(foundCall, parser));
                }
                else {
                    str = foundCall.replace("[Invalid method call: "+ foundCall.functionName() + "(" + foundCall.argsSection() + ")]");
                }
            }
            else {
                str = foundCall.replace("[Unknown method call: "+ foundCall.functionName() + "(" + foundCall.argsSection() + ")]");
            }
            //TODO А что делать с невалидными? Если не трогать то создают бесконечный цикл
            foundCall =  StrSubMethodCall.findFunctionCall(str);
        }

        return str;
    }


}
