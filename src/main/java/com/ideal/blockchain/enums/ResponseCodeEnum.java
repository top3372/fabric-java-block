package com.ideal.blockchain.enums;





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
	
	SIGN_VERIFY_FAILURE("000015", "验证签名失败"),
	
	IP_VERIFY_FAILURE("000016", "IP地址验证失败"),
	
	FAILURE("999996","失败"),
	
	REMOTE_CALL_FAILURE("999997", "远程调用失败"),

	DB_ERROR("999998", "数据库执行失败"),

	UNDEFINED_ERROR("999999", "服务处理失败"),
	

	
	
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

	public static ResponseCodeEnum getResponseCodeEnum(String value) {
		if (value != null) {
			for (ResponseCodeEnum nameEnum : values()) {
				if (nameEnum.getCode().equals(value)) {
					return nameEnum;
				}
			}
		}
		return null;
	}

	public static boolean isResponseCodeEnum(String value) {
		if (value != null) {
			for (ResponseCodeEnum nameEnum : values()) {
				if (nameEnum.getCode().equals(value)) {
					return true;
				}
			}
		}
		return false;
	}
	

}
