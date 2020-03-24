package com.wl.guns.core.util.file;

import cn.stylefeng.roses.core.util.SpringContextHolder;
import com.wl.guns.config.properties.GunsProperties;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.UUID;

/**
 * 文件处理工具类
 *
 * @author 王柳
 */
@Slf4j
public class FileUtils {

    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    /**
     * 输出指定文件的byte数组
     *
     * @param filePath 文件路径
     * @param os       输出流
     * @return
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }


    }

    /**
     * 删除文件
     *
     * @param filePath 文件
     * @return
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 文件名称验证
     *
     * @param filename 文件名称
     * @return true 正常 false 非法
     */
    public static boolean isValidFilename(String filename) {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * 下载文件名重新编码
     *
     * @param request  请求对象
     * @param fileName 文件名
     * @return 编码后的文件名
     */
    public static String setFileDownloadHeader(HttpServletRequest request, String fileName)
            throws UnsupportedEncodingException {
        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;
        if (agent.contains("MSIE")) {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        } else if (agent.contains("Firefox")) {
            // 火狐浏览器
            filename = new String(fileName.getBytes(), "ISO8859-1");
        } else if (agent.contains("Chrome")) {
            // google浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        } else {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    /**
     * 获取临时目录
     *
     * @return
     */
    public static String getTempPath() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * 获取文件上传路径
     *
     * @return
     */
    public static String getFileUploadPath(String address) {
        String fileUploadPath = SpringContextHolder.getBean(GunsProperties.class).getFileUploadPath() + address;
        //判断目录存不存在,不存在得加上
        File file = new File(fileUploadPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return fileUploadPath;
    }

    /**
     * 编码文件名
     */
    public static String encodingFilename(String filename, String sub) {
        filename = UUID.randomUUID().toString() + "_" + filename + sub;
        return filename;
    }

    /**
     * 获取下载路径
     *
     * @param filename 文件名称
     */
    public static String getAbsoluteFile(String filename) {
        String downloadPath = getFileUploadPath("") + filename;
        File desc = new File(downloadPath);
        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        return downloadPath;
    }

    public static byte[] toByteArray(String filename) throws Exception {
        File f = new File(filename);
        if (!f.exists()) {
            log.error("文件未找到！" + filename);
            throw new Exception("FILE_NOT_FOUND!");
        } else {
            FileChannel channel = null;
            FileInputStream fs = null;

            try {
                fs = new FileInputStream(f);
                channel = fs.getChannel();
                ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());

                while (channel.read(byteBuffer) > 0) {
                }

                byte[] var5 = byteBuffer.array();
                return var5;
            } catch (IOException var17) {
                throw new Exception("FILE_READING_ERROR!");
            } finally {
                try {
                    channel.close();
                } catch (IOException var16) {
                    throw new Exception("FILE_READING_ERROR!");
                }

                try {
                    fs.close();
                } catch (IOException var15) {
                    throw new Exception("FILE_READING_ERROR!");
                }
            }
        }
    }

    public static void createDirectory(File myfile) {
        if (!myfile.getParentFile().exists()) {
            myfile.getParentFile().mkdirs();
        }
    }

    public static boolean deleteNoticeFile(List<String> pathFileNames, String uploadDir) {
        File deFile;
        for (String pathName : pathFileNames) {
            deFile = new File(uploadDir + pathName);
            if (deFile.isFile() && deFile.exists()) {
                if (!deFile.delete()) {
                    return false;
                }
            }
        }
        return true;
    }
}

