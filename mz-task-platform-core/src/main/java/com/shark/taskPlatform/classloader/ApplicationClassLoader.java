package com.shark.taskPlatform.classloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import com.shark.taskPlatform.utils.IOHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 自定义类加载器
 */
public class ApplicationClassLoader extends URLClassLoader {
	
	public static final Log log = LogFactory.getLog(ApplicationClassLoader.class);
	
	private Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
    
	private ClassLoader javaClassLoader;
    
    /**通过构造方法设置父类加载器和要热加载的类名**/
    public ApplicationClassLoader(ClassLoader parent) {
        super(new URL[]{}, parent);
        ClassLoader classLoader = String.class.getClassLoader();
        if (classLoader == null) {
            classLoader = getSystemClassLoader();
            while (classLoader.getParent() != null) {
                classLoader = classLoader.getParent();
            }
        }
        this.javaClassLoader = classLoader;
        		
    }
    
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = classMap.get(name);
        if (clazz != null) {
            return clazz;
        }
        synchronized (getClassLoadingLock(name)) {
        	try {
	        	clazz = findLoadedClass(name);
	            if (clazz != null) {
	                if (resolve) {
	                    resolveClass(clazz);
	                }
	                return clazz;
	            }
        	}catch(Exception e){
        	}
        	
        	try {
                clazz = javaClassLoader.loadClass(name);
                if (clazz != null) {
                    if (resolve) {
                        resolveClass(clazz);
                    }
                    return clazz;
                }
            } catch (Throwable e) {
            }
        	
        	 try {
                 InputStream resource = getResourceAsStream(binaryNameToPath(name, false));
                 byte[] bytes = IOHelper.readStreamBytesAndClose(resource);
                 clazz = defineClass(name, bytes, 0, bytes.length);
                 if (clazz != null) {
                     classMap.put(name, clazz);
                     if (resolve) {
                         resolveClass(clazz);
                     }
                     return clazz;
                 }
             } catch (Throwable e) {
             }
        	
        	
        }
         
        return super.loadClass(name, resolve);
    }
    
    
    private String binaryNameToPath(String binaryName, boolean withLeadingSlash) {
        StringBuilder path = new StringBuilder(7 + binaryName.length());
        if (withLeadingSlash) {
            path.append('/');
        }
        path.append(binaryName.replace('.', '/'));
        path.append(".class");
        return path.toString();
    }
    
    @Override
    protected void addURL(URL url) {
        super.addURL(url);
    }
    
    public synchronized void addJarFiles(String... jarFilePaths) {
        if (jarFilePaths == null || jarFilePaths.length == 0) {
            return;
        }
        for (String jarFilePath : jarFilePaths) {
            File file = new File(jarFilePath);
            if (file.exists()) {
                try {
                    addURL(file.toURI().toURL());
                } catch (Throwable e) {
                	log.info("jar file [" + jarFilePath + "] can't be add.");
                }
            } else {
            	log.info("jar file [" + jarFilePath + "] can't be found.");
            }
        }
    }
    
    
}
