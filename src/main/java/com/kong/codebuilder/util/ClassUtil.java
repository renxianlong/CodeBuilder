package com.kong.codebuilder.util;

import com.google.common.base.CaseFormat;
import com.intellij.psi.*;
import com.kong.codebuilder.ClassType;
import com.kong.codebuilder.common.Context;
import com.kong.codebuilder.loader.MapperBuilderConfigLoader;
import com.kong.codebuilder.pojo.ClassInfo;
import com.kong.codebuilder.pojo.FieldInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

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
        String getMethodName = "get" + firstChar.toUpperCase() + otherChar;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("public " + field.getType().getCanonicalText() + " " + getMethodName + "() {\n");
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
        String setMethodName = "set" + firstChar.toUpperCase() + otherChar;

        //生成get方法
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.setLength(0);
        stringBuilder.append("public void " + setMethodName + "(" +
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
        for (PsiField psiField : clazz.getFields()) {
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

    /**
     * 获取属性列表
     *
     * @return
     */
    public static List<FieldInfo> getFieldInfoList(PsiClass psiClass) {

        List<FieldInfo> fieldList = new LinkedList<>();
        for (PsiField psiField : psiClass.getAllFields()) {
            if (psiField.getNameIdentifier().getText().equals("serialVersionUID")) {
                continue;
            }
            FieldInfo fieldInfo = getFieldInfo(psiField);
            fieldList.add(fieldInfo);
        }
        return fieldList;
    }

    @NotNull
    public static FieldInfo getFieldInfo(PsiField psiField) {
        String name = psiField.getNameIdentifier().getText();
        String columnName = "`" + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name) + "`";
        ClassType fieldType = ClassType.fromDesc(psiField.getType().getCanonicalText());
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setColumnName(columnName);
        fieldInfo.setName(name);
        fieldInfo.setType(fieldType);
        fieldInfo.setPsiField(psiField);
        return fieldInfo;
    }

    /**
     * 获取search类名
     * @param classInfo
     * @return
     */
    public static String getSimpleSearchName(ClassInfo classInfo){
        return classInfo.getPsiClass().getNameIdentifier().getText() + "Search";
    }

    public static String getFullSearchName(ClassInfo classInfo){
        //类名
        String pojoClass = classInfo.getPsiClass().getNameIdentifier().getText();
        //文件路径
        String filePath = MapperBuilderConfigLoader.getInstance().getSearchPath();
        //包名
        PsiDirectory searchDirectory = FileUtil.getPsiDirectory(Context.project, filePath);
        String packageName = ClassUtil.getPackageName(searchDirectory);
        return packageName + "." + pojoClass + "Search";
    }

    public static String getFullDaoName(ClassInfo classInfo){
        //类名
        String pojoClass = classInfo.getPsiClass().getNameIdentifier().getText();
        //文件路径
        String filePath = MapperBuilderConfigLoader.getInstance().getDaoPath();
        //包名
        PsiDirectory searchDirectory = FileUtil.getPsiDirectory(Context.project, filePath);
        String packageName = ClassUtil.getPackageName(searchDirectory);
        return packageName + "." + pojoClass + "Dao";
    }
}
