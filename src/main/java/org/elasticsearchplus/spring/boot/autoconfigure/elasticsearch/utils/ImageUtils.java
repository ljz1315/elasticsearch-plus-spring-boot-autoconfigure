package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.utils;

import com.google.common.io.Files;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Paths;

/**
 * @Author: lijz
 * @Description 图片工具
 * @Date: 2020/8/31
 */

public class ImageUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtils.class);

    /**
     * 解析图片是否可以正常展示
     *
     * @param content
     * @param suffix
     * @param sinkFile
     * @return
     * @see ImageIO
     */
    public static boolean availableByImageIO(byte[] content, String suffix, boolean sinkFile) {
        // 判断图片是否损坏,异常图片返回null
        BufferedImage sourceImg = null;
        if (sinkFile) {
            sourceImg = parseImage(content, suffix);
        } else {
            sourceImg = parseImage(content);
        }
        return sourceImg != null;
    }

    /**
     * 解析图片是否可以正常展示
     *
     * @param content
     * @return
     * @see MagicMatch
     */
    public static boolean availableByMagicMatch(byte[] content) {
        MagicMatch magicMatch = parseMagicMatch(content);
        return magicMatch != null;
    }

    /**
     * 把图片内容构建为ByteArrayInputStream
     * 再解析
     *
     * @param content
     * @return
     * @see ImageIO
     */
    public static BufferedImage parseImage(byte[] content) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);
            // 两种方式，校验图片是否损坏
            // 1. java->BufferedImage
            //判断图片是否损坏,异常图片返回null
            BufferedImage sourceImg = ImageIO.read(byteArrayInputStream);
            return sourceImg;
        } catch (Exception e) {
            LOGGER.error("ImageUtils#parseImage->BufferedImage is error.", e);
            return null;
        }
    }

    /**
     * 把图片内容保存为文件
     * 再解析
     *
     * @param content
     * @param suffix
     * @return
     * @see ImageIO
     */
    public static BufferedImage parseImage(byte[] content, String suffix) {
        String dataDir = System.getProperty("user.dir") + "/data/";
        String tmpFileName = "tmp-" + System.currentTimeMillis() + suffix;
        File dataFileDir = Paths.get(dataDir).toFile();
        try {
            if (!dataFileDir.exists() && !dataFileDir.isDirectory()) {
                dataFileDir.createNewFile();
            }
            File tempFile = new File(dataFileDir + tmpFileName);
            Files.write(content, tempFile);
            // 两种方式，校验图片是否损坏
            // 1. java->BufferedImage
            // 判断图片是否损坏,异常图片返回null
            BufferedImage sourceImg = ImageIO.read(tempFile);
            return sourceImg;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析图片内容
     *
     * @param content
     * @return
     * @see MagicMatch
     */
    public static MagicMatch parseMagicMatch(byte[] content) {
        MagicMatch magicMatch = null;
        try {
            // 两种方式，校验图片是否损坏
            // 2. 第三方依赖jmimemagic->MagicMatch
            // 正常图片，会解析为MagicMatch；否则，会报错
            magicMatch = Magic.getMagicMatch(content, false);
            return magicMatch;
        } catch (Exception e) {
            LOGGER.error("ImageUtils#parseMagicMatch->MagicMatch is error.", e);
            return null;
        }
    }

    public static HttpHeaders getDefaultHttpHeaders() {
        HttpHeaders hs = new HttpHeaders();
        hs.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        hs.add("X-Requested-With", "XMLHttpRequest");

        return hs;
    }
}
