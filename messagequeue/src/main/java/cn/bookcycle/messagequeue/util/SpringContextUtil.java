package cn.bookcycle.messagequeue.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * SpringContextUtil
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/28
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {


    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    public static Object getBean(Class c) {
        return applicationContext.getBean(c);
    }

}
