package com.kong.codebuilder.loader;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.kong.codebuilder.pojo.ClassInfo;
import com.kong.codebuilder.util.ClassUtil;

/**
 * 加载类属性
 */
public class ClassInfoLoader {

    /**
     * 加载类属性
     *
     * @param pojoName 类名
     * @return
     */
    public static ClassInfo loadClassInfo(Project project, String pojoName) {
        //查找类文件
        String pojoFileShortName = pojoName + ".java";
        PsiFile[] psiFileArry = FilenameIndex
                .getFilesByName(project, pojoFileShortName, GlobalSearchScope.projectScope(project));

        //校验文件是否存在
        if (psiFileArry == null || psiFileArry.length == 0) {
            throw new RuntimeException(pojoFileShortName + "文件不存在");
        }

        //校验文件唯一性
        if (psiFileArry.length != 1) {
            throw new RuntimeException(pojoFileShortName + "文件不唯一:" + psiFileArry);
        }

        //获取class和包对象
        PsiElement child = psiFileArry[0].getFirstChild();
        ClassInfo classInfo = new ClassInfo();
        do {
            if (child instanceof PsiClass) {
                PsiClass psiClass = (PsiClass) child;
                classInfo.setPsiClass(psiClass);
                classInfo.setFieldList(ClassUtil.getFieldInfoList(psiClass));
            } else if (child instanceof PsiPackageStatement) {
                classInfo.setPsiPackage((PsiPackageStatement) child);
            } else if (child instanceof PsiImportList) {
                classInfo.setPsiImportList((PsiImportList) child);
            }
            child = child.getNextSibling();
        }
        while (child != null);
        return classInfo;
    }
}
