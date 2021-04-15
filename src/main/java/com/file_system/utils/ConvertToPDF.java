package com.file_system.utils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

import com.aspose.slides.License;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.itextpdf.text.Image;


import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.hslf.usermodel.*;
import org.springframework.util.StringUtils;


/**
 * 将文件转成PDF文件
 */
public class ConvertToPDF {


    /**
     * PPT文件转成PDF文件
     *
     * @param ppt_Path ppt文件路径
     */
    public static void PPTtoPDF(String ppt_Path) {

        Document document = null;

        XMLSlideShow slideShow = null;


        FileOutputStream fileOutputStream = null;

        PdfWriter pdfWriter = null;
    }

    /**
     * pptToPdf
     *
     * @param pptPath PPT文件路径
     * @return
     */
    public static boolean pptToPdf(String pptPath) throws Exception {

        if (StringUtils.isEmpty(pptPath)) {
            throw new Exception("word文档路径不能为空");
        }


        String pdfPath = pptPath.substring(0, pptPath.lastIndexOf(".")) + ".pdf";
        Document document = null;
        HSLFSlideShow hslfSlideShow = null;
        FileOutputStream fileOutputStream = null;
        PdfWriter pdfWriter = null;
        float zoom = 2;

        try {
            hslfSlideShow = new HSLFSlideShow(new FileInputStream(pptPath));

            // 获取ppt文件页面
            Dimension dimension = hslfSlideShow.getPageSize();

            fileOutputStream = new FileOutputStream(pdfPath);

            document = new Document();

            document.setPageSize(new com.itextpdf.text.Rectangle((float) dimension.width, (float) dimension.height));

            // pdfWriter实例
            pdfWriter = PdfWriter.getInstance(document, fileOutputStream);

            document.open();

            PdfPTable pdfPTable = new PdfPTable(1);


            List<HSLFSlide> hslfSlideList = hslfSlideShow.getSlides();

            for (int i = 0; i < hslfSlideList.size(); i++) {
                HSLFSlide hslfSlide = hslfSlideList.get(i);
//                // 设置字体, 解决中文乱码
//                for (HSLFShape shape : hslfSlide.getShapes()) {
//                    HSLFTextShape textShape = (HSLFTextShape) shape;
//
//                    for (HSLFTextParagraph textParagraph : textShape.getTextParagraphs()) {
//                        for (HSLFTextRun textRun : textParagraph.getTextRuns()) {
//                            textRun.setFontFamily("宋体");
//                        }
//                    }
//                }

                BufferedImage bufferedImage = new BufferedImage((int) dimension.getWidth(), (int) dimension.getHeight(), BufferedImage.TYPE_INT_RGB);

                Graphics2D graphics2d = bufferedImage.createGraphics();

                graphics2d.setPaint(Color.white);
                graphics2d.setFont(new java.awt.Font("宋体", java.awt.Font.PLAIN, 12));
                graphics2d.fill(new Rectangle2D.Float(0, 0, dimension.width * zoom, dimension.height * zoom));


                hslfSlide.draw(graphics2d);

                graphics2d.dispose();

                Image image = Image.getInstance(bufferedImage, null);
                image.scalePercent(90f);

                // 写入单元格
                pdfPTable.addCell(new PdfPCell(image, true));
                document.add(image);

            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (document != null) {
                    document.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (pdfWriter != null) {
                    pdfWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    /**
     * 獲得license确保转换的PDF文件不会出现水印
     *
     * @return
     */
    public static boolean getLicense() {
        boolean flag = false;
        //制定license文件的路径
        //String path="classpath:license.xml";
//        String licenseXml = "<License>\n" +
//                "  <Data>\n" +
//                "    <Products>\n" +
//                "    <Product>Aspose.Total for Java</Product>      \n" +
//                "    </Products>\n" +
//                "    <EditionType>Enterprise</EditionType>\n" +
//                "    <SubscriptionExpiry>20991231</SubscriptionExpiry>\n" +
//                "    <LicenseExpiry>20991231</LicenseExpiry>\n" +
//                "    <SerialNumber>8bfe198c-7f0c-4ef8-8ff0-acc3237bf0d7</SerialNumber>\n" +
//                "  </Data>\n" +
//                "  <Signature>sNLLKGMUdF0r8O1kKilWAGdgfs2BvJb/2Xp8p5iuDVfZXmhppo+d0Ran1P9TKdjV4ABwAgKXxJ3jcQTqE/2IRfqwnPf8itN8aFZlV3TJPYeD3yWE7IT55Gz6EijUpC7aKeoohTb4w2fpox58wWoF3SNp6sK6jDfiAUGEHYJ9pjU=</Signature>\n" +
//                "</License>";

        // 避免文件遗漏
        String licensexml = "<License>\n" +
                "<Data>\n" +
                "<Products>\n" +
                "<Product>Aspose.Total for Java</Product>\n" +
                "<Product>Aspose.Words for Java</Product>\n" +
                "</Products>\n" +
                "<EditionType>Enterprise</EditionType>\n" +
                "<SubscriptionExpiry>20991231</SubscriptionExpiry>\n" +
                "<LicenseExpiry>20991231</LicenseExpiry>\n" +
                "<SerialNumber>8bfe198c-7f0c-4ef8-8ff0-acc3237bf0d7</SerialNumber>\n" +
                "</Data>\n" +
                "<Signature>sNLLKGMUdF0r8O1kKilWAGdgfs2BvJb/2Xp8p5iuDVfZXmhppo+d0Ran1P9TKdjV4ABwAgKXxJ3jcQTqE/2IRfqwnPf8itN8aFZlV3TJPYeD3yWE7IT55Gz6EijUpC7aKeoohTb4w2fpox58wWoF3SNp6sK6jDfiAUGEHYJ9pjU=</Signature>\n" +
                "</License>";
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(licensexml.getBytes());
            License license = new License();
            license.setLicense(inputStream);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * ppt文件转化为PDF文件
     *
     * @param path PPT文件路径
     */
    public static void ppt2Pdf(String path) throws Exception {

        Presentation presentation = null;
        //制定输出文件路径
        String outPdfPath = path.substring(0, path.lastIndexOf(".")) + ".pdf";
        //判断制定路径的文件存在
        File file = new File(path);
        //判断是否得到授权
        if (getLicense()) {
            if (file.isFile() && file.exists()) {
                //判断是否PPT格式的文件
                String expandName = path.substring(path.lastIndexOf(".") + 1);
                if (expandName.toUpperCase().equals("PPT") || expandName.toUpperCase().equals("PPTX")) {

                } else {
                    throw new Exception("需要转换的文件格式错误！只能是PPT或者PPTX格式的文件");
                }
                presentation = new Presentation(path);
                FileOutputStream fileOS = new FileOutputStream(outPdfPath);
                presentation.save(fileOS, SaveFormat.Pdf);
                fileOS.close();

            }
        } else {
            return;
        }


    }

}
