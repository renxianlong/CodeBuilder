package com.kong.codebuilder.pojo;

import com.kong.codebuilder.ClassType;
import com.intellij.psi.PsiField;

public class FieldInfo {
    private String name;
    private String columnName;
    private ClassType type;
    private PsiField psiField;

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

    public PsiField getPsiField() {
        return psiField;
    }

    public void setPsiField(PsiField psiField) {
        this.psiField = psiField;
    }
}
