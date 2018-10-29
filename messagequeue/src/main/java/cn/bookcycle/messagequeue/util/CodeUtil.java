package cn.bookcycle.messagequeue.util;

import cn.bookcycle.messagequeue.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * CodeUtil
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/28
 */
public class CodeUtil {
    private static Logger LOGGER = LoggerFactory.getLogger(CodeUtil.class);

    public static void code4Serialize() {
        // 序列化
        Serialize serialize = new Serialize();
        serialize.initClass4serialize(MessageService.class);
        Thread thread = new Thread(serialize);
        thread.start();
    }

    public static void code4UnSerialize() {
        UnSerialize unSerialize = new UnSerialize();
        Thread thread = new Thread(unSerialize);
        thread.start();
    }

    public static MessageService code4AssignValueUnSerialize() {
        ApplicationContext appCtx = SpringContextUtil.getApplicationContext();
        MessageService bean = (MessageService) SpringContextUtil.getBean(MessageService.class);
        bean = UnSerialize.messageService;
        LOGGER.info("========bean:{}", bean);
        return bean;
    }
}
