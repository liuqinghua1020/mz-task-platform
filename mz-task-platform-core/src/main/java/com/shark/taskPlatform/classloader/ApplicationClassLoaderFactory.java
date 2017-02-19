package com.shark.taskPlatform.classloader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 对 自定义 类加载器做 缓存管理
 * @author liuqinghua
 *
 */
public abstract class ApplicationClassLoaderFactory {

    private static ClassLoader systemClassLoader;

    private static ApplicationClassLoader nodeApplicationClassLoader;
    
    private static ConcurrentLinkedQueue<ApplicationClassLoader> applicationClassLoaders = new ConcurrentLinkedQueue<ApplicationClassLoader>();

    private static Map<String, ApplicationClassLoader> jarApplicationClassLoaderCache = new HashMap<String, ApplicationClassLoader>();

    /**
     * 该工厂在使用之前,必须制定系统加载器
     *
     * @param systemClassLoader 系统加载器
     */
    public synchronized static void setSystemClassLoader(ClassLoader systemClassLoader) {
        ApplicationClassLoaderFactory.systemClassLoader = systemClassLoader;
    }
    
    /**
     * 通过 初始化若干数量的  ApplicationClassLoader
     * @param maxClassLoaderCount
     */
    public static void setSystemClassLoader(Long maxClassLoaderCount) {
    	 if(maxClassLoaderCount == null || maxClassLoaderCount <= 0){
    		 new IllegalStateException("maxClassLoaderCount 必须是一个 长整型整数");
    	 }
    	 
    	 if (systemClassLoader == null) {
             throw new IllegalStateException("Can't create nodeClassLoader because systemClassLoader is null.");
         }
    	 
    	 for(int i=0;i<maxClassLoaderCount;i++){
    		 ApplicationClassLoader applicationClassLoader = new ApplicationClassLoader(systemClassLoader);
    		 applicationClassLoaders.offer(applicationClassLoader);
    	 }
    	 
    }

    /**
     * 从 ApplicationClassLoaders 中获取 ApplicationClassLoader
     * @return 节点的类加载器
     */
    public static ApplicationClassLoader getApplicationClassLoader() {
    	if(applicationClassLoaders.isEmpty()){
    		setSystemClassLoader(10L);
    	}
    	ApplicationClassLoader applicationClassLoader = applicationClassLoaders.poll();
    	if(applicationClassLoader == null){
    		throw new NullPointerException("没有可用的 applicationClassLoader");
    	}
    	
    	return applicationClassLoader;
    }
    
    
    public static void releaseApplicationClassLoader(ApplicationClassLoader applicationClassLoader){
    	applicationClassLoaders.offer(applicationClassLoader);
    }
}
