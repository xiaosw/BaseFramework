package com.xiaosw.permission.compiler;

import com.google.auto.service.AutoService;
import com.xiaosw.permission.annotation.PermissionDenied;
import com.xiaosw.permission.annotation.PermissionGrant;
import com.xiaosw.permission.annotation.ShowRequestPermissionRationale;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import static javax.lang.model.SourceVersion.latestSupported;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * @ClassName {@link PermissionProcessor}
 * @Description
 * @Date 2018-02-06.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

@AutoService(Processor.class)
public class PermissionProcessor extends AbstractProcessor {
    private Messager mMessager;
    private Elements mElementUtils;
    private Map<String, ClassInfo> mProxyMap = new HashMap<String, ClassInfo>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mElementUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(PermissionDenied.class.getCanonicalName());
        supportTypes.add(PermissionGrant.class.getCanonicalName());
        supportTypes.add(ShowRequestPermissionRationale.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return latestSupported();
    }

    private boolean processAnnotations(RoundEnvironment roundEnv, Class<? extends Annotation> clazz) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(clazz)) {

            if (!checkMethodValid(annotatedElement, clazz)) {
                return false;
            }

            ExecutableElement annotatedMethod = (ExecutableElement) annotatedElement;
            //class type
            TypeElement classElement = (TypeElement) annotatedMethod.getEnclosingElement();
            //full class name
            String fullClassName = classElement.getQualifiedName().toString();

            ClassInfo proxyInfo = mProxyMap.get(fullClassName);
            if (proxyInfo == null) {
                proxyInfo = new ClassInfo(processingEnv, mElementUtils, classElement);
                mProxyMap.put(fullClassName, proxyInfo);
                proxyInfo.setTypeElement(classElement);
            }

            Annotation annotation = annotatedMethod.getAnnotation(clazz);
            if (annotation instanceof PermissionGrant) {
                int requestCode = ((PermissionGrant) annotation).requestCode();
                proxyInfo.grantMethodMap.put(requestCode, annotatedMethod.getSimpleName().toString());
            } else if (annotation instanceof PermissionDenied) {
                int requestCode = ((PermissionDenied) annotation).requestCode();
                proxyInfo.deniedMethodMap.put(requestCode, annotatedMethod.getSimpleName().toString());
            } else if (annotation instanceof ShowRequestPermissionRationale) {
                int requestCode = ((ShowRequestPermissionRationale) annotation).requestCode();
                proxyInfo.rationaleMethodMap.put(requestCode, annotatedMethod.getSimpleName().toString());
            } else {
                error(annotatedElement, "%s not support .", clazz.getSimpleName());
                return false;
            }

        }

        return true;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mProxyMap.clear();
        mMessager.printMessage(Diagnostic.Kind.NOTE, "--------------------------process...--------------------------");

        if (!processAnnotations(roundEnv, PermissionGrant.class)) {
            return false;
        }
        if (!processAnnotations(roundEnv, PermissionDenied.class)) {
            return false;
        }
        if (!processAnnotations(roundEnv, ShowRequestPermissionRationale.class)) {
            return false;
        }


        for (String key : mProxyMap.keySet()) {
            mProxyMap.get(key).generateJavaCode();
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, "-------------------------- end...--------------------------");
        return true;
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message, element);
    }

    private boolean checkMethodValid(Element annotatedElement, Class clazz) {
        if (annotatedElement.getKind() != ElementKind.METHOD) {
            error(annotatedElement, "%s must be declared on method.", clazz.getSimpleName());
            return false;
        }
        if (isPrivate(annotatedElement) || isAbstract(annotatedElement)) {
            error(annotatedElement, "%s() must can not be abstract or private.", annotatedElement.getSimpleName());
            return false;
        }

        return true;
    }

    private boolean isPublic(Element annotatedClass) {
        return annotatedClass.getModifiers().contains(PUBLIC);
    }

    private boolean isPrivate(Element annotatedClass) {
        return annotatedClass.getModifiers().contains(PRIVATE);
    }

    private boolean isAbstract(Element annotatedClass) {
        return annotatedClass.getModifiers().contains(ABSTRACT);
    }

    static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen)
                .replace('.', '$');
    }

}
