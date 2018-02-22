package com.kong.codebuilder.loader;

import com.google.common.base.Splitter;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.kong.codebuilder.common.Context;
import com.kong.codebuilder.pojo.ClassInfo;
import com.kong.codebuilder.util.FileUtil;
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
    private PsiDirectory searchDirectory;
    //生成的mapperDirectory
    private PsiDirectory mapperDirectory;
    //生成的dao的包路径
    private PsiDirectory daoDirectory;
    //生成的service的包路径
    private PsiDirectory serviceDirectory;
    //生成的建表语句的包路径
    private PsiDirectory createSqlDirectory;
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
    //项目
    private Project project;

    /**
     * 加载项目属性
     *
     * @param event
     */
    public void load(AnActionEvent event) {
        try {
            //设置属性
            this.project = event.getProject();
            Context.project = event.getProject();
            Properties properties = PropertiesLoader.load(event.getData(PlatformDataKeys.PSI_FILE));
            this.pojoList = Splitter.on("|").trimResults().omitEmptyStrings().splitToList(properties.getProperty("pojos"));
            classInfoList = new ArrayList<>();
            pojoList.forEach(pojo -> {
                classInfoList.add(ClassInfoLoader.loadClassInfo(project, pojo));
            });

            this.searchPath = properties.getProperty("search.path");
            this.searchDirectory = FileUtil.getPsiDirectory(project, searchPath);
            this.mapperPath = properties.getProperty("mapper.path");
            this.mapperDirectory = FileUtil.getPsiDirectory(project, mapperPath);
            this.daoPath = properties.getProperty("dao.path");
            this.daoDirectory = FileUtil.getPsiDirectory(project, daoPath);
            this.servicePath = properties.getProperty("service.path");
            this.serviceDirectory = FileUtil.getPsiDirectory(project, servicePath);
            this.createSqlPath = properties.getProperty("createsql.path");
            this.createSqlDirectory = FileUtil.getPsiDirectory(project, createSqlPath);

            String baseSearch = properties.getProperty("baseSearch");
            if (StringUtils.isNotBlank(baseSearch)) {
                this.baseSearch = ClassInfoLoader.loadClassInfo(project, baseSearch);
            }

        } catch (Exception e) {
            LOGGER.error("readConfigFile config file read error ", e);
            throw new RuntimeException("readConfigFile error occurred");
        }
    }

    /**
     * 根据路径获取PsiDirectory
     *
     * @param path
     * @return
     */
    private PsiDirectory getPsiDirectory(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        //获取Project根目录
        PsiDirectory baseDir = PsiDirectoryFactory.getInstance(project).createDirectory(project.getBaseDir());

        //递归查找要创建的目录
        PsiDirectory subDir = baseDir.findSubdirectory(path);

        //判断要创建的目录是否已存在
        boolean isExist = subDir == null;

        //如不存在，创建新目录
        if (!isExist) {
            subDir = baseDir.createSubdirectory(path);
        }

        return subDir;
//
//        VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(path);
//        VirtualFile baseDir = MapperBuilderConfigLoader.getInstance().getProject().getBaseDir();
//        baseDir.f
////        VirtualFileManager.getInstance().
//
//
////        PsiJavaDirectoryFactory.getInstance(this.project).
//
////        PsiFileFactory.getInstance(this.project).
//        if (null == virtualFile) {
//            VirtualFileManager.getInstance()
//            return PsiJavaDirectoryFactory.getInstance(this.project).createDirectory(virtualFile);
//        } else {
//            return PsiManager.getInstance(this.project).findDirectory(virtualFile);
//        }
    }

    public List<String> getPojoList() {
        return pojoList;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public PsiDirectory getSearchDirectory() {
        return searchDirectory;
    }

    public PsiDirectory getMapperDirectory() {
        return mapperDirectory;
    }

    public PsiDirectory getDaoDirectory() {
        return daoDirectory;
    }

    public PsiDirectory getServiceDirectory() {
        return serviceDirectory;
    }

    public PsiDirectory getCreateSqlDirectory() {
        return createSqlDirectory;
    }

    public Project getProject() {
        return project;
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
