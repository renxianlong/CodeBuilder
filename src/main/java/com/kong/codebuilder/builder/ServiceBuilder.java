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

public class ServiceBuilder {

    /**
     * 生成Dao
     */
    public static void createService() {
        ClassInfo baseSearch = MapperBuilderConfigLoader.getInstance().getBaseSearch();
        if (null != baseSearch) {
            MapperBuilderConfigLoader.getInstance().getClassInfoList().forEach(classInfo -> {
                new WriteCommandAction(Context.project) {
                    @Override
                    protected void run(@NotNull Result result) throws Throwable {
                        createService(classInfo);
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
    private static void createService(ClassInfo classInfo) {
        //类名
        String pojoClass = classInfo.getPsiClass().getNameIdentifier().getText();
        //java文件名
        String fileName = pojoClass + "Service.java";
        //文件路径
        String filePath = MapperBuilderConfigLoader.getInstance().getServicePath();
        //包名
        PsiDirectory searchDirectory = FileUtil.getPsiDirectory(Context.project, filePath);
        String packageName = ClassUtil.getPackageName(searchDirectory);

        String daoFile = "package " + packageName + ";\n" +
                "\n" +
                "import java.util.List;\n" +
                "import " + Context.getAttribute("DAO_FULL_NAME").toString() + ";\n" +
                "import " + classInfo.getSearchClass().getClassFullName() + ";\n" +
                "import " + classInfo.getClassFullName() + ";\n" +
                "import org.springframework.stereotype.Service;\n" +
                "\n" +
                "import javax.annotation.Resource;\n" +
                "\n" +
                "@Service\n" +
                "public class " + pojoClass + "Service {\n" +
                "    @Resource\n" +
                "    private " + pojoClass + "Dao " + formatFieldName(pojoClass) + "Dao;\n" +
                "\n" +
                "    public void insert(" + pojoClass + " " + formatFieldName(pojoClass) + ") {\n" +
                "        " + formatFieldName(pojoClass) + "Dao.insert(" + formatFieldName(pojoClass) + ");\n" +
                "    }\n" +
                "\n" +
                "    public void batchInsert(List<" + pojoClass + "> " + formatFieldName(pojoClass) + "List) {\n" +
                "        " + formatFieldName(pojoClass) + "Dao.batchInsert(" + formatFieldName(pojoClass) + "List);\n" +
                "    }\n" +
                "\n" +
                "    public void delete(Long id) {\n" +
                "        " + formatFieldName(pojoClass) + "Dao.delete(id);\n" +
                "    }\n" +
                "\n" +
                "    public void update(" + pojoClass + " " + formatFieldName(pojoClass) + ") {\n" +
                "        " + formatFieldName(pojoClass) + "Dao.update(" + formatFieldName(pojoClass) + ");\n" +
                "    }\n" +
                "\n" +
                "    public " + pojoClass + " getById(Long id) {\n" +
                "        return " + formatFieldName(pojoClass) + "Dao.getById(id);\n" +
                "    }\n" +
                "\n" +
                "    public List<" + pojoClass + "> search(" + pojoClass + "Search search) {\n" +
                "        return " + formatFieldName(pojoClass) + "Dao.search(search);\n" +
                "    }\n" +
                "\n" +
                "    public int count(" + pojoClass + "Search search) {\n" +
                "        return " + formatFieldName(pojoClass) + "Dao.count(search);\n" +
                "    }\n" +
                "}\n";

        //创建文件
        FileUtil.createFile(fileName, filePath, daoFile);

        ApplicationManager.getApplication().saveAll();
        VirtualFileManager.getInstance().syncRefresh();
    }

    private static String formatFieldName(String className) {
        String firstChar = className.substring(0, 1);
        String otherChar = className.substring(1);
        return firstChar.toLowerCase() + otherChar;
    }
}
