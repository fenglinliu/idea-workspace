package cn.bookcycle.messagequeue.service;

import cn.bookcycle.messagequeue.pojo.ResponseInterface;

/**
 * MessageService
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/27
 */
public interface MessageService {

    ResponseInterface putMessageToQueue(String msg, String name);

    ResponseInterface pullMessage(String name, String businessId);



}
