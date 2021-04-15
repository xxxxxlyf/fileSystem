package com.file_system.pojo.returnMes;

/**
 * 错误枚举码
 */
public enum  Code {

    INFO_SUCCEED(0,"succeed"),
    INFO_ERROR(-1,"其它错误"),
    INFO_ERROR_SQL(1000,"数据库错误"),
    INFO_ERROR_400(400,"请求参数错误"),
    INFO_ERROR_404(404,"请求地址未找到"),
    INFO_ERROR_500(500,"内部服务器错误");




    private int code;
    private String value;
    public int getCode() {
        return code;
    }
    public String getValue() {
        return value;
    }

    private Code(int code,String value) {
        this.code=code;
        this.value=value;
    }
}
