/**
 *
 */
package org.xialing.common.dto.base;

/**
 * @author xiaonian.yang
 *
 */
public class ResponseBean<T> extends AbsResponseBean<T> implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private boolean result;

    private T data;


    public ResponseBean() {

    }

    public ResponseBean(String code, String msg) {
        this.result = false;
        super.setErrorCode(code);
        super.setErrorMessage(msg);
    }

    public ResponseBean(T data, String code, String msg) {
        this.result = true;
        this.data = data;
        super.setErrorCode(code);
        super.setErrorMessage(msg);
    }

    public ResponseBean(boolean result, T data, String code, String msg) {
        this.result = result;
        this.data = data;
        super.setErrorCode(code);
        super.setErrorMessage(msg);
    }

    public ResponseBean(boolean result, T data) {
        this.result = result;
        this.data = data;
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

    public static <T> ResponseBean<T> error(String code, String msg) {
        return new ResponseBean<T>(code, msg);
    }

    public static <T> ResponseBean<T> error(T data, String code, String msg) {
        return new ResponseBean<T>(false, data, code, msg);
    }

    public static <T> ResponseBean<T> success(T data, String code, String msg) {
        return new ResponseBean<T>(true, data, code, msg);
    }

    public static <T> ResponseBean<T> success(String code, String msg) {
        return new ResponseBean<T>(true, null, code, msg);
    }
}
