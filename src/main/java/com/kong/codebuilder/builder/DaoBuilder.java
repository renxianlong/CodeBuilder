package com.kong.codebuilder.builder;

import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElementFactory;
import com.kong.codebuilder.common.Context;
import com.kong.codebuilder.loader.MapperBuilderConfigLoader;
import com.kong.codebuilder.pojo.ClassInfo;
import com.kong.codebuilder.util.ClassUtil;
import com.kong.codebuilder.util.FileUtil;

public class DaoBuilder {
    /**
     * 创建dao文件
     *
     * @param classInfo
     */
    public static void createDao(ClassInfo classInfo) {
        //类名
        String pojoClass = classInfo.getPsiClass().getNameIdentifier().getText();

        //java文件名
        String fileName = pojoClass + "Dao.java";
        //文件路径
        String filePath = MapperBuilderConfigLoader.getInstance().getDaoPath();
        //包名
        PsiDirectory daoDirectory = FileUtil.getPsiDirectory(Context.project, filePath);
        String packageName = ClassUtil.getPackageName(daoDirectory);
        //search类名
        String searchClassName = ClassUtil.getSimpleSearchName(classInfo);

        String daoFile = "package " + packageName + ";\n" +
                "\n" +
                "import java.util.List;\n" +
                "import org.apache.ibatis.annotations.Param;\n" +
                "import " + ClassUtil.getFullSearchName(classInfo) + ";\n" +
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
                "    int count(@Param(\"search\") " + searchClassName + " search);\n" +
                "}";

        //生成PsiClass
        PsiElementFactory.SERVICE.getInstance(Context.project).createClassFromText(daoFile, daoDirectory);

        //创建文件
        FileUtil.createFile(fileName, filePath, daoFile);
    }
}
