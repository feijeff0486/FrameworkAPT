package com.jeff.framework.apt.annotationprocessor;

import com.google.auto.service.AutoService;
import com.jeff.framework.apt.annotation.ViewModelGenerator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * 处理 {@link ViewModelGenerator} 注解，自动生成对应的 ViewModel 类
 * <p>
 *
 * @author Jeff
 * @date 2021/1/7
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
@AutoService(Processor.class)
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
//@SupportedOptions()
public class ViewModelAnnotationProcessor extends AbstractProcessor {
    private Messager messager;
    private Elements elementUtils;
    private Filer filer;
    private Types typeUtils;

    /**
     * 初始化操作
     *
     * @param processingEnvironment
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        typeUtils = processingEnvironment.getTypeUtils();
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
    }

    /**
     * 返回此 Processor 支持的注释类型的名称。结果元素可能是某一受支持注释类型的规范（完全限定）名称。
     * 它也可能是 " name.*" 形式的名称，表示所有以 " name." 开头的规范名称的注释类型集合。
     * 最后， "*" 自身表示所有注释类型的集合，包括空集。
     * 注意，Processor 不应声明 "*"，除非它实际处理了所有文件；
     * 声明不必要的注释可能导致在某些环境中的性能下降。
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(ViewModelGenerator.class.getCanonicalName());
        return annotations;
    }

    /**
     * 返回此Processor可以处理的注解操作
     *
     * @return
     */
    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    /**
     * 返回此注释 Processor 支持的最新的源版本
     * 该方法可以通过注解@SupportedSourceVersion指定
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 注解处理器的核心方法，处理具体的注解
     *
     * @param set
     * @param env
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment env) {
        if (env.processingOver()) {
            return true;
        }
        for (Element element : env.getElementsAnnotatedWith(ViewModelGenerator.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                error(String.format("Only classes can be annotated with @%s", ViewModelGenerator.class.getSimpleName()));
                continue;
            }
            final String className = element.toString();
            final String simpleClassName = element.getSimpleName().toString();
            final String packageName = className.substring(0, className.lastIndexOf("."));
            String annotationValue = "";
            boolean useAndroidX = element.getAnnotation(ViewModelGenerator.class).useAndroidX();
            Element actionElement = processingEnv.getElementUtils().getTypeElement(ViewModelGenerator.class.getName());
            TypeMirror actionType = actionElement.asType();
            for (AnnotationMirror am : element.getAnnotationMirrors()) {
                if (am.getAnnotationType().equals(actionType)) {
                    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet()) {
                        note(String.format("%s@ViewModelGenerator({%s=%s})\n", className, entry.getKey().getSimpleName().toString(), entry.getValue().getValue().toString()));
                        if ("value".equals(entry.getKey().getSimpleName().toString())) {
                            annotationValue = entry.getValue().getValue().toString();
                            break;
                        }
                    }
                }
            }
            if (className.length() > 0 && annotationValue.length() > 0) {
                generateViewModelJavaFile(packageName, className, simpleClassName, annotationValue, useAndroidX);
            }
        }
        return true;
    }

    private Map<TypeElement, List<ViewModelGenerator>> getViewModelTargetMap(RoundEnvironment env) {
        return getTargetMap(env, ViewModelGenerator.class, ElementKind.CLASS);
    }

    private <A extends Annotation> Map<TypeElement, List<A>> getTargetMap(RoundEnvironment env,
                                                                          Class<A> cls,
                                                                          ElementKind... kinds) {
        Map<TypeElement, List<A>> targetMap = new HashMap<>();
        Set<? extends Element> annotatedElements = env.getElementsAnnotatedWith(cls);
        //获取代码中所有使用注解修饰的字段
        for (Element element : env.getElementsAnnotatedWith(cls)) {
            if (!isSupportElementKind(kinds, element.getKind())) {
                error(String.format("Only %s can be annotated with @%s", getSupportElementKinds(kinds), cls.getSimpleName()));
                continue;
            }
            if (element.getKind() == ElementKind.CLASS) {

            } else if (element.getKind() == ElementKind.FIELD) {
                handleFieldElement(element, cls);
            } else if (element.getKind() == ElementKind.METHOD) {

            }
        }

        return targetMap;
    }

    private <A extends Annotation> void handleFieldElement(Element element, Class<A> cls) {
        // 获取字段名称 (textView)
        String fieldName = element.getSimpleName().toString();
        // 获取字段类型 (android.widget.TextView)
        TypeMirror fieldType = element.asType();
        // 获取注解元素的值 (R.id.textView)
//        int viewId = element.getAnnotation(cls).value();
    }

    private boolean isSupportElementKind(ElementKind[] defKinds, ElementKind useKind) {
        for (ElementKind kind : defKinds) {
            if (useKind == kind) {
                return true;
            }
        }
        return false;
    }

    private String getSupportElementKinds(ElementKind[] defKinds) {
        StringBuilder builder = new StringBuilder();
        for (ElementKind kind : defKinds) {
            builder.append(kind.name()).append(";");
        }
        return builder.toString();
    }

    /**
     * 生成对应的 ViewModel 类
     *
     * @param packageName     包名
     * @param className       类名
     * @param simpleClassName 类名
     * @param annotationValue 注解值
     * @param useAndroidX     是否使用AndroidX
     */
    private void generateViewModelJavaFile(String packageName, String className,
                                           String simpleClassName, String annotationValue,
                                           boolean useAndroidX) {
        note("generateViewModelJavaFile className=" + className
                + ", packageName=" + packageName
                + ", simpleClassName=" + simpleClassName
                + ", annotationValue=" + annotationValue
                + ", useAndroidX=" + useAndroidX);
        final String[] annotationClassNames = annotationValue.split(",");
        final String[] annotationSimpleNames = new String[annotationClassNames.length];
        for (int i = 0; i < annotationClassNames.length; i++) {
            annotationClassNames[i] = annotationClassNames[i].substring(0, annotationClassNames[i].lastIndexOf("."));
            annotationSimpleNames[i] = annotationClassNames[i].substring(annotationClassNames[i].lastIndexOf(".") + 1);
        }
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(simpleClassName + "ViewModel")
                .superclass(ClassName.get(useAndroidX ? "androidx.lifecycle" : "android.arch.lifecycle", "ViewModel"))
                .addModifiers(Modifier.PUBLIC);
        for (int i = 0; i < annotationClassNames.length; i++) {
            String annotationPackageName = annotationClassNames[i].substring(0, annotationClassNames[i].lastIndexOf("."));
            ClassName annotationClass = ClassName.get(annotationPackageName, annotationSimpleNames[i]);
            //生成成员变量
            classBuilder.addField(FieldSpec.builder(annotationClass, "m" + annotationSimpleNames[i], Modifier.PRIVATE)
                    .initializer("new $T()", annotationClass)
                    .build());
            //生成方法
            classBuilder.addMethod(MethodSpec.methodBuilder("get" + annotationSimpleNames[i])
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("return $N", "m" + annotationSimpleNames[i])
                    .returns(annotationClass)
                    .build());
        }
        JavaFile javaFile = JavaFile.builder(packageName, classBuilder.build()).build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void note(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
    }

    private void error(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg);
    }

}
