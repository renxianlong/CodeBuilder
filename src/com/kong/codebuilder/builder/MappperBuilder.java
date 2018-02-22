package com.kong.codebuilder.builder;

import com.google.common.base.CaseFormat;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiField;
import com.kong.codebuilder.loader.MapperBuilderConfigLoader;
import com.kong.codebuilder.pojo.ClassInfo;
import com.kong.codebuilder.pojo.FieldInfo;
import com.kong.codebuilder.util.ClassUtil;
import com.kong.codebuilder.util.FileUtil;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;

import java.util.List;

public class MappperBuilder {
    /**
     * 生成mapper
     */
    public static void generateMapper() {
        List<ClassInfo> classInfoList = MapperBuilderConfigLoader.getInstance().getClassInfoList();
        classInfoList.forEach(classInfo -> {
            generateMapper(classInfo);
        });
    }

    private static void generateMapper(ClassInfo classInfo) {
        //设置头相关信息
        Document document = new Document();
        DocType docType = new DocType("mapper",
                "-//mybatis.org//DTD Mapper 3.0//EN", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
        document.addContent(docType);

        //mapper节点
        Element mapper = new Element("mapper");
        mapper.setAttribute("namespace", classInfo.getClassFullName());
        document.addContent(mapper);

        //设置resultMap
        mapper.addContent(getResultMapElement(classInfo));

        //设置列名集合
        mapper.addContent(getColumnListElement(classInfo));

        //设置insert语句
        mapper.addContent(getInsertElement(classInfo));

        //设置insertList语句
        mapper.addContent(getBatchInsertElement(classInfo));

        //设置更新语句
        mapper.addContent(getUpdateElement(classInfo));

        //设置search语句
        mapper.addContent(getSearchElement(classInfo));

        //保存xml
        String fileName = classInfo.getPsiClass().getName() + ".xml";
        FileUtil.saveXml(fileName, MapperBuilderConfigLoader.getInstance().getMapperPath(), document);
    }

    private static Element getSearchElement(ClassInfo classInfo) {
        Element search = new Element("select");
        search.setAttribute("id", "search");

        Text select = new Text("SELECT");
        search.addContent(select);
        Element include = new Element("include");
        include.setAttribute("refid", "AllColumnList");
        search.addContent(include);

        Text from = new Text("FROM " + ClassUtil.generateTableName(classInfo));
        search.addContent(from);

        Element where = new Element("where");
        classInfo.getFieldList().forEach(fieldInfo -> {
            Element test = new Element("if");
            test.setAttribute("test", fieldInfo.getName() + "!=null");
            Text text = new Text(fieldInfo.getColumnName() + "=#{" + fieldInfo.getName() + "}");
            test.addContent(text);
            where.addContent(test);
        });

        search.addContent(where);
        return search;
    }

    /**
     * 获取更新元素
     *
     * @param classInfo
     * @return
     */
    private static Element getUpdateElement(ClassInfo classInfo) {
        Element update = new Element("update");
        update.setAttribute("id", "update");

        Text text = new Text("UPDATE " + ClassUtil.generateTableName(classInfo));
        update.addContent(text);

        Element set = new Element("set");
        classInfo.getFieldList().forEach(fieldInfo -> {
            Element test = new Element("if");
            test.setAttribute("test", fieldInfo.getName() + "!=null");
            Text textTemp = new Text(fieldInfo.getColumnName() + "=#{" + fieldInfo.getName() + "}");
            test.addContent(textTemp);
            set.addContent(test);
        });
        update.addContent(set);

        Text textWhere = new Text("WHERE `id` = #{id}");
        update.addContent(textWhere);
        return update;
    }

    /**
     * 获取批量插入元素
     *
     * @param classInfo
     * @return
     */
    private static Element getBatchInsertElement(ClassInfo classInfo) {
        String tableName = ClassUtil.generateTableName(classInfo);
        Element insert = new Element("insert");
        insert.setAttribute("id", "batchInsert");

        Text insertText = new Text("INSERT INTO " + tableName + "(");
        insert.addContent(insertText);

        Element include = new Element("include");
        include.setAttribute("refid", "AllColumnList");
        insert.addContent(include);

        Text values = new Text("VALUES");
        insert.addContent(values);

        Element foreach = new Element("foreach");
        foreach.setAttribute("collection", "pojoList");
        foreach.setAttribute("item", "pojo");
        foreach.setAttribute("index", "index");
        foreach.setAttribute("separator", ",");
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        boolean first = true;
        for (FieldInfo fieldInfo : classInfo.getFieldList()) {
            if (first) {
                builder.append("#{pojo." + fieldInfo.getName() + "}");
                first = false;
            } else {
                builder.append(", #{pojo." + fieldInfo.getName() + "}");
            }
        }
        builder.append(")");
        Text foreachText = new Text(builder.toString());
        foreach.addContent(foreachText);
        insert.addContent(foreach);
        return insert;
    }

    /**
     * 获取insert语句元素
     *
     * @param classInfo
     * @return
     */
    private static Element getInsertElement(ClassInfo classInfo) {
        String tableName = ClassUtil.generateTableName(classInfo);

        Element insert = new Element("insert");
        insert.setAttribute("id", "insert");

        insert.addContent(new Text("INSERT INTO " + tableName));

        Element trimColumn = new Element("trim");
        trimColumn.setAttribute("prefix", "(");
        trimColumn.setAttribute("suffix", ")");
        trimColumn.setAttribute("suffixOverrides", ",");

        for (FieldInfo field : classInfo.getFieldList()) {
            Element test = new Element("if");
            test.setAttribute("test", field.getName() + "!=null");
            test.setText(field.getColumnName() + ",");
            trimColumn.addContent(test);
        }
        insert.addContent(trimColumn);

        insert.addContent(new Text("VALUES"));

        Element trimValue = new Element("trim");
        trimValue.setAttribute("prefix", "(");
        trimValue.setAttribute("suffix", ")");
        trimValue.setAttribute("suffixOverrides", ",");

        for (FieldInfo field : classInfo.getFieldList()) {
            Element test = new Element("if");
            test.setAttribute("test", field.getName() + "!=null");
            test.setText("#{" + field.getName() + "}" + ",");
            trimValue.addContent(test);
        }

        insert.addContent(trimValue);
        return insert;
    }

    /**
     * 获取类名集合
     *
     * @param classInfo
     */
    private static Element getResultMapElement(ClassInfo classInfo) {
        Element resultMap = new Element("resultMap");
        resultMap.setAttribute("id", "BaseResultMap");
        resultMap.setAttribute("type", classInfo.getClassFullName());

        PsiClass psiClass = classInfo.getPsiClass();
        PsiField[] psiFieldList = psiClass.getAllFields();
        for (PsiField psiField : psiFieldList) {
            String name = psiField.getNameIdentifier().getText();
            String underScoreName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
            Element result = new Element("result");
            result.setAttribute("column", underScoreName);
            result.setAttribute("property", name);
            resultMap.addContent(result);
        }

        return resultMap;
    }

    /**
     * 获取列名集合
     *
     * @param classInfo
     * @return
     */
    private static Element getColumnListElement(ClassInfo classInfo) {
        Element allColumnList = new Element("sql");
        allColumnList.setAttribute("id", "AllColumnList");
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (PsiField psiField : classInfo.getPsiClass().getAllFields()) {
            String name = psiField.getNameIdentifier().getText();
            String formatName = "`" + name + "`";
            if (first) {
                builder.append(formatName);
                first = false;
            } else {
                builder.append("," + formatName);
            }
        }
        allColumnList.setText(builder.toString());
        return allColumnList;
    }
}
