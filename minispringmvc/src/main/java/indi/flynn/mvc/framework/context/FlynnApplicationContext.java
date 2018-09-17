package indi.flynn.mvc.framework.context;

import indi.flynn.mvc.framework.annotation.FlynnAutowired;
import indi.flynn.mvc.framework.annotation.FlynnController;
import indi.flynn.mvc.framework.annotation.FlynnService;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FlynnApplicationContext
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/9/11
 */
public class FlynnApplicationContext {
    private Map<String,Object> instanceMapping = new ConcurrentHashMap<String, Object>();

    //类似于内部的配置信息，我们在外面是看不到的
    //我们能够看到的只有ioc容器  getBean方法来间接调用的
    private List<String> classCache = new ArrayList<String>();

    //
    private Properties config = new Properties();

    public FlynnApplicationContext(String location) {
        // 先加载配置文件
        InputStream is = null;
        try {
            // 1、定位
            is = this.getClass().getClassLoader().getResourceAsStream(location); // 配置文件和类文件在同一个路径下
            // 2、载入
//            Properties config = new Properties();
            config.load(is);
            // 3、注册，把所有class找出来存着
            String packageName = config.getProperty("scanPackage");
            doRegister(packageName);
            // 4、初始化，循环所有的class,实例化需要ioc的对象(就是加了@Service，@Controller)
            doCreateBean();
            // 5、注入
            populate();
        } catch (Exception e) {
            e.getStackTrace();
        }
        System.out.println("IoC容器已经初始化");
    }

    /**
     * 把符合条件的所有class找出来，注册到缓存中去
     *
     * @param packageName
     */
    private void doRegister(String packageName) {
        // url是要扫描的包所在的目录
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());

        for (File file : dir.listFiles()) {
            //如果是一个文件夹，继续递归
            if(file.isDirectory()){
                doRegister(packageName + "." + file.getName());
            }else{
                // 加到缓存中进行注册
                classCache.add(packageName + "." + file.getName().replace(".class", "").trim());
            }
        }
    }

    private void doCreateBean() {
        //检查看有没有注册信息，注册信息里面保存了所有的class名字
        // Spring中的BeanDefinition 保存了类的名字，也保存了类和类之间的关系（Map、List、Set、ref、parent）
        if(classCache.size() == 0){ return; }

        for (String className : classCache) {
            try {
                //知道这里有一个套路
                Class<?> clazz = Class.forName(className);

                //那个类需要初始化，哪个类不要初始化
                //只要加了@Controller都要初始化
                if(clazz.isAnnotationPresent(FlynnController.class)){
                    //名字起啥？默认就是类名首字母小写
                    String id = lowerFirstChar(clazz.getSimpleName());
                    instanceMapping.put(id, clazz.newInstance());
                    // 只要加了@Service也要初始化
                }else if(clazz.isAnnotationPresent(FlynnService.class)){

                    FlynnService service = clazz.getAnnotation(FlynnService.class);

                    //如果设置了自定义名字，就优先用他自己定义的名字
                    String id = service.value();
                    if(!"".equals(id.trim())){
                        instanceMapping.put(id, clazz.newInstance());
                        continue;
                    }

                    //如果是空的，就用默认规则
                    //1、类名首字母小写
                    //如果这个类是接口
                    //2、可以根据类型类匹配
                    Class<?>[] interfaces = clazz.getInterfaces();
                    //如果这个类实现了接口，就用接口的类型作为id
                    for (Class<?> i : interfaces) {
                        instanceMapping.put(i.getName(), clazz.newInstance());
                    }

                }else{
                    continue;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    //依赖注入
    private void populate() {
        //首先要判断ioc容器中有没有东西
        if(instanceMapping.isEmpty()){ return ;}
        // 挨个扫描，如果有Autowired就自动注入，否则忽略
        for (Map.Entry<String, Object> entry : instanceMapping.entrySet()) {
            //把所有的属性全部取出来，包括私有属性
            Field[] fields = entry.getValue().getClass().getDeclaredFields();

            for (Field field : fields) {
                // 判断是否加上了Autowired注解
                if (!field.isAnnotationPresent(FlynnAutowired.class)) {
                    continue;
                }
                // 如果加了Autowired注解，就把它取出来
                FlynnAutowired autowired = field.getAnnotation(FlynnAutowired.class);
                // 取出用户自定义的ID
                String id = autowired.value().trim();
                //如果id为空，也就是说，自己没有设置，默认根据类型来注入
                if ("".equals(id)) {
                    id = field.getType().getName();
                }
                // 把私有变量开放访问权限，
                field.setAccessible(true);

                try {
                    field.set(entry.getValue(), instanceMapping.get(id));
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

            }
        }
    }

    public Object getBean(String name) {
        return null;
    }

    public Map<String,Object> getAll() {
        return instanceMapping;
    }

    public Properties getConfig() {
        return config;
    }

    /**
     * 将首字母小写
     * @param str
     * @return
     */
    private String lowerFirstChar(String str){
        char [] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}


