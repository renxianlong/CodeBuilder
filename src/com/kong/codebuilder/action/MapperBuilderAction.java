package com.kong.codebuilder.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.kong.codebuilder.builder.CreateSqlBuilder;
import com.kong.codebuilder.builder.DaoBuilder;
import com.kong.codebuilder.builder.MappperBuilder;
import com.kong.codebuilder.builder.SearchBuilder;
import com.kong.codebuilder.loader.MapperBuilderConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapperBuilderAction extends AnAction {
    private static final Logger logger = LoggerFactory.getLogger(MapperBuilderAction.class);

    @Override
    public void actionPerformed(AnActionEvent event) {
        //解析配置文件
        MapperBuilderConfigLoader.getInstance().load(event);

        //生成sql
        CreateSqlBuilder.generateCreateSql();

        //生成xml文件
        MappperBuilder.generateMapper();

        //生成search类
        SearchBuilder.createSearch();

        //生成dao类
        DaoBuilder.createDao();
    }
}
