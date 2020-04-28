package org.xialing.common.dto.base;

import java.io.Serializable;

public class RequestBean<T> extends AbsRequestBean<T> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8719244596842350606L;
    private T data;

    public RequestBean() {
        super();
    }

    public RequestBean(T data) {
        super();
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}