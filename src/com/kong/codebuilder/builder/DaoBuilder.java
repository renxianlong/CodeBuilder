package com.kong.codebuilder.builder;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.kong.codebuilder.loader.MapperBuilderConfigLoader;
import com.kong.codebuilder.pojo.ClassInfo;
import com.kong.codebuilder.util.FileUtil;
import org.jetbrains.annotations.NotNull;

public class DaoBuilder {

    /**
     * 生成Dao
     */
    public static void createDao() {
        ClassInfo baseSearch = MapperBuilderConfigLoader.getInstance().getBaseSearch();
        Project project = MapperBuilderConfigLoader.getInstance().getProject();
        if (null != baseSearch) {
            MapperBuilderConfigLoader.getInstance().getClassInfoList().forEach(classInfo -> {
                new WriteCommandAction(project) {
                    @Override
                    protected void run(@NotNull Result result) throws Throwable {
                        createDao(classInfo);
                    }
                }.execute();
            });
        }
    }

    private static void createDao(ClassInfo classInfo) {
        Project project = MapperBuilderConfigLoader.getInstance().getProject();
//        PsiElementFactory psiElementFactory = PsiElementFactory.SERVICE.getInstance(project);

        //获取类名
//        String pojoName = classInfo.getPsiClass().getNameIdentifier().getText();
        String className = classInfo.getPsiClass().getNameIdentifier().getText() + "Dao";
//
//        //生成PsiClass
//        PsiDirectory directory = MapperBuilderConfigLoader.getInstance().getDaoDirectory();
//        PsiClass clazz = JavaDirectoryService.getInstance().createInterface(directory, className);
//
//        //设置包名
//        ((PsiJavaFile) clazz.getContainingFile()).setPackageName(ClassUtil.getPackageName(directory));
//
//        //设置import
////        ((PsiJavaFile) clazz.getContainingFile())
////                .add(psiElementFactory.createImportStatement(psiElementFactory
////                        .createClass("org.apache.ibatis.annotations.Param")));
////        ((PsiJavaFile) clazz.getContainingFile())
////                .add(psiElementFactory.createImportStatement(psiElementFactory
////                        .createClass("java.util.List")));
////        ((PsiJavaFile) clazz.getContainingFile())
////                .add(psiElementFactory.createImport(psiElementFactory
////                        .createClass("Complaint"), "com.zhouhua.cuanju.po.user.Complaint"));
//
//        String insertMethod = "int insert(@Param(\"pojo\") "+ pojoName +" pojo);\n";
//        clazz.add(psiElementFactory.createMethodFromText(insertMethod, clazz));
//
//        String insertListMethod = "int insertList(@Param(\"pojos\") List<" + pojoName + "> pojo);\n";
//        clazz.add(psiElementFactory.createMethodFromText(insertListMethod, clazz));
//
//        //保存所有更改
//        MapperBuilderConfigLoader.getInstance().getProject().save();
//        VirtualFileManager.getInstance().syncRefresh();

        String daoFile = "package com.zhouhua.cuanju.dao;\n" +
                "\n" +
                "import org.apache.ibatis.annotations.Param;\n" +
                "import java.util.List;\n" +
                "import com.zhouhua.cuanju.po.user.Complaint;\n" +
                "\n" +
                "public interface ComplaintDao {\n" +
                "\n" +
                "    int insert(@Param(\"pojo\") Complaint pojo);\n" +
                "\n" +
                "    int insertList(@Param(\"pojos\") List< Complaint> pojo);\n" +
                "\n" +
                "    List<Complaint> select(@Param(\"pojo\") Complaint pojo);\n" +
                "\n" +
                "    int update(@Param(\"pojo\") Complaint pojo);\n" +
                "\n" +
                "}";

        //创建文件
        FileUtil.createFile(className + ".java"
                , MapperBuilderConfigLoader.getInstance().getDaoPath(), daoFile);

        //保存所有更改
        MapperBuilderConfigLoader.getInstance().getProject().save();
        VirtualFileManager.getInstance().syncRefresh();
    }
}
