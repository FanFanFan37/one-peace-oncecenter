package com.treefintech.b2b.oncecenter.common.utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.treefinance.b2b.common.concurrent.Processor;
import com.treefinance.b2b.common.exceptions.ServiceException;
import com.treefinance.b2b.common.utils.lang.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * pdf工具类
 */
@Slf4j
public class PdfUtil {

    /**
     * HTML转PDF
     * @param inputStream  html文件输入流
     * @param outputStream 输出流，pdf文件
     * @param fontResource  字体文件  如 /resource/font/simsun.ttc文件
     * @param variableValue 替换html文件中的${}
     * @param <T>
     */
    public static <T> void parseHtml2Pdf(InputStream inputStream, OutputStream outputStream, String fontResource, T variableValue){
        try {

            Processor<InputStream, String> processor = paramInputStream -> {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    IOUtils.copyLarge(paramInputStream, bos);
                    return new String(bos.toByteArray());
                } catch (Exception e) {
                    throw new ServiceException(-1, e);
                }
            };

            //将html的inputStream转为字符串
            String htmlContent = processor.process(inputStream);
            //替换字符串中的${}
            htmlContent = StringUtils.replaceVariable(htmlContent, variableValue);

            Document document = new Document();
            PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
            document.open();

            //加载字体
            XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider() {

                @Override
                public Font getFont(String fontname, String encoding, boolean embedded, float size, int style, BaseColor color) {

                    //你的字体文件的位置
                    //这里把所有字体都设置为宋体了,可以根据fontname的值设置字体
                    String yaHeiFontName = fontResource;
                    //如果是ttc需要这一行,ttf不需要
                    yaHeiFontName += ",1";
                    Font font = null;
                    try {
                        font = new Font(BaseFont.createFont(yaHeiFontName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED));
                        font.setStyle(style);
                        font.setColor(color);
                        if (size>0){
                            font.setSize(size);
                        }
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return font;
                }
            };

            // step 4
            XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, document, new ByteArrayInputStream(htmlContent.getBytes()), Charset.forName("UTF-8"),fontProvider);
            // step 5
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
