package com.codeL.mc.agent;


import com.codeL.mc.agent.common.util.StringUtils;
import com.codeL.mc.agent.log.Log;
import com.codeL.mc.agent.log.LogFactory;

import java.net.URL;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Description: from dubbo</p>
 * <p></p>
 *
 * @author laihj
 *         2018/8/6
 */
public final class Version {

    private Version() {
    }

    private static final Log logger = LogFactory.getLog(Version.class);

    private static final String VERSION = getVersion(Version.class, "x.x.x");

    static {
        Version.checkDuplicate(Version.class);
    }

    public static String getVersion() {
        return VERSION;
    }


    public static String getVersion(Class<?> cls, String defaultVersion) {
        try {
            String version = cls.getPackage().getImplementationVersion();
            if (StringUtils.isEmpty(version)) {
                version = cls.getPackage().getSpecificationVersion();
            }
            if (StringUtils.isEmpty(version)) {
                CodeSource codeSource = cls.getProtectionDomain().getCodeSource();
                if (codeSource == null) {
                    logger.info("No codeSource for class " + cls.getName() + " when getVersion, use default version " + defaultVersion);
                } else {
                    String file = codeSource.getLocation().getFile();
                    if (file != null && file.length() > 0 && file.endsWith(".jar")) {
                        file = file.substring(0, file.length() - 4);
                        int i = file.lastIndexOf('/');
                        if (i >= 0) {
                            file = file.substring(i + 1);
                        }
                        i = file.indexOf("-");
                        if (i >= 0) {
                            file = file.substring(i + 1);
                        }
                        while (file.length() > 0 && !Character.isDigit(file.charAt(0))) {
                            i = file.indexOf("-");
                            if (i >= 0) {
                                file = file.substring(i + 1);
                            } else {
                                break;
                            }
                        }
                        version = file;
                    }
                }
            }
            return StringUtils.isEmpty(version) ? defaultVersion : version;
        } catch (Throwable e) {
            logger.error("return default version, ignore exception " + e.getMessage(), e);
            return defaultVersion;
        }
    }

    public static void checkDuplicate(Class<?> cls, boolean failOnError) {
        checkDuplicate(cls.getName().replace('.', '/') + ".class", failOnError);
    }

    public static void checkDuplicate(Class<?> cls) {
        checkDuplicate(cls, false);
    }

    public static void checkDuplicate(String path, boolean failOnError) {
        try {
            Enumeration<URL> urls = getCallerClassLoader(Version.class).getResources(path);
            Set<String> files = new HashSet<String>();
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String file = url.getFile();
                    if (file != null && file.length() > 0) {
                        files.add(file);
                    }
                }
            }
            if (files.size() > 1) {
                String error = "Duplicate class " + path + " in " + files.size() + " jar " + files;
                if (failOnError) {
                    throw new IllegalStateException(error);
                } else {
                    logger.error(error);
                }
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static ClassLoader getCallerClassLoader(Class<?> caller) {
        return caller.getClassLoader();
    }

}