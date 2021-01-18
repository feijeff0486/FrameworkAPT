package com.jeff.framework.apt.annotationprocessor;

import com.google.auto.service.AutoService;
import com.jeff.framework.apt.annotation.PresenterGenerator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
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
import javax.tools.Diagnostic;

/**
 * 处理 {@link PresenterGenerator} 注解，自动生成对应的 Presenter关联逻辑
 * <p>
 *
 * @author Jeff
 * @date 2021/1/7
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
@AutoService(Processor.class)
public class PresenterAnnotationProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(PresenterGenerator.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment env) {
        if (env.processingOver()) {
            return true;
        }
        for (Element element : env.getElementsAnnotatedWith(PresenterGenerator.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                error(String.format("Only classes can be annotated with @%s", PresenterGenerator.class.getSimpleName()));
                continue;
            }
            final String className = element.toString();
            final String simpleClassName = element.getSimpleName().toString();
            final String packageName = className.substring(0, className.lastIndexOf("."));
            String annotationValue = "";
            boolean useAndroidX = element.getAnnotation(PresenterGenerator.class).useAndroidX();
            Element actionElement = processingEnv.getElementUtils().getTypeElement(PresenterGenerator.class.getName());
            TypeMirror actionType = actionElement.asType();
            for (AnnotationMirror am : element.getAnnotationMirrors()) {
                if (am.getAnnotationType().equals(actionType)) {
                    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet()) {
                        note(String.format("%s@PresenterGenerator(%s={%s},useAndroidX=%s)\n", className, entry.getKey().getSimpleName().toString(), entry.getValue().getValue().toString(),useAndroidX));
                        if ("value".equals(entry.getKey().getSimpleName().toString())) {
                            annotationValue = entry.getValue().getValue().toString();
                            break;
                        }
                    }
                }
            }
            if (className.length() > 0 && annotationValue.length() > 0) {
                generatePresenterProviderJavaFile(packageName, className, simpleClassName, annotationValue, useAndroidX);
            }
        }
        return true;
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
    private void generatePresenterProviderJavaFile(String packageName, String className,
                                           String simpleClassName, String annotationValue,
                                           boolean useAndroidX) {
        note("generatePresenterProviderJavaFile className=" + className
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
        //class XXPresenterProvider
//        Class<?> simpleClass= null;
//        try {
//            simpleClass = Class.forName(packageName+"."+simpleClassName);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        TypeVariableName vTypeVariableName=TypeVariableName.get("V", TypeName.get(simpleClass));
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(simpleClassName + "PresenterProvider")
                .superclass(ClassName.get("com.jeff.framework.mvp", "AbstractPresenterProvider"))
//                .addTypeVariable(vTypeVariableName)
                .addModifiers(Modifier.PUBLIC);

        //field mPresenterStore
//        ClassName presenterStoreClass = ClassName.get("com.jeff.framework.mvp","PresenterStore");
//        classBuilder.addField(FieldSpec.builder(presenterStoreClass, "mPresenterStore", Modifier.PRIVATE)
//                .initializer("new $T()", presenterStoreClass)
//                .build());

        //field mPresenterDispatcher
//        ClassName presenterDispatcherClass = ClassName.get("com.jeff.framework.mvp","PresenterDispatcher");
//        classBuilder.addField(FieldSpec.builder(presenterDispatcherClass, "mPresenterDispatcher", Modifier.PRIVATE)
//                .initializer("new $T($N)", presenterDispatcherClass,"mPresenterStore")
//                .build());

        //method constructor
        ClassName bundle = ClassName.get("android.os", "Bundle");
        ClassName nullable = ClassName.get("android.support.annotation","Nullable");
        ClassName nonNull=ClassName.get("android.support.annotation","NonNull");
        ClassName targetClass = ClassName.get(packageName,simpleClassName);
        ParameterSpec targetParam= ParameterSpec.builder(targetClass,"target").addAnnotation(nonNull).build();
        ParameterSpec savedInstanceStateParam= ParameterSpec.builder(bundle,"savedInstanceState").addAnnotation(nullable).build();

        MethodSpec.Builder constructorBuilder=MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PROTECTED)
                .addParameters(Arrays.asList(targetParam,savedInstanceStateParam))
                .addStatement("super(target, savedInstanceState)");

        //method attach
        ClassName thisClass=ClassName.get(packageName,simpleClassName + "PresenterProvider");

        MethodSpec.Builder attchBuilder=MethodSpec.methodBuilder("attach")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .addParameters(Arrays.asList(targetParam,savedInstanceStateParam))
                .addStatement("return new $N($N,$N)",simpleClassName + "PresenterProvider","target","savedInstanceState")
                .returns(thisClass);

        //storePresenters
        ClassName override = ClassName.get("java.lang","Override");
        MethodSpec.Builder storePresentersBuilder=MethodSpec.methodBuilder("storePresenters")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(override);

        //method onSaveInstanceState(Bundle)
//        ParameterSpec outStateParam= ParameterSpec.builder(bundle,"outState").build();
//        MethodSpec.Builder onSaveInstanceStateBuilder=MethodSpec.methodBuilder("onSaveInstanceState")
//                .addModifiers(Modifier.PUBLIC)
//                .addParameter(outStateParam)
//                .addStatement("if (mPresenterDispatcher != null) {mPresenterDispatcher.onSaveInstanceState(outState);}");

        //method detach
        MethodSpec.Builder detachBuilder=MethodSpec.methodBuilder("detach")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(override)
                .addStatement("super.detach()");
//                .addStatement("if (mPresenterDispatcher != null) {mPresenterDispatcher.detach();}")
//                .addStatement("if(mPresenterStore!=null){mPresenterStore.clear();}");

        //method getPresenter(String key)
//        ClassName string=ClassName.get("java.lang","String");
//        ClassName basePresenter=ClassName.get("com.jeff.framework.mvp","BasePresenter");
//        ParameterSpec key= ParameterSpec.builder(string,"key").addAnnotation(nonNull).build();
//        MethodSpec.Builder getPresenterBuilder=MethodSpec.methodBuilder("getPresenter")
//                .addModifiers(Modifier.PUBLIC)
//                .addParameter(key)
//                .addStatement("return $N", "mPresenterStore.get(key)")
//                .returns(basePresenter);
//        classBuilder.addMethod(getPresenterBuilder.build());

        //method getPresenter(Class cls)
//        ClassName classClass=ClassName.get("java.lang","Class");
//        ParameterSpec cls= ParameterSpec.builder(classClass,"cls").addAnnotation(nonNull).build();
//        MethodSpec.Builder getPresenter2Builder=MethodSpec.methodBuilder("getPresenter")
//                .addModifiers(Modifier.PUBLIC)
//                .addParameter(cls)
//                .addStatement("System.out.println(\"getPresenter cls=\" + $N)", "cls.getName()")
//                .addStatement("return getPresenter($N)","cls.getName()")
//                .returns(basePresenter);
//        classBuilder.addMethod(getPresenter2Builder.build());

        for (int i = 0; i < annotationClassNames.length; i++) {
            String annotationPackageName = annotationClassNames[i].substring(0, annotationClassNames[i].lastIndexOf("."));
            ClassName annotationClass = ClassName.get(annotationPackageName, annotationSimpleNames[i]);
            //生成成员变量
            String presenterField="m" + annotationSimpleNames[i];
            classBuilder.addField(FieldSpec.builder(annotationClass, presenterField, Modifier.PRIVATE)
//                    .initializer("new $T()", annotationClass)
                    .build());

            storePresentersBuilder.addStatement("$N = new $T()", presenterField,annotationClass)
                    .addStatement("getPresenterStore().put(\"$N\",$N)", annotationPackageName+"."+annotationSimpleNames[i],presenterField);
            detachBuilder.addStatement("$N=null",presenterField);
            //生成方法
            classBuilder.addMethod(MethodSpec.methodBuilder("get" + annotationSimpleNames[i])
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("return $N", presenterField)
                    .returns(annotationClass)
                    .build());
        }


//        constructorBuilder.addStatement("mPresenterDispatcher.attach($N)","target")
//                .addStatement("mPresenterDispatcher.onCreatePresenter(savedInstanceState)");
        classBuilder.addMethod(constructorBuilder.build());
        classBuilder.addMethod(storePresentersBuilder.build());
        classBuilder.addMethod(attchBuilder.build());
//        classBuilder.addMethod(onSaveInstanceStateBuilder.build());
        classBuilder.addMethod(detachBuilder.build());

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
