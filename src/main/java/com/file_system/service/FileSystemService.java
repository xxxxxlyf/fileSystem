package com.file_system.service;

import com.file_system.pojo.returnMes.JsonData;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SAAJResult;

public interface FileSystemService {

    /**
     * 上传文件
     * @param files 文件
     * @param groupId 文件组id
     * @param emp 操作人员工号
     * @return
     */
    JsonData uploadFiles(HttpServletRequest request, MultipartFile[] files, String groupId, String fileDesc, String emp,@Nullable String status);

    /**
     * 多文件上传实现加到同一个文件组里头
     * @return
     */
    JsonData uploadFilesWithAdd(HttpServletRequest request, MultipartFile[] files, String groupId, String fileDesc, String emp,@Nullable String status);

    /***
     * 查询文件组中的所有文件信息
     * @param groupId
     * @return
     */
    JsonData getFileInfo(String groupId);

    /**
     * 根據文件id刪除文件組中的數據
     * @param fileId 文件id
     * @param emp 工号
     * @return
     */
    JsonData delFile(String fileId,String emp);

    /**
     * check文件组的使用状态
     * @param fileGroup
     * @return
     */
    JsonData updFileStatus(String fileGroup);

    /**
     * 根據算法獲得上傳文件的分數
     * @param groupId 文件組id
     * @return
     */
    JsonData getFileScore(String groupId);

    /**
     * 寫入文件的學分信息
     * @param fileInfo
     * @return
     */
    JsonData writeScoreInfo(String fileInfo);

    /**
     * 根據算法計算課程的學分
     * @param courseId 課程id
     * @param category  课程类别
     * @return
     */
    JsonData calculateCourseScore(String courseId,String category);

    /**
     * 多文件上傳
     * @param request 請求
     * @param files 文件列表
     * @param groupId 文件組id
     * @param fileDesc 文件描述
     * @param emp 操作人工號
     * @param status 操作文件狀態（新增，替換，）
     * @param changeType 是否需要將PPT轉換成PDF
     * @return
     */
    //sonData uploadFiles(HttpServletRequest request, MultipartFile[] files, String groupId, String fileDesc, String emp,@Nullable String status,@Nullable String changeType) throws  Exception;

}
