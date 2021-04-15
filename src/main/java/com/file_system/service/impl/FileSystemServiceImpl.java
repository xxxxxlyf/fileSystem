package com.file_system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.file_system.dao.FileOperationDao;
import com.file_system.dao.FilePathDao;
import com.file_system.pojo.*;
import com.file_system.pojo.enumerate.FileEnum;
import com.file_system.pojo.returnMes.JsonData;
import com.file_system.service.FileSystemService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class FileSystemServiceImpl implements FileSystemService {

    @Autowired
    FileOperationDao fileOperationDao;
    @Autowired
    FilePathDao filePathDao;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Value("${fileRootPath}")
    private String fileRootPath;
    @Value("${uploadFilePath}")
    private String uploadFilePath;
    @Value("${baseFilePath}")
    private String baseFilePath;
    @Value("${avatar_path}")
    private String avatar_path;
    @Value("${avatar_upload_path}")
    private String avatar_upload_path;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf1=new SimpleDateFormat("yyyyMMddHHmmssSSS");


    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    private Logger log = LoggerFactory.getLogger(FileSystemServiceImpl.class);

    @Override
    public JsonData uploadFiles(HttpServletRequest request, MultipartFile[] files, String groupId, String fileDesc, String emp,String status) {

        JsonData res = new JsonData();
        String dayPath = sdf.format(new Date());
        String path = uploadFilePath+ dayPath + "/";
        String newGroupId = fileOperationDao.getNextRowId();
        int count = 0;
        for (MultipartFile file : files) {
            //文件id
            String fileId = fileOperationDao.getNextRowId();
            //获得文件的信息
            String fullName = file.getOriginalFilename();
            String expandName = fullName.substring(fullName.lastIndexOf(".") + 1);
            String fileName = fullName.substring(0, fullName.lastIndexOf("."));
            //时间格式化作为文件名
            String pathssss=sdf1.format(new Date())+"."+expandName;
            String fileSize;
            if (file.getSize() / 1024 > 1024) {
                //当文件大于500M时，抛出文件上传大小异常，超出规定的500M
                fileSize = file.getSize() / 1024 / 1024 + "M";
                if (file.getSize() / 1024/1024 > 1024) {
                    throw new MaxUploadSizeExceededException(file.getSize());
                }

            } else {
                fileSize = file.getSize() / 1024 + "K";
            }

            //文件上传动作
            try {

                if(status!=null&&status.equals("avatar")){
                    if(StringUtils.isEmpty(emp)){
                        throw  new Exception("工号不为空");
                    }
                    //文件存储为头像时
                    path=avatar_upload_path;
                    pathssss=sdf1.format(new Date())+"."+expandName;
                    baseFilePath=avatar_path;
                }
                Path paths = Paths.get(path + pathssss);
                //文件夹路径不存在则创建
                if (!Files.isWritable(Paths.get(path))) {
                    Files.createDirectories(Paths.get(path));
                }
                //执行上传文件操作
                InputStream is = null;
                FileOutputStream out = null;

                try {
                    is = file.getInputStream();
                    //创建一个文件输出流
                    out = new FileOutputStream(path + pathssss);
                    //创建一个缓冲区，一次读写的量是5M
                    byte buffer[] = new byte[1024 * 1024 * 5];
                    //判断输入流中的数据是否已经读完的标志
                    int len = 0;
                    while ((len = is.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }

                } catch (Exception e) {
                    res.setMsg("文件上传时失败" + getExceptionMes(e));
                    return res;
                } finally {
                    //关闭输出流
                    if (out != null) {
                        out.close();
                    }
                    //关闭输入流
                    if (is != null) {
                        is.close();
                    }
                }

            } catch (Exception e) {
                res.setMsg("文件上传时失败" + getExceptionMes(e));
                return res;
            }


            CFilePathData filePathData = new CFilePathData();
            FileOperationLog fileOperationLog = new FileOperationLog();
            //插入文件路径记录
            if (!StringUtils.isEmpty(groupId)) {
                filePathDao.updFileStatus(groupId, "N");
            }
            //文件路径以及文件操作记录写入
            filePathData.setFileGroupId(newGroupId);
            filePathData.setRowId(fileOperationDao.getNextRowId());
            filePathData.setFileId(fileId);
            filePathData.setFileName(fileName);
            filePathData.setFileSize(fileSize);
            filePathData.setFileExpandedName(expandName);
            if(status!=null&&status.equals("avatar")){

                filePathData.setFilePath(baseFilePath + pathssss);
            }else{

                filePathData.setFilePath(baseFilePath + dayPath + "/" + pathssss);
            }
            filePathData.setFileDesc(fileDesc + "==" + fileName);
            //默认文件不启用
            filePathData.setIsSuccess("N");
            filePathData.setIsDelete("N");
            filePathData.setCreateEmp(emp);
            count += filePathDao.insert(filePathData);


            fileOperationLog.setRowId(fileOperationDao.getNextRowId());
            fileOperationLog.setFileId(fileId);
            fileOperationLog.setOperationType("INSERT");
            fileOperationLog.setCreateEmp(emp);
            fileOperationLog.setOperationDesc("INSERT" + "===" + fullName);
            count += fileOperationDao.insert(fileOperationLog);
        }

        if (count == files.length * 2) {
            if(count==0){

                res.setCode(1);
                res.setMsg("请检查上传的文件格式！");
                return res;

            }else{
                res.setMsg("成功上传" + files.length + "个文件");
                res.setData(newGroupId);

            }

        } else {
            try {
                throw new Exception("写文件信息与log信息错误，请重新上传文件");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //開啓綫程池，將文件的學分信息，寫入到文件表中
        threadPoolExecutor.execute(()->{
            getFileScore(newGroupId);
        });

        return res;
    }

    @Override
    public JsonData uploadFilesWithAdd(HttpServletRequest request, MultipartFile[] files, String groupId, String fileDesc, String emp,String status) {

        JsonData res = new JsonData();
        String dayPath = sdf.format(new Date());
        String path = uploadFilePath + dayPath + "/";
        if(StringUtils.isEmpty(groupId)){

            res.setMsg("参数传递错误,文件组不为空");
            res.setCode(1);
            return res;
        }else {
            //check文件组是否传入正确
            if (filePathDao.selectList(new QueryWrapper<CFilePathData>().eq("FILE_GROUP_ID", groupId)).size() >= 0) {
                int count = 0;
                for (MultipartFile file : files) {
                    //文件id
                    String fileId = fileOperationDao.getNextRowId();
                    //获得文件的信息
                    String fullName = file.getOriginalFilename();
                    String expandName = fullName.substring(fullName.lastIndexOf(".") + 1);
                    String fileName = fullName.substring(0, fullName.lastIndexOf("."));
                    //时间格式化作为文件名
                    String pathssss = sdf1.format(new Date()) + "." + expandName;
                    String fileSize;
                    if (file.getSize() / 1024 > 1024) {
                        //当文件大于500M时，抛出文件上传大小异常，超出规定的500M
                        fileSize = file.getSize() / 1024 / 1024 + "M";
                        if (file.getSize() / 1024 / 1024 > 1024) {
                            throw new MaxUploadSizeExceededException(file.getSize());
                        }

                    } else {
                        fileSize = file.getSize() / 1024 + "K";
                    }

                    //文件上传动作
                    try {

                        if(status!=null&&status.equals("avatar")){
                            if(StringUtils.isEmpty(emp)){
                                throw  new Exception("工号不为空");
                            }
                            //文件存储为头像时
                            path=avatar_upload_path;
                            pathssss=sdf1.format(new Date())+"."+expandName;
                            baseFilePath=avatar_path;
                        }
                        Path paths = Paths.get(path + pathssss);
                        //文件夹路径不存在则创建
                        if (!Files.isWritable(Paths.get(path))) {
                            Files.createDirectories(Paths.get(path));
                        }
                        //执行上传文件操作
                        InputStream is = null;
                        FileOutputStream out = null;

                        try {
                            is = file.getInputStream();
                            //创建一个文件输出流
                            out = new FileOutputStream(path + pathssss);
                            //创建一个缓冲区，一次读写的量是5M
                            byte buffer[] = new byte[1024 * 1024 * 5];
                            //判断输入流中的数据是否已经读完的标志
                            int len = 0;
                            while ((len = is.read(buffer)) > 0) {
                                out.write(buffer, 0, len);
                            }

                        } catch (Exception e) {
                            res.setMsg("文件上传时失败" + getExceptionMes(e));
                            return res;
                        } finally {
                            //关闭输出流
                            if (out != null) {
                                out.close();
                            }
                            //关闭输入流
                            if (is != null) {
                                is.close();
                            }
                        }

                    } catch (Exception e) {
                        res.setMsg("文件上传时失败" + getExceptionMes(e));
                        return res;
                    }
                    CFilePathData filePathData = new CFilePathData();
                    FileOperationLog fileOperationLog = new FileOperationLog();
                    //插入文件路径记录
                    if (!StringUtils.isEmpty(groupId)) {
                        filePathDao.updFileStatus(groupId, "N");
                    }
                    //文件路径以及文件操作记录写入
                    filePathData.setFileGroupId(groupId);
                    filePathData.setRowId(fileOperationDao.getNextRowId());
                    filePathData.setFileId(fileId);
                    filePathData.setFileName(fileName);
                    filePathData.setFileSize(fileSize);
                    filePathData.setFileExpandedName(expandName);
                    if(status!=null&&status.equals("avatar")){

                        filePathData.setFilePath(baseFilePath + pathssss);
                    }else{

                        filePathData.setFilePath(baseFilePath + dayPath + "/" + pathssss);
                    }
                    filePathData.setFileDesc(fileDesc + "==" + fileName);
                    //默认文件不启用
                    filePathData.setIsSuccess("N");
                    filePathData.setIsDelete("N");
                    filePathData.setCreateEmp(emp);
                    count += filePathDao.insert(filePathData);


                    fileOperationLog.setRowId(fileOperationDao.getNextRowId());
                    fileOperationLog.setFileId(fileId);
                    fileOperationLog.setOperationType("INSERT");
                    fileOperationLog.setCreateEmp(emp);
                    fileOperationLog.setOperationDesc("INSERT" + "===" + fullName);
                    count += fileOperationDao.insert(fileOperationLog);
                }
                if (count == files.length * 2) {
                    if(count==0){
                        res.setCode(1);
                        res.setMsg("请检查上传的文件格式！");
                        return res;
                    }else{
                        res.setMsg("成功上传" + files.length + "个文件");
                        res.setData(groupId);
                    }
                } else {
                    try {
                        throw new Exception("写文件信息与log信息错误，请重新上传文件");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //開啓綫程池，將文件的學分信息，寫入到文件表中
                threadPoolExecutor.execute(() -> {
                    getFileScore(groupId);
                });
            }
            else {
                res.setMsg("文件组传入错误，无法加入到同一个文件组下，请检查输入参数");
                res.setCode(1);
                return res;
            }

        }
        return res;

    }

    @Override
    public JsonData getFileInfo(String groupId) {
        JsonData res = new JsonData();
        try {
            List<CFilePathData> list = filePathDao.selectList(new QueryWrapper<CFilePathData>().eq("FILE_GROUP_ID", groupId).eq("IS_DELETE", "N"));
            if (list.size() > 0) {
                FileView view = new FileView();
                view.setGroupId(groupId);
                List<OneFileInfo> fileList = new ArrayList<>();
                for (CFilePathData pathData : list) {
                    String fileId = pathData.getFileId();
                    String fullPath = fileRootPath + pathData.getFilePath();
                    fileList.add(new OneFileInfo(fileId, fullPath));
                }
                view.setFileList(fileList);
                res.setData(view);
                res.setMsg("查询文件信息如下");
            } else {
                res.setMsg("文件组id错误！无法查询该文件组下的所有文件信息");
                res.setCode(1);
            }
        } catch (Exception e) {
            res.setMsg("查询文件错误" + e.getMessage());
            res.setCode(1);
        }
        return res;
    }

    @Override
    public JsonData delFile(String fileId, String emp) {
        JsonData res = new JsonData();

        try {
            //根据文件id查询文件路径详情
            CFilePathData file = filePathDao.selectOne(new QueryWrapper<CFilePathData>().eq("FILE_ID", fileId).eq("IS_DELETE", "N"));
            if (file != null) {
                String path = file.getFilePath();
                Path filePath = Paths.get("../webapps" + path);
                File f = filePath.toFile();
                //当文件存在时,删除文件
                boolean deleleFalg = false;
                if (f.exists()) {
                    deleleFalg = f.delete();
                } else {
                    Map<String,Object> paramMap=new HashMap<>();
                    res.setMsg("成功删除了一个文件");
                    //刪除文件记录中的数据
                    paramMap.put("FILE_ID",fileId);
                    filePathDao.deleteByMap(paramMap);
                    return res;
                }
                if (deleleFalg) {
                    //更新文件的删除标记
                    int i1 = filePathDao.updFileDeleteStatus(fileId);
                    //插入操作记录
                    FileOperationLog operationLog = new FileOperationLog();
                    operationLog.setRowId(fileOperationDao.getNextRowId());
                    operationLog.setFileId(fileId);
                    operationLog.setOperationType("DELETE");
                    operationLog.setCreateEmp(emp);
                    operationLog.setOperationDesc("DELETE" + "==" + file.getFileName() + file.getFileExpandedName());
                    int i2 = fileOperationDao.insert(operationLog);

                    if (i1 + i2 == 2) {
                        //删除文件记录的数据
                        Map<String,Object> paramMap=new HashMap<>();
                        paramMap.put("FILE_ID",fileId);
                        filePathDao.deleteByMap(paramMap);
                        res.setMsg("成功删除了一个文件");
                    }

                }
            } else {
                res.setMsg("文件id错误，没有找到该文件信息，删除文件失败");
                res.setData(1);
            }
        } catch (Exception e) {
            res.setMsg("删除文件失败" + getExceptionMes(e));
            res.setCode(1);

        }
        return res;
    }

    /**
     * check文件组的使用状态
     *
     * @param fileGroup
     * @return
     */
    @Override
    public JsonData updFileStatus(String fileGroup) {
        JsonData res = new JsonData();
        try {
            List<CFilePathData> list = filePathDao.selectList(new QueryWrapper<CFilePathData>().eq("FILE_GROUP_ID", fileGroup));
            if (list.size() == 0) {
                res.setMsg("输入的文件组id错误，无法更新当前状态");
                res.setCode(1);
            } else {
                //更新文件的启用状态
                int count = filePathDao.updFileStatus(fileGroup, "Y");
                if (count > 0) {
                    res.setMsg("更新状态成功");
                }
            }
        } catch (Exception e) {
            res.setMsg("更新文件启用状态失败" + getExceptionMes(e));
            res.setCode(1);
        }
        return res;
    }

    @Override
    public JsonData writeScoreInfo(String groupId){ return getFileScore(groupId);}

    /**
     * 根據算法計算課程的學分
     * @param courseId 課程id
     * @return
     */
    @Override
    public JsonData calculateCourseScore(String courseId,String category){
        JsonData res=new JsonData();
        int pdfCount=0;
        int videoCount=0;
        float score=0;
        try {

            //根据课程id查询添加的课件信息
            List<String> fileList=jdbcTemplate.queryForList("select FILE_ID from R_COURSEWARE_DATA where  CURRICULUM_ID=? and IS_DELETE='N'",new Object[]{courseId},String.class);
            if(fileList.size()==0){
                res.setMsg("文件未被启用或已删除，请检查课程的课件使用情况");
                res.setCode(1);
            }
            else{
                //根据文件组id遍历查询文件解析信息
                for (String s : fileList) {
                    CFilePathData file=filePathDao.getFile(s);
                    if(file!=null){
                        //根据文件类型，得到文件的学分信息
                        if(file.getFileUnit().equals("分")){
                            videoCount+=file.getScoreInfo();
                        }else if (file.getFileUnit().equals("頁")){
                            pdfCount+=file.getScoreInfo();
                        }
                    }
                }
            }

            if(pdfCount>0||videoCount>0){
                CFileScore score1=null;
                CFileScore score2=null;
                if(category.equals("online")){
                    //视频
                    score1=filePathDao.getScore(videoCount, "V", "online");
                    if(score1!=null){
                        score+= score1.getSore();
                    }
                    //pdf
                    score2 = filePathDao.getScore(pdfCount,"P","online");
                    if(score2!=null){
                        score+=score2.getSore();
                    }
                }else if(category.equals("offline")){
                    //视频
                    score1=filePathDao.getScore(videoCount, "V", "offline");
                    if(score1!=null){
                        score+= score1.getSore();
                    }
                    //pdf
                    score2 = filePathDao.getScore(pdfCount,"P","offline");
                    if(score2!=null){
                        score+=score2.getSore();
                    }
                }else{
                    res.setMsg("类别参数输入错误，只允许为online/offline");
                    res.setCode(1);
                    return res;
                }

            }
            //设置返回数据
            res.setMsg(courseId+"号课程对应的学分为："+score);
            res.setData(score);
            res.setCode(0);

            //更新当前课程的学分信息
            if(category.equals("online")){
                filePathDao.updScore(score+"",courseId);
            }

        }catch (Exception e){
            res.setCode(1);
            res.setMsg("查询课程的学分失败\n"+getExceptionMes(e));

        }
        return res;
    }


    //内部调用方法
    /**
     * 获得异常信息
     * @param e
     * @return
     */
    public String getExceptionMes(Exception e) {
        return e.getMessage() + "==" + e.getStackTrace()[0].getClassName() + "==" + e.getStackTrace()[0].getMethodName() + e.getStackTrace()[0].getLineNumber();
    }

    /**
     * 根據文件拓展名check文件的類型
     *
     * @param expandName
     * @return
     */
    public static FileEnum checkFileType(String expandName) {
        expandName = expandName.toUpperCase();
        //默認上傳的文件的格式
        FileEnum fileType = FileEnum.PIC;
        switch (expandName) {
            case "PNG":
            case "JPG":
            case "JPEG":
            case "GIF":
                fileType = FileEnum.PIC;
                break;
            case "PDF":
                fileType = FileEnum.PDF;
                break;
            case "MP4":
            case "AVI":
            case "FLV":
                fileType = FileEnum.VEDIO;
                break;
            case "PPT":
            case "PPTX":
                fileType = FileEnum.PPT;
                break;
            case "XLS":
            case "XLSX":
                fileType = FileEnum.EXCEL;
                break;
            default:
                fileType = FileEnum.PIC;
                break;
        }
        return fileType;
    }

    /**
     * 计算上传文件的时长或页码数
     * @param groupId 文件組id
     * @return
     */
    @Override
    public JsonData getFileScore(String groupId) {
        JsonData res = new JsonData();
        FileInfo info = new FileInfo();
        try {
            //根據文件組id查詢文件的信息
            List<CFilePathData> fileList = filePathDao.selectList(new QueryWrapper<CFilePathData>().eq("FILE_GROUP_ID", groupId));
            //學分
            float score = 0;
            for (CFilePathData file : fileList) {
                String expandName = file.getFileExpandedName().toUpperCase();
                String path = file.getFilePath();
                Path filePath = Paths.get("../webapps" + path);
                File file1 = filePath.toFile();
                FileEnum fileType = checkFileType(expandName);
                //視頻類型的文件，讀取視頻文件的時長
                if (fileType == FileEnum.VEDIO) {
                    if (file1.exists()) {
                        MultimediaObject object = new MultimediaObject(file1);
                        MultimediaInfo multimediaInfo = object.getInfo();
                        //計算時長,單位為min
                        float duration = (float) (multimediaInfo.getDuration() * .001 / 60);
                        info.num = duration;
                        info.unit = "分";
                        filePathDao.writeScoreInfo(info.num,info.unit,groupId);
                    }
                    //pdf格式的文件，讀取PDF的頁數
                } else if (fileType == FileEnum.PDF) {
                    if (file1.exists()) {
                        PDDocument pdDocument = PDDocument.load(file1);
                        int pages = pdDocument.getNumberOfPages();
                        pdDocument.close();
                        info.num = pages;
                        info.unit = "頁";
                        filePathDao.writeScoreInfo(info.num,info.unit,groupId);
                    }
                    //ppt格式的文件，讀取PPT的頁數
                } else if (fileType == FileEnum.PPT) {
                    if (file1.exists()) {
                        FileInputStream inputStream = new FileInputStream(file1);
                        XMLSlideShow ppt = new XMLSlideShow(inputStream);
                        int pages = ppt.getSlides().size();
                        ppt.close();
                        inputStream.close();
                        info.num = pages;
                        info.unit = "頁";
                        filePathDao.writeScoreInfo(info.num,info.unit,groupId);
                    }
                }
            }
            res.setCode(0);
            res.setData(info);
            //更新当前课程的学分信息
        } catch (Exception e) {
            res.setMsg("獲取學分失敗" + e.getMessage() + "==" + e.getStackTrace()[0].getLineNumber());
            res.setCode(1);
        }
        return res;
    }


}

