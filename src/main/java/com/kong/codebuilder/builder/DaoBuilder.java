package com.kong.codebuilder.builder;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.kong.codebuilder.common.Context;
import com.kong.codebuilder.loader.MapperBuilderConfigLoader;
import com.kong.codebuilder.pojo.ClassInfo;
import com.kong.codebuilder.util.ClassUtil;
import com.kong.codebuilder.util.FileUtil;
import org.jetbrains.annotations.NotNull;

public class DaoBuilder {

    /**
     * 生成Dao
     */
    public static void createDao() {
        ClassInfo baseSearch = MapperBuilderConfigLoader.getInstance().getBaseSearch();
        if (null != baseSearch) {
            MapperBuilderConfigLoader.getInstance().getClassInfoList().forEach(classInfo -> {
                new WriteCommandAction(Context.project) {
                    @Override
                    protected void run(@NotNull Result result) throws Throwable {
                        createDao(classInfo);
                    }
                }.execute();
            });
        }
    }

    /**
     * 创建dao文件
     *
     * @param classInfo
     */
    private static void createDao(ClassInfo classInfo) {
        //类名
        String pojoClass = classInfo.getPsiClass().getNameIdentifier().getText();
        //java文件名
        String fileName = pojoClass + "Dao.java";
        //文件路径
        String filePath = MapperBuilderConfigLoader.getInstance().getDaoPath();
        //包名
        PsiDirectory searchDirectory = FileUtil.getPsiDirectory(Context.project, filePath);
        String packageName = ClassUtil.getPackageName(searchDirectory);
        //search类名
        String searchClassName = classInfo.getSearchClass().getPsiClass().getNameIdentifier().getText();

        String daoFile = "package " + packageName + ";\n" +
                "\n" +
                "import java.util.List;\n" +
                "import org.apache.ibatis.annotations.Param;\n" +

                "import " + classInfo.getSearchClass().getClassFullName() + ";\n" +
                "import " + classInfo.getClassFullName() + ";\n" +
                "\n" +
                "public interface " + pojoClass + "Dao" + " {\n" +
                "\n" +
                "    int insert(@Param(\"pojo\") " + pojoClass + " pojo);\n" +
                "\n" +
                "    int batchInsert(@Param(\"pojoList\") List<" + pojoClass + "> pojoList);\n" +
                "\n" +
                "    int delete(@Param(\"id\") Long id);\n" +
                "\n" +
                "    int update(@Param(\"pojo\") " + pojoClass + " pojo);\n" +
                "\n" +
                "    " + pojoClass + " getById(@Param(\"id\") Long id);\n" +
                "\n" +
                "    List<" + pojoClass + "> search(@Param(\"search\") " + searchClassName + " search);\n" +
                "\n" +
                "    int count(@Param(\"search\")" + searchClassName + " search);\n" +
                "}";

        //创建文件
        FileUtil.createFile(fileName, filePath, daoFile);

        Context.addAttribute("DAO_FULL_NAME", packageName + "." + pojoClass + "Dao");

        ApplicationManager.getApplication().saveAll();
        VirtualFileManager.getInstance().syncRefresh();
    }
}
