package com.kong.codebuilder.pojo;

import com.kong.codebuilder.ClassType;
import com.google.common.base.CaseFormat;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiPackageStatement;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * 类相关信息封装
 */
public class ClassInfo {
    //包信息
    private PsiPackageStatement psiPackage;
    //import信息
    private PsiImportList psiImportList;
    //类信息
    private PsiClass psiClass;
    //类名
    private String classFullName;
    //属性集合
    private List<FieldInfo> fieldList;
    //search类
    private ClassInfo searchClass;

    /**
     * 获取属性集合
     *
     * @return
     */
    public List<FieldInfo> getFieldList() {
        return fieldList;
    }

    /**
     * 获取完整类名
     *
     * @return
     */
    public String getClassFullName() {
        if (null != classFullName) {
            return classFullName;
        }

        String packageName = psiPackage.getPackageName();
        classFullName = packageName + "." + psiClass.getNameIdentifier().getText();
        return classFullName;
    }

    public PsiPackageStatement getPsiPackage() {
        return psiPackage;
    }

    public void setPsiPackage(PsiPackageStatement psiPackage) {
        this.psiPackage = psiPackage;
    }

    public PsiImportList getPsiImportList() {
        return psiImportList;
    }

    public void setPsiImportList(PsiImportList psiImportList) {
        this.psiImportList = psiImportList;
    }

    public void setClassFullName(String classFullName) {
        this.classFullName = classFullName;
    }

    public void setFieldList(List<FieldInfo> fieldList) {
        this.fieldList = fieldList;
    }

    public PsiClass getPsiClass() {
        return psiClass;
    }

    public void setPsiClass(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    public ClassInfo getSearchClass() {
        return searchClass;
    }

    public void setSearchClass(ClassInfo searchClass) {
        this.searchClass = searchClass;
    }
}
