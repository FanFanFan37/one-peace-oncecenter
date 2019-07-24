package com.treefintech.b2b.oncecenter.common.utils;

import com.treefinance.b2b.common.exceptions.ServiceException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 画图
 */
public class ImageCreator {

    /**
     * 画用户签章图片
     * @param pressText 签章水印文字
     * @param fontName  字体文件  如 /resource/font/simsun.ttc文件
     * @param fontStyle 字体风格
     * @param color     字体颜色
     * @param fontSize  字体大小
     * @param x         签章水印文字坐标
     * @param y         签章水印文字坐标
     * @return
     */
    public static BufferedImage createUserImage(String pressText, String fontName, int fontStyle, Color color, int fontSize, int x, int y) {

        try {
            int imageWidth = 500;
            int imageHeight = 200;

            BufferedImage image = new BufferedImage(imageWidth, imageHeight,BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) image.getGraphics();
            g2d.fillRect(0, 0, imageWidth, imageHeight);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


            //背景透明
            image = g2d.getDeviceConfiguration().createCompatibleImage(imageWidth,imageHeight, Transparency.TRANSLUCENT);
            g2d.dispose();
            g2d = image.createGraphics();
            g2d.setStroke(new BasicStroke(10.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));

            // 画横线
            g2d.setColor(Color.red);
            g2d.setStroke(new BasicStroke(10.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
            g2d.drawLine(0, 0, imageWidth , 0);
            g2d.drawLine(0, imageHeight - 1, imageWidth - 1, imageHeight - 1);

            // 画竖线
            g2d.setColor(Color.red);
            g2d.setStroke(new BasicStroke(10.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
            g2d.drawLine(0, 0, 0, imageHeight - 1);
            g2d.drawLine(imageWidth - 1, 0, imageWidth - 1, imageHeight - 1);

            // 在指定坐标绘制水印文字
            g2d.setColor(color);
            Font font = Font.createFont(Font.TRUETYPE_FONT, ImageCreator.class.getResourceAsStream(fontName));
            font = font.deriveFont(fontStyle, fontSize);
            g2d.setFont(font);

            g2d.drawString(pressText, (imageWidth - (getLength(pressText) * fontSize)) / 2 + x, (imageHeight - fontSize) / 2 + y);
            g2d.dispose();
            return image;
        } catch (Exception exp) {
            throw new ServiceException(-1, "" , exp);
        }
    }


    public static void main(String[] args) throws Exception{
        BufferedImage bufferedImage = createUserImage("凡凡凡",  "/font/simsun.ttc", Font.PLAIN, Color.RED, 150, 0, 125);
        ImageIO.write(bufferedImage, "png", new File("/Users/zhang/Downloads/项目资料/担保系统--签章处理/a1Test.png"));
    }


    /**
     * 计算text的长度（一个中文算两个字符）
     *
     * @param text
     * @return
     */
    public static int getLength(String text) {
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            if (new String(text.charAt(i) + "").getBytes().length > 1) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return length / 2;
    }
}
