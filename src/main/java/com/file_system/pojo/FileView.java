package com.file_system.pojo;

import lombok.Data;

import java.util.List;

@Data
public class FileView {

    private String groupId;
    private List<OneFileInfo> fileList;
}
