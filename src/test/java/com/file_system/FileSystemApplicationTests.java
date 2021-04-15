package com.file_system;
import com.file_system.pojo.enumerate.FileEnum;
import com.file_system.service.impl.FileSystemServiceImpl;
import com.file_system.utils.ConvertToPDF;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;


import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


@SpringBootTest
@RunWith(SpringRunner.class)
public class FileSystemApplicationTests {

    @Autowired
    DataSource dataSource;
    @Autowired
    JdbcTemplate jdbcTemplate;



    @Test
    public  void contextLoads() {

        if(dataSource!=null){
            System.out.println(dataSource.getClass());
        }
    }

    //測試獲得ppt的頁數
    @Test
    public void getPptNums() throws IOException {
        long startTime=System.currentTimeMillis();
        String filePath="C:\\Users\\F1336301\\Desktop\\教育训练PPT模板2.pptx";
        File file=new File(filePath);
        if(file.exists()){
            //判斷是否是PPT類型的文件
            String fileExpandName=file.getName();
            fileExpandName=fileExpandName.substring(fileExpandName.indexOf(".")+1);
            FileEnum fileType= FileSystemServiceImpl.checkFileType(fileExpandName);
            if(fileType==FileEnum.PPT){
                FileInputStream in=new FileInputStream(file);
                XMLSlideShow ppt = new XMLSlideShow(in);
                int pages=ppt.getSlides().size();
                ppt.close();
                in.close();
                System.out.println("page："+pages);
            }
        }
        long endTime=System.currentTimeMillis();
        System.out.println("共花費了"+(endTime-startTime)+"ms");

    }

    @Test
    public void jdbcTest() {
        String courseId="2020082810393200003616";
        String sql="select FILE_ID from R_COURSEWARE_DATA where  CURRICULUM_ID=? and IS_DELETE='N' ";
        List<String> list=jdbcTemplate.queryForList(sql,new Object[]{courseId},String.class);
        System.out.println(list.size());
    }


    @Test
    public void testContains(){

        String[] allowDomains = {"http://10.167.214.11:8020","http://localhost:8080","http://10.167.214.11:8011"};
        Set allowOrigins = new HashSet(Arrays.asList(allowDomains));
        if(allowOrigins.contains("http://10.167.214.11:8011")){
            System.out.println("true");
        }
    }


    @Test
    public void testGetProfileInfo(){

         String path="1234.PPT";
         String res=path.substring(0,path.lastIndexOf("."))+".pdf";
         System.out.println(res);
    }



    @Test
    public void testpptToPdf(){

        String path="C:\\Users\\F1336301\\Desktop\\F1323333 .ppt";
        try {
            ConvertToPDF.ppt2Pdf(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
