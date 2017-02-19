package com.shark.taskPlatform.utils;

import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JarFileHelper {
	private static final Log log = LogFactory.getLog(JarFileHelper.class);
	
	/**
	 * 
	 * @param jarFileName jar包文件路径
	 * @throws IOException 构造JarFile失败时抛出，或是找不到 jarFileName 对应的文件，或是 jarFileName 文件格式有误
	 */
	public static JarFile readJarFile(String jarFileName) throws IOException{
		JarFile jarFile = new JarFile(jarFileName);
		
		Manifest manifest = jarFile.getManifest();
		
		/*Attributes attrs = manifest.getMainAttributes();
		Iterator<Entry<Object, Object>> it = attrs.entrySet().iterator();
		while(it.hasNext()){
			Entry entry = it.next();
			log.info(String.format("key:%s ==> value:%s", entry.getKey(), entry.getValue()));
		}*/
		return jarFile;
	}
	
	public static void main(String[] args) throws IOException{
		readJarFile("C:\\Users\\liuqinghua\\Desktop\\adjustsoftwaredata.jar");
	}
	
}
