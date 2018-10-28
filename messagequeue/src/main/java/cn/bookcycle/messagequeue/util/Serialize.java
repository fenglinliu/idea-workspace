package cn.bookcycle.messagequeue.util;

import cn.bookcycle.messagequeue.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Serialize
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/28
 */
public class Serialize implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Serialize.class);

    private String address = "G:/obj.txt";

    private Class clazz;

    public void initClass4serialize(Class clazz) {
        this.clazz = clazz;
    }

    public void serialize() {
        ApplicationContext appCtx = SpringContextUtil.getApplicationContext();
        MessageService bean = (MessageService) SpringContextUtil.getBean(clazz);
        try {
            LOGGER.info("serialize - address:{}", address);
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(address));
            // 将bean对象写进文件
            os.writeObject(bean);
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        serialize();
    }
}
