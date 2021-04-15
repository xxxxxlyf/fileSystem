package com.file_system.pojo.returnMes;

/**
 * 返回数据模板
 */
public class JsonData {
    private int code;
    private String msg;
    private Object data;

    public JsonData() {}
    public JsonData(Object data) {
        code= Code.INFO_SUCCEED.getCode();
        msg=Code.INFO_SUCCEED.getValue();
        this.data=data;
    }
    public JsonData(Code code,String msg,Object data) {
        this.code=code.getCode();
        this.msg=msg;
        this.data=data;
    }
    public JsonData(Code code,Object data) {
        this.code=code.getCode();
        this.msg=code.getValue();
        this.data=data;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
}
