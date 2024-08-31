package com.github.max_person.templating;

import com.github.drapostolos.typeparser.TypeParser;
import com.github.max_person.templating.expressions.TemplateExprParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A text template, containing all the necessary data on <i>how</i> to construct the necessary text.
 * <p>
 * A template is an arbitrary string containing any number of <i>interpolations</i>
 * which are denoted by the ${...} (or just $... in case of simple interpolations where only the identifier is used).
 * <p>
 * The expression language inside interpolations is provided by the {@link TemplateExprParser},
 * but can be redefined, if needed, using an arbitrary {@link TemplateInterpolationParser}
 * (see {@link Template#Template(String, TemplateInterpolationParser)}).
 * <p>
 * The substitution of interpolations within the template (<i>interpretation</i>) requires {@link InterpretationData}
 * to define the data used to evaluate expressions within the interpolations (see {@link Template#interpret}).
 */
public class Template implements TemplateSection{
    private final List<TemplateSection> sections;
    
    public Template(String str){
        this(str, new TemplateExprParser());
    }
    
    public Template(String str, TemplateInterpolationParser<?> interpolationParser){
        TemplatingLexer l = new TemplatingLexer(CharStreams.fromString(str));
        l.removeErrorListeners();
        TemplatingParser p = new TemplatingParser(new CommonTokenStream(l));
        p.removeErrorListeners();
        
        sections = createSections(p.template(), interpolationParser);
    }
    
    @Override
    public String interpret(InterpretationData data) {
        StringBuilder stringBuilder = new StringBuilder();
        for(TemplateSection section : sections){
            stringBuilder.append(section.interpret(data));
        }
        return stringBuilder.toString();
    }
    
    
    private record ModifiedInterpolationSection(
        TemplateSection interpolationSection,
        List<Modifier> modifiers
    ) implements TemplateSection {
        
        @Override
        public String interpret(InterpretationData data) {
            Object modifierObject = data.getModifierObject();
            String text = interpolationSection.interpret(data);
            for(Modifier modifier : modifiers){
                text = modifier.apply(text, modifierObject, data.getParser());
            }
            return text;
        }
    }
    
    private record Modifier(String name, List<Object> arguments) {
        public String apply(String string, Object owner, TypeParser typeParser){
            List<Object> passedArguments = new ArrayList<>();
            passedArguments.add(string);
            passedArguments.addAll(arguments);
            return ReflectionUtils.invokeMethodWithParsedArguments(
                owner,
                name,
                (object, methodName) -> ReflectionUtils.getAvailableMethods(
                    object,
                    TemplatingModifier.class,
                    (m, a) -> canBeModifier(m),
                    (m, a) -> a.value().isBlank() ? m.getName() : a.value()
                ).get(methodName),
                passedArguments,
                typeParser
            ).toString();
        }
        
        private boolean canBeModifier(Method method){
            return !method.getReturnType().equals(void.class)
                && method.getParameterCount() >= 1
                && method.getParameterTypes()[0].equals(String.class);
        }
    }
    
    private static class StringSection implements TemplateSection {
        public final String value;
        
        private StringSection(String value) {
            this.value = value;
        }
        
        @Override
        public String interpret(InterpretationData data) {
            return value;
        }
    }
    
    //---CONSTRUCTION---
    
    private static List<TemplateSection> createSections(
        TemplatingParser.TemplateContext ctx,
        TemplateInterpolationParser<?> interpolationParser
    ){
        List<TemplateSection> sections = new ArrayList<>();
        for(TemplatingParser.TemplateSectionContext sectionContext : ctx.templateSection()){
            TemplateSection section;
            if(sectionContext.STR()!= null){
                section = new Template.StringSection(sectionContext.getText());
            }
            else { //Interpolation
                String interpolText = sectionContext.interpolation().getText().substring(1); //Removing '$' from interpolation content
                boolean isSimpleInterpolation;
                if(sectionContext.interpolation().SIMPLE_INTERPOLATION() != null){ //Simple interpolation
                    isSimpleInterpolation = true;
                }
                else { //ordinary interpolation
                    interpolText = interpolText.substring(1, interpolText.length() - 1); //Removing surrounding { } from interpolation content
                    isSimpleInterpolation = false;
                }
                TemplateSection interpolationSection = interpolationParser.parse(
                    new TemplateInterpolationParser.InterpolationContent(interpolText, isSimpleInterpolation)
                );
                List<Modifier> modifiers = createModifiers(sectionContext.modifier());
                section = modifiers.isEmpty()
                    ? interpolationSection
                    : new Template.ModifiedInterpolationSection(interpolationSection, modifiers);
            }
            sections.add(section);
        }
        return sections;
    }
    
    private static List<Modifier> createModifiers(TemplatingParser.ModifierContext modifierContext){
        List<Modifier> modifiers = new ArrayList<>();
        if(modifierContext == null || modifierContext.modifierContent() == null) return modifiers;
        
        for(TemplatingParser.ModContext mod : modifierContext.modifierContent().mod()){
            modifiers.add(new Modifier(
                mod.IDENTIFIER().getText(),
                mod.literal().stream().map(Template::literalToObject).toList()
            ));
        }
        return modifiers;
    }
    
    private static Object literalToObject(TemplatingParser.LiteralContext literalContext){
        if(literalContext instanceof TemplatingParser.BoolContext boolContext)
            return Boolean.valueOf(boolContext.getText());
        if(literalContext instanceof TemplatingParser.IntContext intContext)
            return Integer.valueOf(intContext.getText());
        if(literalContext instanceof TemplatingParser.DoubleContext doubleContext)
            return Double.valueOf(doubleContext.getText());
        if(literalContext instanceof TemplatingParser.StringContext stringContext)
            return parseString(stringContext.getText());
        
        return null;
    }
    
    private static String parseString(String text) {
        return text.substring(1, text.length()-1)
            .replace( "\\\\", "\\")
            .replace("\\b","\b")
            .replace("\\t","\t")
            .replace("\\n","\n")
            .replace("\\r","\r")
            .replace("\\'","\'")
            .replace("\\\"","\"");
    }
}
