package com.github.max_person.templating;

import com.github.max_person.templating.expressions.TemplateExprParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

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
        TemplatingParser p = new TemplatingParser(new CommonTokenStream(new TemplatingLexer(CharStreams.fromString(str))));
        sections = createSections(p.template(), interpolationParser);
    }
    
    private static List<TemplateSection> createSections(
        TemplatingParser.TemplateContext ctx,
        TemplateInterpolationParser<?> interpolationParser
    ){
        List<TemplateSection> sections = new ArrayList<>();
        for(TemplatingParser.TemplateSectionContext sectionContext : ctx.templateSection()){
            TemplateSection section;
            String sectionText = sectionContext.getText();
            if(sectionContext.STR()!= null){
                section = new StringSection(sectionText);
            }
            else { //Interpolation
                sectionText = sectionText.substring(1); //Removing '$' from interpolation content
                boolean isSimpleInterpolation;
                if(sectionContext.interpolation().SIMPLE_INTERPOLATION() != null){ //Simple interpolation
                    isSimpleInterpolation = true;
                }
                else { //ordinary interpolation
                    sectionText = sectionText.substring(1, sectionText.length() - 1); //Removing surrounding { } from interpolation content
                    isSimpleInterpolation = false;
                }
                section = interpolationParser.parse(
                    new TemplateInterpolationParser.InterpolationContent(sectionText, isSimpleInterpolation)
                );
            }
            sections.add(section);
        }
        return sections;
    }
    
    @Override
    public String interpret(InterpretationData data) {
        StringBuilder stringBuilder = new StringBuilder();
        for(TemplateSection section : sections){
            stringBuilder.append(section.interpret(data));
        }
        return stringBuilder.toString();
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
}
