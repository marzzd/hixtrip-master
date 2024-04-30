package com.hixtrip.sample.client;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 返回结果
 **/
@Getter
@Setter
public class ResultVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -1L;
    public Object data;
    public String msg;
    public int code;

    public boolean isSuccess() {
        return code == 0;
    }


    public static ResultVO success() {
        ResultVO result = new ResultVO();
        result.setCode(0);
        return result;
    }

    public static <D> ResultVO success(D d) {
        ResultVO result = new ResultVO();
        result.setCode(0);
        result.setData(d);
        return result;
    }

    public static <D> ResultVO success(String msg, D d) {
        ResultVO result = success(d);
        result.setMsg(msg);
        return result;
    }

    public static ResultVO fail(String msg) {
        ResultVO result = new ResultVO();
        result.setCode(-1);
        result.setMsg(msg);
        return result;
    }

    public static <D> ResultVO fail(String msg, D d) {
        ResultVO result = new ResultVO();
        result.setCode(-1);
        result.setMsg(msg);
        result.setData(d);
        return result;
    }
}