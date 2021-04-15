package com.file_system.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.file_system.dao.FileOperationDao;
import com.file_system.dao.FilePathDao;
import com.file_system.pojo.CFilePathData;
import com.file_system.pojo.FileOperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 每日中午12點删除不启用的文件
 */
@Component
@Configurable
@EnableScheduling
public class DeleteFilePerDay {

    @Autowired
    FilePathDao filePathDao;
    @Autowired
    FileOperationDao fileOperationDao;

    @Scheduled(cron = "0 0 12 * * ?")
    public void deleteGarbishFiles(){
        long startTime=System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName()+" is deleting the useless files");
        int count=0;
        try {
            //查詢所有不啟用文件信息
            List<CFilePathData> pathData = filePathDao.selectList(new QueryWrapper<CFilePathData>().eq("IS_SUCCESS", "N").eq("IS_DELETE","N"));
            //存在不啟用的文件時，進行刪除，並記錄
            if(pathData.size()>0){
                for (CFilePathData file : pathData) {
                    //文件id
                    String fileId=file.getFileId();
                    //獲得文件的地址
                    String path=file.getFilePath();
                    Path filePath= Paths.get("../webapps"+path);
                    File f = filePath.toFile();
                    //当文件存在时,删除文件
                    boolean deleleFalg=false;
                    if(f.exists()){
                        deleleFalg= f.delete();
                    }else{
                        //文件不存在時更新當前文件的狀態
                        filePathDao.updFileDeleteStatus(fileId);
                        System.out.println("文件不存在");
                    }
                    if(deleleFalg){
                        //刪除垃圾文件個數加一
                        count++;
                        //更新文件的删除标记
                        filePathDao.updFileDeleteStatus(fileId);
                        //插入操作记录
                        FileOperationLog operationLog = new FileOperationLog();
                        operationLog.setRowId(fileOperationDao.getNextRowId());
                        operationLog.setFileId(fileId);
                        operationLog.setOperationType("DELETE");
                        operationLog.setCreateEmp("system");
                        operationLog.setOperationDesc("DELETE"+"=="+file.getFileName()+file.getFileExpandedName());
                        fileOperationDao.insert(operationLog);
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage()+e.getStackTrace()[0].getClassName()+"==="+e.getStackTrace()[0].getMethodName()+"==="+e.getStackTrace()[0].getLineNumber());
        }

        long endTime=System.currentTimeMillis();
        System.out.println("清理"+count+"個垃圾文件共用了"+(endTime-startTime)+"ms");

    }
}
