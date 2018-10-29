package cn.bookcycle.messagequeue.util;

import cn.bookcycle.messagequeue.constant.Constants;
import cn.bookcycle.messagequeue.service.MessageService;
import cn.bookcycle.messagequeue.service.MessageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.*;

/**
 * Serialize
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/28
 */
public class UnSerialize implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnSerialize.class);

    public static MessageService messageService;

    private String address = Constants.ADDRESS;

    public void unSerialize() throws IOException, ClassNotFoundException {
        File file = new File(address);
        if (file.exists()) {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(address));
            MessageService bean = (MessageServiceImpl) is.readObject();
            LOGGER.info("-------------------反序列化开始了。。。。。-------------------");
            LOGGER.info("unSerialize - bean:{}", bean.toString());
            messageService = bean;
        }
//        ApplicationContext appCtx = SpringContextUtil.getApplicationContext();
//        MessageService messageService =  (MessageService) SpringContextUtil.getBean(MessageService.class);
//        ((MessageServiceImpl) messageService).setBusinessIdsAndQueueName(((MessageServiceImpl) bean).getBusinessIdsAndQueueName());
//        ((MessageServiceImpl) messageService).setExchangeAndQueueNumber(((MessageServiceImpl) bean).getExchangeAndQueueNumber());
//        ((MessageServiceImpl) messageService).setExchangeAndUsedQueueNumber(((MessageServiceImpl) bean).getExchangeAndUsedQueueNumber());
        // TODO
//        LOGGER.info("unSerialize - messageService:{}", messageService.toString());
    }

    @Override
    public void run() {
        try {
            unSerialize();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
