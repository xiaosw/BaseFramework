package com.xiaosw.permission.compiler;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * @ClassName {@link ClassInfo}
 * @Description
 *
 * @Date 2018-02-06.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
public class ClassInfo {
    private String packageName;
    private String proxyClassName;
    private TypeElement typeElement;
    private ProcessingEnvironment mProcessingEnv;

    Map<Integer, String> grantMethodMap = new HashMap<>();
    Map<Integer, String> deniedMethodMap = new HashMap<>();
    Map<Integer, String> rationaleMethodMap = new HashMap<>();

    public static final String PROXY = "PermissionProxy";

    public ClassInfo(ProcessingEnvironment processingEnv, Elements elementUtils, TypeElement classElement) {
        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        String packageName = packageElement.getQualifiedName().toString();
        //classname
        String className = PermissionProcessor.getClassName(classElement, packageName);
        this.packageName = packageName;
        this.proxyClassName = className.concat("$$").concat(PROXY);
        this.mProcessingEnv = processingEnv;
    }

    public String getProxyClassFullName() {
        return packageName + "." + proxyClassName;
    }

    /**
     * @return
     */
    private String buildJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("// Generated code. Do not modify!\n");
        builder.append("package ").append(packageName).append(";\n\n");

        builder.append("import com.xiaosw.common.helper.proxy.PermissionProxy;\n");
        builder.append('\n');

        builder.append("public class ").append(proxyClassName).append(" implements " + ClassInfo.PROXY + "<" + typeElement.getSimpleName() + ">");
        builder.append(" {\n");

        generateMethods(builder);
        builder.append('\n');

        builder.append("}\n");
        return builder.toString();

    }

    private void generateMethods(StringBuilder builder) {
        generateGrantMethod(builder);
        generateDeniedMethod(builder);
        generateRationaleMethod(builder);
    }

    private void generateRationaleMethod(StringBuilder builder) {
        builder.append("@Override\n ");
        builder.append("public void onRationale(" + typeElement.getSimpleName() + " target , int requestCode, String... permissions) {\n");
        builder.append("switch(requestCode) {");
        for (int code : rationaleMethodMap.keySet()) {
            builder.append("case " + code + ":");
            builder.append("target." + rationaleMethodMap.get(code) + "();");
            builder.append("break;");
        }

        builder.append("}");
        builder.append("  }\n");

        ///

        builder.append("@Override\n ");
        builder.append("public boolean onNeedShowRationale(int requestCode) {\n");
        builder.append("switch(requestCode) {");
        for (int code : rationaleMethodMap.keySet()) {
            builder.append("case " + code + ":");
            builder.append("return true;");
        }
        builder.append("}\n");
        builder.append("return false;");

        builder.append("  }\n");
    }

    private void generateDeniedMethod(StringBuilder builder) {
        builder.append("@Override\n ");
        builder.append("public void onDenied(" + typeElement.getSimpleName() + " target , int requestCode) {\n");
        builder.append("switch(requestCode) {");
        for (int code : deniedMethodMap.keySet()) {
            builder.append("case " + code + ":");
            builder.append("target." + deniedMethodMap.get(code) + "();");
            builder.append("break;");
        }

        builder.append("}");
        builder.append("  }\n");
    }

    private void generateGrantMethod(StringBuilder builder) {
        builder.append("@Override\n ");
        builder.append("public void onGrant(" + typeElement.getSimpleName() + " target , int requestCode) {\n");
        builder.append("switch(requestCode) {");
        for (int code : grantMethodMap.keySet()) {
            builder.append("case " + code + ":");
            builder.append("target." + grantMethodMap.get(code) + "();");
            builder.append("break;");
        }

        builder.append("}");
        builder.append("  }\n");
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public void setTypeElement(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    /**
     * 生成java代码
     */
    public void generateJavaCode() {
        try {
            JavaFileObject jfo = mProcessingEnv.getFiler().createSourceFile(
                    getProxyClassFullName(),
                    getTypeElement());
            Writer writer = jfo.openWriter();
            writer.write(buildJavaCode());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            error(getTypeElement(),
                    "Unable to write injector for type %s: %s",
                    getTypeElement(), e.getMessage());
        }
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        mProcessingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message, element);
    }

}