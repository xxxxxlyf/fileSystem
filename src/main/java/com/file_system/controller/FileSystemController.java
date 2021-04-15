package com.file_system.controller;

import com.file_system.service.FileSystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Api("文件管理")
@RestController
@RequestMapping("/api/")
public class FileSystemController {

    @Autowired
    FileSystemService fileSystemService;

    @ApiOperation("启用文件组中的所有文件")
    @PutMapping("/updFileStatus")
    public Object updFileStatus(String fileGroup) {
        return fileSystemService.updFileStatus(fileGroup);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "files", value = "文件"),
            @ApiImplicitParam(name = "groupId", value = "文件组(替换文件时加上该参数，默认第一次上传不带该值)"),
            @ApiImplicitParam(name = "fileDesc", value = "文件描述"),
            @ApiImplicitParam(name = "emp", value = "工号"),
            @ApiImplicitParam(name = "status",value = "文件状态,默认为空，传值为avatar时，头像形式存储")
    })
    @ApiOperation("多文件上传(替换)")
    @PostMapping(value = "/uploadFiles", consumes = "multipart/form-data")
    public Object uploadFiles(HttpServletRequest request, @RequestParam("files") MultipartFile[] files, String groupId, String fileDesc, String emp,String status ) {
        return fileSystemService.uploadFiles(request,files, groupId, fileDesc, emp,status);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "files", value = "文件"),
            @ApiImplicitParam(name = "groupId", value = "文件组(必传)"),
            @ApiImplicitParam(name = "fileDesc", value = "文件描述"),
            @ApiImplicitParam(name = "emp", value = "工号"),
            @ApiImplicitParam(name = "status",value = "文件状态,默认为空，传值为avatar时，头像形式存储")
    })
    @ApiOperation("多文件上传(加入到同一个文件组)")
    @PostMapping(value = "/uploadFilesWithAdd", consumes = "multipart/form-data")
    public Object uploadFilesWithAdd(HttpServletRequest request, @RequestParam("files") MultipartFile[] files, String groupId, String fileDesc, String emp,String status) {
        return fileSystemService.uploadFilesWithAdd(request,files, groupId, fileDesc, emp,status);
    }


    @ApiOperation("查询文件组中所有文件的信息")
    @GetMapping("/getFileInfo")
    public Object getFileInfo(String groupId) {
        return fileSystemService.getFileInfo(groupId);
    }


    @ApiImplicitParams({
         @ApiImplicitParam(name = "fileId",value = "文件Id"),
         @ApiImplicitParam(name = "emp",value = "工号")
    })
    @ApiOperation("删除文件id所代表的的文件")
    @DeleteMapping("/delFileByFileId")
    public Object delFileByFileId(String fileId, String emp) {
        return fileSystemService.delFile(fileId, emp);
    }


    @ApiOperation("寫入文件的學分信息,内部方法調用")
    @GetMapping("/writeScorInfo")
    public Object writeScorInfo(String fileGroupId){
        return fileSystemService.writeScoreInfo(fileGroupId);
    }

    @GetMapping("/getScore")
    @ApiOperation("获得课程的学分")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId",value = "课程id"),
            @ApiImplicitParam(name = "category",value = "课程类别（online/offline）",defaultValue = "online")
    })
    public Object getScore(String courseId,String category){
        return fileSystemService.calculateCourseScore(courseId,category);
    }


}