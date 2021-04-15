package com.file_system.pojo;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@Data
@ApiModel("文件操作记录")
@TableName("FILE_OPERATION_LOG")
public class FileOperationLog {

  @TableId
  @ApiModelProperty("行标")
  private String rowId;
  @ApiModelProperty("文件id")
  private String fileId;
  @ApiModelProperty("操作描述")
  private String operationDesc;
  @ApiModelProperty("操作类型")
  private String operationType;
  @ApiModelProperty("创建人")
  private String createEmp;
  @ApiModelProperty("创建时间")
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private Date createTime;

}
