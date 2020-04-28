package org.xialing.common.dto;

import org.xialing.common.constants.Constant;
import lombok.Data;
import org.xialing.common.dto.base.ResponseBean;

import java.io.Serializable;

/**
 * 响应对象
 *
 * @author jack
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = -3355484205126156607L;

    private boolean result;

    private T data;

    private String code;

    private String msg;

    public R() {

    }

    public R(ResponseBean<T> responseBean) {
        this.result = true;
        this.code = responseBean.getErrorCode();
        this.msg = responseBean.getErrorMessage();
    }

    public R(String code, String msg) {
        this.result = true;
        this.code = code;
        this.msg = msg;
    }

    public R(T data, String code, String msg) {
        this.result = true;
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public R(boolean result, T data, String code, String msg) {
        this.result = result;
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public R(boolean result, T data) {
        this.result = result;
        this.data = data;
    }

    public R(String msg) {
        this.result = true;
        this.msg = msg;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static <T> R<T> error(String code, String msg) {
        return new R<>(code, msg);
    }

    public static <T> R<T> error(T data, String code, String msg) {
        return new R<>(false, data, code, msg);
    }
    public static R error(String s) {
        return new R<>(Constant.FAILURE, s);
    }



    public static <T> R<T> success(T data, String msg) {
        return new R<>(data, Constant.SUCCESS, msg);
    }
    public static <T> R<T> success(T data) {
        return new R<>(data, Constant.SUCCESS, "success");
    }

    public static <T> R<T> successSimple(ResponseBean<T> responseBean) {
        return new R<T>(responseBean);
    }

    public static R error() {
        return new R<>(Constant.FAILURE, "未知异常，请联系管理员");
    }

    public static R success() {
        return new R<>(null, Constant.SUCCESS, "success");
    }
}