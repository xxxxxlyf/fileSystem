package com.file_system.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("文件路径表")
@TableName("C_FILE_PATH_DATA")
@Data
public class CFilePathData {

  @TableId
  @ApiModelProperty("行标")
  private String rowId;
  @ApiModelProperty("文件组id")
  private String fileGroupId;
  @ApiModelProperty("文件id")
  private String fileId;
  @ApiModelProperty("文件名")
  private String fileName;
  @ApiModelProperty("文件版本")
  private String fileVersion;
  @ApiModelProperty("文件拓展名")
  private String fileExpandedName;
  @ApiModelProperty("文件路径")
  private String filePath;
  @ApiModelProperty("文件大小")
  private String fileSize;
  @ApiModelProperty("文件描述")
  private String fileDesc;
  @ApiModelProperty("文件是否删除")
  private String isDelete;
  @ApiModelProperty("文件是否启用")
  private String isSuccess;
  @ApiModelProperty("备用")
  private String data1;
  @ApiModelProperty("备用")
  private String data2;
  @ApiModelProperty("备用")
  private String data3;
  @ApiModelProperty("创建人")
  private String createEmp;
  @ApiModelProperty("创建时间")
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private Date createTime;
  @ApiModelProperty("课件单位（分/页）")
  private String fileUnit;
  @ApiModelProperty("学分信息")
  private Integer scoreInfo;

}
