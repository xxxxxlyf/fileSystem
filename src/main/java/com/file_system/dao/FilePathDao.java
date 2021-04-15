package com.file_system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.file_system.pojo.CFilePathData;
import com.file_system.pojo.CFileScore;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface FilePathDao extends BaseMapper<CFilePathData> {

    /**
     * 更新文件的状态
     * @param fileGroupId 文件组id
     * @param status 文件状态
     * @return 影响行数
     */
    @Update("update C_FILE_PATH_DATA set IS_SUCCESS=#{status},CREATE_TIME=SYSDATE where FILE_GROUP_ID=#{fileGroupId}")
    int updFileStatus(String fileGroupId,String status);

    /**
     * 更新文件的删除标志
     * @param fileId 文件id
     * @return 影响行数
     */
    @Update("update C_FILE_PATH_DATA set IS_DELETE='Y', IS_SUCCESS='N'  where FILE_ID=#{fileId} ")
    int updFileDeleteStatus(String fileId);

    /**
     * 寫入文件的學分信息
     * @param scoreInfo 学分信息
     * @param unit 单位
     * @param groupId 文件组id
     * @return 影响行数
     */
    @Update("update C_FILE_PATH_DATA set FILE_UNIT=#{unit},SCORE_INFO=#{scoreInfo} where FILE_GROUP_ID=#{groupId}")
    int writeScoreInfo(Object scoreInfo,String unit,String groupId);

    /**
     * 根据文件组id查询文件详情
     * @param fileGroupId 文件组id
     * @return  文件详情
     */
    @Select("select * from C_FILE_PATH_DATA where FILE_GROUP_ID=#{fileGroupId} and IS_DELETE='N' and IS_SUCCESS='Y'")
    CFilePathData getFile(String fileGroupId);

    /**
     * 查询对应num值所在的学分区间
     * @param num 总数
     * @param fileType 文件类型（V/P）
     * @param category 课程类型（online/offline）
     * @return
     */
    @Select("select * from C_FILE_SCORE where LIMIT_MIN <=#{num} and LIMIT_MAX >#{num} and FILE_TYPE=#{fileType} and CATEGORY=#{category}")
    CFileScore getScore(Integer num,String fileType,String category);

    /**
     * 重写课程的学分信息
     * @param score 学分
     * @param courseId 课程id
     * @return 影响的行数
     */
    @Update("update R_ONLINE_CURRICULUM set SCORE=#{score} where ROW_ID=#{courseId}")
    int updScore(String score,String courseId);

}