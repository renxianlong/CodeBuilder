package com.kong.codebuilder.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.roots.FileIndex;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.java.stubs.index.JavaFullClassNameIndex;
import com.intellij.psi.impl.java.stubs.index.JavaShortClassNameIndex;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.PsiDependentIndex;
import com.kong.codebuilder.builder.*;
import com.kong.codebuilder.common.Context;
import com.kong.codebuilder.loader.MapperBuilderConfigLoader;
import org.jetbrains.annotations.NotNull;

public class MapperBuilderAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        ApplicationManager.getApplication().saveAll();
        VirtualFileManager.getInstance().syncRefresh();
        Context.clear();

        //解析配置文件
        MapperBuilderConfigLoader.getInstance().load(event);

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                new WriteCommandAction(Context.project) {
                    @Override
                    protected void run(@NotNull Result result) throws Throwable {
                        MapperBuilderConfigLoader.getInstance().getClassInfoList().forEach(classInfo -> {
                            //生成sql
                            CreateSqlBuilder.generateCreateSql(classInfo);

                            //生成search类
                            SearchBuilder.createSearch(classInfo);

                            //生成dao类
                            DaoBuilder.createDao(classInfo);

                            //生成Service类
                            ServiceBuilder.createService(classInfo);

                            //生成xml文件
                            MapperBuilder.generateMapper(classInfo);
                        });

                        ApplicationManager.getApplication().saveAll();
                        VirtualFileManager.getInstance().syncRefresh();
                    }
                }.execute();
            }
        });
    }
}
