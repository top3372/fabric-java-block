package com.ideal.blockchain.dto.response;

import com.ideal.blockchain.enums.ResponseCodeEnum;

public class ResultInfo<T>  implements java.io.Serializable{

    private static final long serialVersionUID = 1L;

    private String code;

    private T data;

    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultInfo(){
        
    }

    public ResultInfo(ResponseCodeEnum responseCodeEnum){
        this.code = responseCodeEnum.getCode();
        this.msg = responseCodeEnum.getDesc();
    }

    public ResultInfo(ResponseCodeEnum responseCodeEnum,T data){
        this.data = data;
        this.code = responseCodeEnum.getCode();
        this.msg = responseCodeEnum.getDesc();
    }

    public ResultInfo(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public ResultInfo(T data,String code, String msg){
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public static <T> ResultInfo<T> error(String code, String msg){
        return new ResultInfo<T>(code,msg);
    }

    public static <T> ResultInfo<T> error(ResponseCodeEnum responseCodeEnum){
        return new ResultInfo<T>(responseCodeEnum);
    }

    public static <T> ResultInfo<T> error(T data,String code, String msg){
        return new ResultInfo<T>(data,code,msg);
    }

    public static <T> ResultInfo<T> success(T data, String msg){
        return new ResultInfo<T>(data, ResponseCodeEnum.SUCCESS.getCode(),msg);
    }

    public static <T> ResultInfo<T> error(T data,ResponseCodeEnum responseCodeEnum){
        return new ResultInfo<T>(responseCodeEnum,data);
    }

    public static <T> ResultInfo<T> success(T data,ResponseCodeEnum responseCodeEnum){
        return new ResultInfo<T>(responseCodeEnum,data);
    }
}
