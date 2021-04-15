package com.file_system.pojo;

import lombok.Data;

@Data
public class CFileScore {

  private String rowId;
  private String category;
  private String fileType;
  private Integer limitMin;
  private Integer limitMax;
  private String unit;
  private float sore;
  private String createEmp;
  private java.util.Date createTime;

}
