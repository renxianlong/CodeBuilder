package com.kong.codebuilder.builder;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.kong.codebuilder.common.Context;
import com.kong.codebuilder.loader.MapperBuilderConfigLoader;
import com.kong.codebuilder.pojo.ClassInfo;
import com.kong.codebuilder.pojo.FieldInfo;
import com.kong.codebuilder.util.ClassUtil;
import com.kong.codebuilder.util.FileUtil;
import org.jetbrains.annotations.NotNull;

public class SearchBuilder {

    /**
     * 生成Search
     */
    public static void createSearch() {
        ClassInfo baseSearch = MapperBuilderConfigLoader.getInstance().getBaseSearch();
        Project project = Context.project;
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
        PsiElementFactory psiElementFactory = PsiElementFactory.SERVICE.getInstance(Context.project);

        //获取类名
        String className = classInfo.getPsiClass().getNameIdentifier().getText() + "Search";

        //生成PsiClass
        PsiDirectory searchDirectory = FileUtil.getPsiDirectory(Context.project, MapperBuilderConfigLoader.getInstance().getSearchPath());
        PsiClass searchClass = JavaDirectoryService.getInstance().createClass(searchDirectory, className);

        //设置包名
        String packageName = ClassUtil.getPackageName(searchDirectory);
        ((PsiJavaFile) searchClass.getContainingFile()).setPackageName(packageName);

        //设置import属性
        if (classInfo.getPsiImportList() != null && classInfo.getPsiImportList().getAllImportStatements().length > 0) {
            for (PsiImportStatementBase psiImportStatementBase : classInfo.getPsiImportList().getAllImportStatements()) {
                ((PsiJavaFile) searchClass.getContainingFile()).getImportList().add(psiImportStatementBase);
            }
        }

        //设置继承属性
        searchClass.getExtendsList().add(psiElementFactory.createClassReferenceElement(baseSearch.getPsiClass()));

        //设置属性
        for (FieldInfo psiField : classInfo.getFieldList()) {
            searchClass.add(psiField.getPsiField());
        }

        //生成get和set方
        ClassUtil.generateAllGetAndSet(searchClass);

        ClassInfo searchClassInfo = new ClassInfo();
        searchClassInfo.setPsiClass(searchClass);
        searchClassInfo.setFieldList(ClassUtil.getFieldInfoList(searchClass));
        searchClassInfo.setClassFullName(packageName + "." + className);
        classInfo.setSearchClass(searchClassInfo);

        ApplicationManager.getApplication().saveAll();
        VirtualFileManager.getInstance().syncRefresh();
    }
}
