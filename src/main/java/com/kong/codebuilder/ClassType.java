package com.kong.codebuilder;

/**
 * 类型
 */
public enum ClassType {
    STRING(0, "String"),
    DATE(1, "Date"),
    BIG_DECIMAL(2, "BigDecimal"),
    INTEGER(3, "Integer"),
    LONG(4, "Long"),
    SHORT(5, "Short"),
    DOUBLE(6, "Double"),
    FLOAT(7, "Float"),
    JAVA_SQL_TIMESTAMP(8, "Timestamp"),
    JAVA_SQL_Date(9, "Date"),
    BASIC_INT(10, "int"),
    BASIC_LONG(11, "long"),
    BASIC_SHORT(12, "short"),
    BASIC_DOUBLE(13, "double"),
    BASIC_FLOAT(14, "float"),
    BOOLEAN(15, "Boolean"),
    BASIC_BOOLEAN(16, "boolean"),
    NONE(-1, "none");

    private Integer code;
    private String desc;

    private ClassType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ClassType fromName(String name) {
        for (ClassType e : ClassType.values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return ClassType.NONE;
    }

    public static ClassType fromCode(Integer code) {
        for (ClassType e : ClassType.values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return ClassType.NONE;
    }

    public static ClassType fromDesc(String desc) {
        for (ClassType e : ClassType.values()) {
            if (e.getDesc().equalsIgnoreCase(desc)) {
                return e;
            }
        }
        return ClassType.NONE;
    }


}
