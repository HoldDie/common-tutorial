package com.holddie.module;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author yangze1
 * @version 1.0.0
 * @email holddie@163.com
 * @date 2018/4/27 22:03
 */
public class ModuleManager {
    private ExecutorService executor = Executors.newFixedThreadPool(8);
    private Map<String, Class> cache = new ConcurrentHashMap<>();
    private List<String> moudleList = new ArrayList<String>();

    private String getClassName(JarEntry entry) {
        String entryName = entry.getName();

        if (!entryName.endsWith(".class")) {
            return null;
        }
        if (entryName.charAt(0) == '/') {
            entryName = entryName.substring(1);
        }
        entryName = entryName.replace("/", ".");
        return entryName.substring(0, entryName.length() - 6);
    }

    public void init() {

        System.out.println("----begin load  All module----");
        List<Future<String>> futureList = new ArrayList<Future<String>>();
        for (String moudle : moudleList) {
            Future<String> future = executor.submit(new Callable<String>() {

                @Override
                public String call() throws Exception {
                    try {
                        URL[] moduleUrl = new URL[]{new URL("file://" + moudle)};
                        @SuppressWarnings("resource")
                        URLClassLoader classLoader = new URLClassLoader(moduleUrl);
                        @SuppressWarnings("resource")
                        JarFile jar = new JarFile(new File(moudle));
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String className = getClassName(entry);
                            if (className == null) {
                                continue;
                            }

                            try {
                                Class<?> clazz = classLoader.loadClass(className);
                                cache.put(className, clazz);
                            } catch (Throwable t) {
                                //System.out.println(t.getLocalizedMessage());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getLocalizedMessage());
                    }

                    return moudle;
                }

            });
            futureList.add(future);
        }

        for (Future<String> future : futureList) {
            try {
                String moduleName = future.get();
                System.out.println("---load moudle " + moduleName + " ok");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        System.out.println("----end load All module----");

    }

    public Map<String, Class> getCache() {
        return cache;
    }

    public void setCache(Map<String, Class> cache) {
        this.cache = cache;
    }

    public List<String> getMoudleList() {
        return moudleList;
    }

    public void setMoudleList(List<String> moudleList) {
        this.moudleList = moudleList;
    }
}
