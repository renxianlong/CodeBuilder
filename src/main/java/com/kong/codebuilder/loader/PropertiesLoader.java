package com.kong.codebuilder.loader;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.PsiFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * properties文件
 */
public class PropertiesLoader {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);

    /**
     * 加载属性
     *
     * @param psiFile
     */
    public static Properties load(PsiFile psiFile) {
        //配置属性
        Properties properties = new Properties();

        //获取当前文件
        if (psiFile.getFileType() != StdFileTypes.PROPERTIES) {
            throw new RuntimeException("非properties文件，操作失败");
        }

        //读取配置文件
        try {
            //读取配置文件
            properties.load(psiFile.getVirtualFile().getInputStream());
        } catch (Exception e) {
            logger.error("read properties error", e);
            throw new RuntimeException("读取配置文件失败", e);
        }

        return properties;
    }
}
