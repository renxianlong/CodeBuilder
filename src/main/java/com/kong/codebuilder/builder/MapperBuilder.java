package com.kong.codebuilder.builder;

import com.google.common.base.CaseFormat;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.kong.codebuilder.common.Context;
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

public class MapperBuilder {
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
        mapper.setAttribute("namespace", Context.getAttribute("DAO_FULL_NAME").toString());
        document.addContent(mapper);

        //设置resultMap
        mapper.addContent(getResultMapElement(classInfo));

        //设置列名集合
        mapper.addContent(getColumnListElement(classInfo));

        //设置insert语句
        mapper.addContent(getInsertElement(classInfo));

        //设置insertList语句
        mapper.addContent(getBatchInsertElement(classInfo));

        //设置delete语句
        mapper.addContent(getDeleteElement(classInfo));

        //设置更新语句
        mapper.addContent(getUpdateElement(classInfo));

        //设置getById语句
        mapper.addContent(getByIdElement(classInfo));

        //设置search语句
        mapper.addContent(getSearchElement(classInfo));

        //设置search语句
        mapper.addContent(getCountElement(classInfo));

        //保存xml
        String fileName = classInfo.getPsiClass().getName() + "Dao.xml";
        FileUtil.saveXml(fileName, MapperBuilderConfigLoader.getInstance().getMapperPath(), document);

