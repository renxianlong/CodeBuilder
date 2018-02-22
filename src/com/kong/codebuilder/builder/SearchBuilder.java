package com.kong.codebuilder.builder;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.kong.codebuilder.loader.MapperBuilderConfigLoader;
import com.kong.codebuilder.pojo.ClassInfo;
import com.kong.codebuilder.util.ClassUtil;
import org.jetbrains.annotations.NotNull;

public class SearchBuilder {

    /**
     * 生成Search
     */
    public static void createSearch() {
        ClassInfo baseSearch = MapperBuilderConfigLoader.getInstance().getBaseSearch();
        Project project = MapperBuilderConfigLoader.getInstance().getProject();
        if (null != baseSearch) {
            MapperBuilderConfigLoader.getInstance().getClassInfoList().forEach(classInfo -> {
                new WriteCommandAction(project) {
                    @Override
                    protected void run(@NotNull Result result) throws Throwable {
                        createSearch(classInfo, baseSearch);
                    }
                }.execute();
            });
        }
    }

    /**
     * 创建查询类
     *
     * @param classInfo
     * @param baseSearch
     */
    private static void createSearch(ClassInfo classInfo, ClassInfo baseSearch) {
        Project project = MapperBuilderConfigLoader.getInstance().getProject();
        PsiElementFactory psiElementFactory = PsiElementFactory.SERVICE.getInstance(project);

        //获取类名
        String className = classInfo.getPsiClass().getNameIdentifier().getText() + "Search";

        //生成PsiClass
        PsiDirectory searchDirectory = MapperBuilderConfigLoader.getInstance().getSearchDirectory();
        PsiClass searchClass = JavaDirectoryService.getInstance().createClass(searchDirectory, className);

        //设置包名
        ((PsiJavaFile) searchClass.getContainingFile()).setPackageName(ClassUtil.getPackageName(searchDirectory));

        //设置import属性
        searchClass.add(classInfo.getPsiImportList());

        //设置继承属性
        searchClass.getExtendsList().add(psiElementFactory.createClassReferenceElement(baseSearch.getPsiClass()));

        //设置属性
        PsiField[] psiFieldArry = classInfo.getPsiClass().getAllFields();
        for (PsiField psiField : psiFieldArry) {
            searchClass.add(psiField);
        }

        //生成get和set方
        ClassUtil.generateAllGetAndSet(searchClass);

        //保存所有更改
        MapperBuilderConfigLoader.getInstance().getProject().save();
        VirtualFileManager.getInstance().syncRefresh();
    }
}
