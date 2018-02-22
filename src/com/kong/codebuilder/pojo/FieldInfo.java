package com.kong.codebuilder.pojo;

import com.kong.codebuilder.ClassType;

public class FieldInfo {
    private String name;
    private String columnName;
    private ClassType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public ClassType getType() {
        return type;
    }

    public void setType(ClassType type) {
        this.type = type;
    }
}
