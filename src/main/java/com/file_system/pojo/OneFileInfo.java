package com.file_system.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@ApiModel("文件信息")
@AllArgsConstructor
@NoArgsConstructor
public class OneFileInfo {
    @ApiModelProperty("文件id")
    private String fileId;
    @ApiModelProperty("文件路径")
    private String filePath;
}
