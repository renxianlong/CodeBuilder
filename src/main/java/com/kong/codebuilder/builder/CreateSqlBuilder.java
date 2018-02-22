package com.kong.codebuilder.builder;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.kong.codebuilder.ClassType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.impl.source.javadoc.PsiDocCommentImpl;
import com.intellij.psi.impl.source.tree.PsiCommentImpl;
import com.kong.codebuilder.loader.MapperBuilderConfigLoader;
import com.kong.codebuilder.pojo.ClassInfo;
import com.kong.codebuilder.pojo.FieldInfo;
import com.kong.codebuilder.util.ClassUtil;
import com.kong.codebuilder.util.DateUtil;
import com.kong.codebuilder.util.FileUtil;
import com.kong.codebuilder.util.GenCodeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 建表语句生成类
 */
public class CreateSqlBuilder {
    /**
     * 生成建表语句
     */
    public static void generateCreateSql() {
        List<ClassInfo> classInfoList = MapperBuilderConfigLoader.getInstance().getClassInfoList();
        classInfoList.forEach(classInfo -> {
            generateCreateSql(classInfo);
        });
    }

    /**
     * 生成建表语句
     *
     * @param classInfo
     */
    private static void generateCreateSql(ClassInfo classInfo) {
        List<String> text = new LinkedList<>();
        String tableName = ClassUtil.generateTableName(classInfo);
        text.add(String.format("-- auto Generated on %s ", DateUtil.formatLong(new Date())));
        text.add("-- DROP TABLE IF EXISTS `" + tableName + "`; ");
        text.add("CREATE TABLE " + tableName + "(");
        for (FieldInfo field : classInfo.getFieldList()) {
            String fieldSql = genfieldSql(field.getPsiField());
            text.add(fieldSql);
        }

        text.add(GenCodeUtil.ONE_RETRACT + "PRIMARY KEY (`id`)");
        text.add(String.format(")ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '%s';", getClassComment(classInfo.getPsiClass())));

        String fileName = classInfo.getPsiClass().getNameIdentifier().getText() + ".sql";
        //写文件
        FileUtil.createFile(fileName, MapperBuilderConfigLoader.getInstance().getCreateSqlPath(), text);

        ApplicationManager.getApplication().saveAll();
        VirtualFileManager.getInstance().syncRefresh();
    }

    private static Object getClassComment(PsiClass clazzPsi) {
        if (clazzPsi == null) {
            return StringUtils.EMPTY;
        }

        if (clazzPsi.getDocComment() != null) {
            return formatText(clazzPsi.getDocComment().getText());
        }

        return StringUtils.EMPTY;
    }


    private static String genfieldSql(PsiField field) {
        StringBuilder ret = new StringBuilder();
        if (field.getName().equalsIgnoreCase("lastUpdate")) {
            ret.append(GenCodeUtil.ONE_RETRACT)
                    .append("`last_update` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '" + getFieldComment(field) + "',");
            return ret.toString();
        }
        if (field.getName().equalsIgnoreCase("updateTime")) {
            ret.append(GenCodeUtil.ONE_RETRACT)
                    .append("`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '" + getFieldComment(field) + "',");
            return ret.toString();
        }

        if (field.getName().equalsIgnoreCase("createTime")) {
            ret.append(GenCodeUtil.ONE_RETRACT)
                    .append("`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '" + getFieldComment(field) + "',");
            return ret.toString();
        }

        if (field.getName().equals("id")) {
            ret.append(GenCodeUtil.ONE_RETRACT).
                    append("`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '" + getFieldComment(field) + "',");
            return ret.toString();
        }

        String filedClassDefault = getDefaultField(field);
        ret.append(GenCodeUtil.ONE_RETRACT).append("`").append(GenCodeUtil.getUnderScore(field.getName())).append("` ")
                .append(filedClassDefault).append(" COMMENT '").append(getFieldComment(field)).append("',");
        return ret.toString();
    }

    /**
     * 获取注释
     *
     * @param psiField
     * @return
     */
    private static String getFieldComment(PsiField psiField) {
        switch (psiField.getName()) {
            case "lastUpdate":
                return "最后更新时间";
            case "updateTime":
                return "更新时间";
            case "createTime":
                return "创建时间";
            case "id":
                return "主键";
            default:
                return parseComment(psiField);
        }
    }

    /**
     * 根据psiField获取comment
     *
     * @param field
     * @return
     */
    private static String parseComment(PsiField field) {
        if (field == null) {
            return StringUtils.EMPTY;
        }
        PsiElement[] children = field.getChildren();
        for (PsiElement child : children) {
            String text = child.getText();
            if (child instanceof PsiDocCommentImpl || child instanceof PsiCommentImpl) {
                return formatText(text);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 格式化注释
     *
     * @param text
     * @return
     */
    private static String formatText(String text) {
        text = text.replace("/*", "");
        text = text.replace("*/", "");
        text = text.replace("//", "");
        text = text.replace("\n", "");
        text = text.replace("*", "");
        text = text.trim();
        return text;
    }

    private static String getDefaultField(PsiField field) {
        String fieldText = field.getType().getCanonicalText();
        fieldText = fieldText.substring(fieldText.lastIndexOf(".") + 1);
        ClassType fieldClass = ClassType.fromDesc(fieldText);
        switch (fieldClass) {
            case STRING:
                return "VARCHAR(50) NOT NULL DEFAULT ''";
            case BASIC_INT:
            case INTEGER:
                return "INTEGER(12) NOT NULL DEFAULT -1";
            case SHORT:
            case BASIC_SHORT:
                return "TINYINT NOT NULL DEFAULT -1";
            case DATE:
            case JAVA_SQL_Date:
            case JAVA_SQL_TIMESTAMP:
                return "DATETIME NOT NULL DEFAULT '1000-01-01 00:00:00'";
            case LONG:
            case BASIC_LONG:
                return "BIGINT NOT NULL DEFAULT -1";
            case BIG_DECIMAL:
                return "DECIMAL(14,4) NOT NULL DEFAULT 0";
            case DOUBLE:
            case BASIC_DOUBLE:
                return "DECIMAL(14,4) NOT NULL DEFAULT 0";
            case FLOAT:
            case BASIC_FLOAT:
                return "DECIMAL(14,4) NOT NULL DEFAULT 0";
            default:
                throw new RuntimeException("unSupport field type :" + field.getType().getCanonicalText());
        }
    }
}