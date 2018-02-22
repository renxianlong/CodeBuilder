package com.kong.codebuilder.loader;

import com.google.common.base.Splitter;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.kong.codebuilder.common.Context;
import com.kong.codebuilder.pojo.ClassInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 用户配置管理类
 */
public class MapperBuilderConfigLoader {
    private final static Logger LOGGER = LoggerFactory.getLogger(MapperBuilderConfigLoader.class);
    public static MapperBuilderConfigLoader instance = new MapperBuilderConfigLoader();
    //pojo列表
    private List<String> pojoList;
    //解析后的类列表
    private List<ClassInfo> classInfoList;
    //生成的search类的路径
    private String searchPath;
    //生成的mapperDirectory
    private String mapperPath;
    //生成的dao的包路径
    private String daoPath;
    //生成的service的包路径
    private String servicePath;
    //生成的建表语句的包路径
    private String createSqlPath;
    //baseSearch类信息
    private ClassInfo baseSearch;

    /**
     * 加载项目属性
     *
     * @param event
     */
    public void load(AnActionEvent event) {
        try {
            //设置属性
            Project project = event.getProject();
            Context.project = project;
            Properties properties = PropertiesLoader.load(event.getData(PlatformDataKeys.PSI_FILE));
            this.pojoList = Splitter.on("|").trimResults().omitEmptyStrings().splitToList(properties.getProperty("pojos"));
            classInfoList = new ArrayList<>();
            pojoList.forEach(pojo -> {
                classInfoList.add(ClassInfoLoader.loadClassInfo(project, pojo));
            });

            this.searchPath = properties.getProperty("search.path");
            this.mapperPath = properties.getProperty("mapper.path");
            this.daoPath = properties.getProperty("dao.path");
            this.servicePath = properties.getProperty("service.path");
            this.createSqlPath = properties.getProperty("createsql.path");

            String baseSearch = properties.getProperty("baseSearch");
            if (StringUtils.isNotBlank(baseSearch)) {
                this.baseSearch = ClassInfoLoader.loadClassInfo(project, baseSearch);
            }

        } catch (Exception e) {
            LOGGER.error("readConfigFile config file read error ", e);
            throw new RuntimeException("readConfigFile error occurred");
        }
    }

    public List<String> getPojoList() {
        return pojoList;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public List<ClassInfo> getClassInfoList() {
        return classInfoList;
    }

    public ClassInfo getBaseSearch() {
        return baseSearch;
    }

    public String getSearchPath() {
        return searchPath;
    }

    public String getMapperPath() {
        return mapperPath;
    }

    public String getDaoPath() {
        return daoPath;
    }

    public String getServicePath() {
        return servicePath;
    }

    public String getCreateSqlPath() {
        return createSqlPath;
    }

    //单例
    private MapperBuilderConfigLoader() {

    }

    //单例
    public static MapperBuilderConfigLoader getInstance() {
        return instance;
    }
}
