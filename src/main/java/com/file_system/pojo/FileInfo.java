package com.file_system.pojo;

import lombok.Data;

/**
 * 文件信息
 */
@Data
public class FileInfo {

    /**
     * 單位
     */
    public String unit;
    /**
     * 文件時長、文件頁數
     */
    public Object num;
}
