package org.xialing.common.enums;


public enum ResponseCodeEnum {
    SUCCESS("000000", "请求处理成功"),

    PROCESSING("0000001", "请求已受理"),

    PROCESS_ERROR("000002", "请求受理失败"),

    PROCESS_CANCEL("000003", "请求受理撤销"),

    PROCESS_SUCCESS("000004", "请求受理成功"),

    MSG_PARSING_FAILURE("000005", "报文解析失败"),

    COMPRESS_FAILURE("000006", "报文压缩处理失败"),

    UNCOMPRESS_FAILURE("000007", "报文解压缩处理失败"),

    INVALID_PARAM("000008", "无效的请求参数"),

    VALID_TARGETSYSCODE_FAILURE("000009", "目标系统校验失败"),

    VALID_DATAMSGSIZE_FAILURE("000010", "请求报文内容长度校验失败"),

    UNDEFINED_SERVICE("000011", "请求服务代码未定义"),

    PARAM_VRFY_FAIL("000012", "参数校验未通过"),

    ENCRYP_MACHINE_HANDLE_FAILURE("000013", "加密机处理失败"),

    BUSI_ERROR("000014", "内部服务异常"),

    API_VERSION_ERROR("000015", "调用接口服务版本错误"),

    /**
     * token 过期
     **/
    TOKEN_TIMEOUT_CODE("000016", "token 过期"),
    /**
     * 禁止访问
     **/
    NO_AUTH_CODE("000017", "禁止访问"),

    VERIFY_SUCCESS("000018", "校验成功"),

    VERIFY_ERROR("000019", "校验失败"),

    REQUEST_DUPLICATE("000020","请求过于频繁"),

    DB_ERROR("999990", "数据库执行失败"),

    DB_BUSY("999991", "数据库执行忙"),

    REMOTE_CALL_FAILURE("999992", "远程调用失败"),

    UNDEFINED_ERROR("999993", "服务处理失败"),

    FAILURE("999999", "未知失败"),

    LOGIN_FAILURE("10001", "登录失败"),

    LOGOUT_FAILURE("10002", "注销失败"),

    WRONG_VERSION("10003", "请求版本非法"),

    VALIDATE_SIGN_EXCEPTION("10004", "数据验签异常"),

    VALIDATE_SIGN_FAIL("10005", "数据验签失败"),

    PARAM_CHECH_ERROR("10006","参数校验错误"),

    ORDER_STATUS_ERROR("10007","订单状态错误"),

    TRANSACTION_DUPLICATE("10008","请求重复"),

    CHANNEL_INVOKE_ERROR("10009","Baas渠道上链失败"),

    CHAIN_ORDER_NOT_FOUND("10010","链单未找到"),

    TRADE_ORDER_NOT_FOUND("10011","网关订单未找到"),

    ORG_IS_NOT_MATCH("10012","组织机构和合约背书组织不匹配"),

    CHANNEL_INVOKE_QUERY_ERROR("10013","Baas渠道上链结果查询失败"),

    CHANNEL_QUERY_ERROR("10014","Baas渠道上链结果查询失败"),

    REGISTER_EVENT_LISTENER_ERROR("10015","注册事件监听失败"),

    UNREGISTER_EVENT_LISTENER_ERROR("10015","取消事件监听失败"),
    ;
    private String code;
    private String desc;

    ResponseCodeEnum(String code, String desc) {

        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static com.uzigood.core.enums.ResponseCodeEnum getResponseCodeEnum(String value) {
        if (value != null) {
            for (com.uzigood.core.enums.ResponseCodeEnum nameEnum : values()) {
                if (nameEnum.getCode().equals(value)) {
                    return nameEnum;
                }
            }
        }
        return null;
    }

    public static boolean isResponseCodeEnum(String value) {
        if (value != null) {
            for (com.uzigood.core.enums.ResponseCodeEnum nameEnum : values()) {
                if (nameEnum.getCode().equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }


}
