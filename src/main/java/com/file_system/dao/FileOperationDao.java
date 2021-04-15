package com.file_system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.file_system.pojo.FileOperationLog;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


public interface FileOperationDao extends BaseMapper<FileOperationLog> {

    @Select("select GET_ROW_ID from dual")
    String getNextRowId();

}