        ApplicationManager.getApplication().saveAll();
        VirtualFileManager.getInstance().syncRefresh();
    }

    private static Element getByIdElement(ClassInfo classInfo) {
        Element getById = new Element("select");
        getById.setAttribute("id", "getById");

        Text select = new Text("SELECT");
        getById.addContent(select);
        Element include = new Element("include");
        include.setAttribute("refid", "AllColumnList");
        getById.addContent(include);

        Text from = new Text("FROM " + ClassUtil.generateTableName(classInfo) + " WHERE id=#{id}");
        getById.addContent(from);

        return getById;
    }

    private static Element getDeleteElement(ClassInfo classInfo) {
        boolean softDelete = false;
        for (FieldInfo fieldInfo : classInfo.getFieldList()) {
            if (fieldInfo.getName().endsWith("delete")) {
                softDelete = true;
            }
        }

        Element delete = new Element("delete");
        delete.setAttribute("id", "delete");
        if (softDelete) {
            Text deleteSql = new Text("UPDATE " + ClassUtil.generateTableName(classInfo) + " SET `delete` = 1 WHERE `id`=#{id}");
            delete.addContent(deleteSql);
        } else {
            Text deleteSql = new Text("DELETE FROM " + ClassUtil.generateTableName(classInfo) + " WHERE `id`=#{id}");
            delete.addContent(deleteSql);
        }
        return delete;
    }

    private static Element getSearchElement(ClassInfo classInfo) {
        Element search = new Element("select");
        search.setAttribute("id", "search");
        search.setAttribute("resultMap", "BaseResultMap");

        Text select = new Text("SELECT");
        search.addContent(select);
        Element include = new Element("include");
        include.setAttribute("refid", "AllColumnList");
        search.addContent(include);

        Text from = new Text("FROM " + ClassUtil.generateTableName(classInfo));
        search.addContent(from);

        Element where = new Element("where");
        for (FieldInfo fieldInfo : classInfo.getSearchClass().getFieldList()) {
            if (fieldInfo.getName().equals("orderBy")
                    || fieldInfo.getName().equals("limit")
                    || fieldInfo.getName().equals("page")
                    || fieldInfo.getName().equals("offset")
                    || fieldInfo.getName().startsWith("null")) {
                continue;
            }

            if (fieldInfo.getName().equals("beginTime")) {
                Element test = new Element("if");
                test.setAttribute("test", fieldInfo.getName() + "!=null");
                Text text = new Text("AND " + "`create_time` >= #{" + fieldInfo.getName() + "}");
                test.addContent(text);
                where.addContent(test);
                continue;
            }

            if (fieldInfo.getName().equals("endTime")) {
                Element test = new Element("if");
                test.setAttribute("test", fieldInfo.getName() + "!=null");
                Text text = new Text("AND " + "`create_time` <= #{" + fieldInfo.getName() + "}");
                test.addContent(text);
                where.addContent(test);
                continue;
            }

            Element test = new Element("if");
            test.setAttribute("test", fieldInfo.getName() + "!=null");
            Text text = new Text("AND " + fieldInfo.getColumnName() + "=#{" + fieldInfo.getName() + "}");
            test.addContent(text);
            where.addContent(test);
        }

        search.addContent(where);
        //设置order by
        classInfo.getSearchClass().getFieldList().forEach(fieldInfo -> {
            if (fieldInfo.getName().equals("orderBy")) {
                Element test = new Element("if");
                test.setAttribute("test", fieldInfo.getName() + "!=null");
                Text text = new Text("ORDER BY #{" + fieldInfo.getName() + "}");
                test.addContent(text);
                search.addContent(test);
            }
        });

        //设置limit
        classInfo.getSearchClass().getFieldList().forEach(fieldInfo -> {
            if (fieldInfo.getName().equals("limit")) {
                Element test = new Element("if");
                test.setAttribute("test", fieldInfo.getName() + "!=null");
                Text text = new Text("LIMIT #{" + fieldInfo.getName() + "}");
                test.addContent(text);
                search.addContent(test);
            }
        });

        //设置offset
        classInfo.getSearchClass().getFieldList().forEach(fieldInfo -> {
            if (fieldInfo.getName().equals("offset")) {
                Element test = new Element("if");
                test.setAttribute("test", fieldInfo.getName() + "!=null");
                Text text = new Text("OFFSET #{" + fieldInfo.getName() + "}");
                test.addContent(text);
                search.addContent(test);
            }
        });

        return search;
    }

    private static Element getCountElement(ClassInfo classInfo) {
        Element search = new Element("select");
        search.setAttribute("id", "count");

        Text select = new Text("SELECT COUNT(*) FROM " + ClassUtil.generateTableName(classInfo));
        search.addContent(select);

        Element where = new Element("where");
        for (FieldInfo fieldInfo : classInfo.getSearchClass().getFieldList()) {
            if (fieldInfo.getName().equals("orderBy")
                    || fieldInfo.getName().equals("limit")
                    || fieldInfo.getName().equals("page")
                    || fieldInfo.getName().equals("offset")
                    || fieldInfo.getName().startsWith("null")) {
                continue;
            }

            if (fieldInfo.getName().equals("beginTime")) {
                Element test = new Element("if");
                test.setAttribute("test", fieldInfo.getName() + "!=null");
                Text text = new Text("AND " + "`create_time` >= #{" + fieldInfo.getName() + "}");
                test.addContent(text);
                where.addContent(test);
                continue;
            }

            if (fieldInfo.getName().equals("endTime")) {
                Element test = new Element("if");
                test.setAttribute("test", fieldInfo.getName() + "!=null");
                Text text = new Text("AND " + "`create_time` <= #{" + fieldInfo.getName() + "}");
                test.addContent(text);
                where.addContent(test);
                continue;
            }

            Element test = new Element("if");
            test.setAttribute("test", fieldInfo.getName() + "!=null");
            Text text = new Text("AND " + fieldInfo.getColumnName() + "=#{" + fieldInfo.getName() + "}");
            test.addContent(text);
            where.addContent(test);
        }

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
        for (FieldInfo fieldInfo : classInfo.getFieldList()) {
            if (fieldInfo.getName().equals("id")) {
                continue;
            }
            Element test = new Element("if");
            test.setAttribute("test", fieldInfo.getName() + "!=null");
            Text textTemp = new Text(fieldInfo.getColumnName() + "=#{" + fieldInfo.getName() + "} ,");
            test.addContent(textTemp);
            set.addContent(test);
        }
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
        insert.setAttribute("useGeneratedKeys", "true");
        insert.setAttribute("keyProperty", "id");

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
                builder.append("#{" + fieldInfo.getName() + "}");
                first = false;
            } else {
                builder.append(", #{" + fieldInfo.getName() + "}");
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
        insert.setAttribute("useGeneratedKeys", "true");
        insert.setAttribute("keyProperty", "id");

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

        for (FieldInfo psiField : classInfo.getFieldList()) {
            String name = psiField.getPsiField().getNameIdentifier().getText();
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
        for (FieldInfo fieldInfo : classInfo.getFieldList()) {
            if (first) {
                builder.append(fieldInfo.getColumnName());
                first = false;
            } else {
                builder.append("," + fieldInfo.getColumnName());
            }
        }
        allColumnList.setText(builder.toString());
        return allColumnList;
    }
}