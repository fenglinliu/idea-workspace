package cn.bookcycle.messagequeue.service;

import cn.bookcycle.messagequeue.pojo.ResponseInterface;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * PutMessageServiceImpl
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/27
 */
@Service
public class PutMessageServiceImpl implements PutMessageService {
    private Map<String, String> appIdsAndQueueName = new HashMap<String, String>();

    @Override
    public ResponseInterface putMessageToQueue(String msg, String name) {
        // 通过name查询交换器是否存在，如果不存在则则创建一个交换器

        if (name == null) {
            // 需要创建交换器
        } else {
            // 通过查询判断是否需要创建交换器
        }
        return null;
    }

}
