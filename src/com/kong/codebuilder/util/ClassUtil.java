package com.kong.codebuilder.util;

import com.google.common.base.CaseFormat;
import com.intellij.psi.*;
import com.kong.codebuilder.common.Context;
import com.kong.codebuilder.pojo.ClassInfo;
import org.apache.commons.lang3.tuple.Pair;

public class ClassUtil {
    /**
     * 根据classInfo获取表名
     *
     * @param classInfo
     * @return
     */
    public static String generateTableName(ClassInfo classInfo) {
        String className = classInfo.getPsiClass().getName();
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
    }

    /**
     * 组装继承后的类
     *
     * @param baseClass   子类
     * @param extendClass 父类
     * @return
     */
    public Pair<PsiJavaFile, ClassInfo> createJavaFile(ClassInfo baseClass, ClassInfo extendClass) {
////       PsiClass psiClass =  baseClass.getPsiClass();
////       PsiJavaFile psiJavaFile;
////       PsiJavaModule psiJavaModule;
////       psiJavaFile.getSingleClassImports()
////       psiJavaFile.get
////
////        PsiElementExternalizer
////
////       psiClass.add();
////
////       psiJavaFile.findImportReferenceTo()
////
////       psiClass.getExtendsList();
//
////        PsiElementFactory.SERVICE.getInstance()
//        PsiJavaFileBaseImpl
        return null;
    }

    /**
     * 生成get方法
     *
     * @param clazz
     * @param field
     */
    public static void generateGetMethod(PsiClass clazz, PsiField field) {
        PsiElementFactory psiElementFactory = PsiElementFactory.SERVICE.getInstance(Context.project);

        String fieldName = field.getNameIdentifier().getText();
        String firstChar = fieldName.substring(0, 1);
        String otherChar = fieldName.substring(1);
        //生成get方法
        String getMethodName = "get" + firstChar.toUpperCase() + otherChar.toLowerCase();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("private " + field.getType().getCanonicalText() + " " + getMethodName + "() {\n");
        stringBuilder.append("    return this." + field.getNameIdentifier().getText() + ";\n");
        stringBuilder.append("}");
        PsiMethod getMethod = psiElementFactory.createMethodFromText(stringBuilder.toString(), clazz);
        clazz.add(getMethod);
    }

    /**
     * 生成set方法
     *
     * @param clazz
     * @param field
     */
    public static void generateSetMethod(PsiClass clazz, PsiField field) {
        PsiElementFactory psiElementFactory = PsiElementFactory.SERVICE.getInstance(Context.project);

        String fieldName = field.getNameIdentifier().getText();
        String firstChar = fieldName.substring(0, 1);
        String otherChar = fieldName.substring(1);
        //生成set方法名
        String setMethodName = "set" + firstChar.toUpperCase() + otherChar.toLowerCase();

        //生成get方法
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.setLength(0);
        stringBuilder.append("private void " + setMethodName + "(" +
                field.getType().getCanonicalText() + " " + fieldName + ") {\n");
        stringBuilder.append("    this." + fieldName + " = " + fieldName + ";\n");
        stringBuilder.append("}");
        PsiMethod setMethod = psiElementFactory.createMethodFromText(stringBuilder.toString(), clazz);
        clazz.add(setMethod);
    }

    /**
     * 生成所有属性的get和set方法
     *
     * @param clazz
     */
    public static void generateAllGetAndSet(PsiClass clazz) {
        for (PsiField psiField : clazz.getAllFields()) {
            generateGetMethod(clazz, psiField);
            generateSetMethod(clazz, psiField);
        }
    }

    /**
     * 获取包名
     *
     * @param directory
     * @return
     */
    public static String getPackageName(PsiDirectory directory) {
        PsiDirectory current = directory;
        String packageName = current.getName();
        PsiDirectory parent = current.getParent();
        while (!parent.getName().equals("java")) {
            packageName = parent.getName() + "." + packageName;
            parent = parent.getParent();
        }

        return packageName;
    }
}
