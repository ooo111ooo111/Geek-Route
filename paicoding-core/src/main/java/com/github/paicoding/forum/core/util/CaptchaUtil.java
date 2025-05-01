package com.github.paicoding.forum.core.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * 验证码工具类
 */
public class CaptchaUtil {
    // 验证码字符集
    private static final char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    // 验证码长度
    private static final int SIZE = 4;
    // 干扰线数量
    private static final int LINES = 5;
    // 宽度
    private static final int WIDTH = 80;
    // 高度
    private static final int HEIGHT = 40;
    // 字体大小
    private static final int FONT_SIZE = 30;

    /**
     * 生成随机验证码
     */
    public static String generateCaptcha() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < SIZE; i++) {
            sb.append(chars[random.nextInt(chars.length)]);
        }
        return sb.toString();
    }

    /**
     * 生成验证码图片
     */
    public static void outputImage(String code, OutputStream out) throws IOException {
        // 创建图片
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        // 获取画笔
        Graphics2D g = image.createGraphics();
        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        // 设置字体
        g.setFont(new Font("微软雅黑", Font.BOLD, FONT_SIZE));
        // 画字符
        Random random = new Random();
        for (int i = 0; i < code.length(); i++) {
            // 随机颜色
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            // 画字符
            g.drawString(code.charAt(i) + "", i * 20 + 5, 30);
        }
        // 画干扰线
        for (int i = 0; i < LINES; i++) {
            // 随机颜色
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            // 随机起点
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            // 随机终点
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            // 画线
            g.drawLine(x1, y1, x2, y2);
        }
        // 释放资源
        g.dispose();
        // 输出图片
        ImageIO.write(image, "JPEG", out);
    }
} 