package org.xialing.common.enums;

/**
 * @author leon
 * @version 1.0
 * @date 2019/9/29 11:02
 */
public enum AsyncInvokeTypeEnum {

    SYNC("0", "sync"),

    ASYNC("1","async"),

    ;

    private final String code;
    private final String description;

    /**
     * 私有构造函数
     *
     * @param code
     * @param description
     */
    AsyncInvokeTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * @return Returns the code.
     */
    public String getCode() {
        return code;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * 通过枚举<code>code</code>获得枚举
     *
     * @param code
     * @return
     */
    public static AsyncInvokeTypeEnum getByCode(String code) {
        for (AsyncInvokeTypeEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

}
