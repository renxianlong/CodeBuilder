package com.kong.codebuilder.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.kong.codebuilder.common.Context;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 写文件
     *
     * @param fileName 文件名
     * @param path     文件路径
     * @param text     文件内容
     */
    public static void createFile(String fileName, String path, List<String> text) {
        //创建完整路径
        String fullDirectoryPath = formatFullDirectoryPath(Context.project.getBasePath(), path);

        try {
            //创建文件夹
            File filePath = new File(fullDirectoryPath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }

            //如果文件不存在，创建文件
            File file = new File(fullDirectoryPath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));
            Iterator<String> iterator = text.iterator();
            String line1 = iterator.next();
            fileWriter.write(line1);
            while (iterator.hasNext()) {
                fileWriter.newLine();
                fileWriter.write(iterator.next());
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            logger.error("写文件失败", e);
        }
    }

    /**
     * 写文件
     *
     * @param fileName 文件名
     * @param path     文件路径
     * @param text     文件内容
     */
    public static void createFile(String fileName, String path, String text) {
        //创建完整路径
        String fullDirectoryPath = formatFullDirectoryPath(Context.project.getBasePath(), path);

        try {
            //创建文件夹
            File filePath = new File(fullDirectoryPath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }

            File file = new File(fullDirectoryPath + fileName);
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();

            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));
            fileWriter.write(text);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            logger.error("写文件失败", e);
        }
    }

    /**
     * 写文件夹
     *
     * @param project
     * @param path    文件路径
     */
    public static PsiDirectory getPsiDirectory(Project project, String path) {
        String basePath = project.getBasePath();

        //创建完整路径
        String fullDirectoryPath = formatFullDirectoryPath(basePath, path);

        try {
            //创建文件夹
            File filePath = new File(fullDirectoryPath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }

            VirtualFileManager.getInstance().syncRefresh();
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(filePath);
            return PsiDirectoryFactory.getInstance(project).createDirectory(virtualFile);
        } catch (Exception e) {
            logger.error("写文件夹失败", e);
        }

        return null;
    }

    public static void saveXml(String fileName, String path, Document document) {
        //创建完整路径
        String fullDirectoryPath = formatFullDirectoryPath(Context.project.getBasePath(), path);

        try {
            //创建文件夹
            File filePath = new File(fullDirectoryPath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }

            //如果文件不存在，创建文件
            File file = new File(fullDirectoryPath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            Format format = Format.getPrettyFormat();
            format.setIndent("    ");
            XMLOutputter outputter = new XMLOutputter(format);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            outputter.output(document, fileOutputStream);//Rawoutput
            fileOutputStream.close();
        } catch (Exception e) {
            logger.error("写文件失败", e);
        }
    }

    /**
     * 格式化完整路径
     *
     * @param basePath
     * @param path
     * @return
     */
    public static String formatFullDirectoryPath(String basePath, String path) {
        if (StringUtils.isEmpty(path) || StringUtils.isEmpty(basePath)) {
            return null;
        }

        if (basePath.endsWith("/") && path.startsWith("/")) {
            basePath.substring(0, basePath.length() - 1);
        }

        if (!basePath.endsWith("/") && !path.startsWith("/")) {
            basePath = basePath + "/";
        }

        if (!path.endsWith("/")) {
            path = path + "/";
        }

        return basePath + path;
    }
}
