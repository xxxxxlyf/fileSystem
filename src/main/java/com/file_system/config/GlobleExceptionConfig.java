package com.file_system.config;

import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 全局异常处理器
 * @author lyf
 */
@RestControllerAdvice
@ConditionalOnClass
public class GlobleExceptionConfig {

    /**
     * 处理上传文件超出大小的情形
     * @return
     */
    @ExceptionHandler(value = {MaxUploadSizeExceededException.class, FileUploadBase.SizeLimitExceededException.class})
    public String throwException(){
        return "文件大小超出最大可上传大小1G,上传文件失败！";
    }
}