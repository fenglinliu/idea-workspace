package cn.bookcycle.messagequeue.service;

import cn.bookcycle.messagequeue.pojo.*;
import cn.bookcycle.messagequeue.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
public class MessageServiceImpl implements MessageService, Serializable {

    private static final long serialVersionUID = 8294180014912109905L;

    private Map<String, Integer> exchangeAndQueueNumber = new HashMap<String, Integer>();
    private Map<String, Integer> exchangeAndUsedQueueNumber = new HashMap<String, Integer>();
    private Map<String, String> businessIdsAndQueueName = new HashMap<String, String>();

    private static final transient String PUT_LOCK = "锁";

    private static final transient String PULL_LOCK = "锁";

    private static final transient String REGULAR_EXPRESSION = "#";

    private static final transient String POINT = ".";

    private static transient Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);

    public void setExchangeAndQueueNumber(Map<String, Integer> exchangeAndQueueNumber) {
        this.exchangeAndQueueNumber = exchangeAndQueueNumber;
    }

    public void setExchangeAndUsedQueueNumber(Map<String, Integer> exchangeAndUsedQueueNumber) {
        this.exchangeAndUsedQueueNumber = exchangeAndUsedQueueNumber;
    }

    public void setBusinessIdsAndQueueName(Map<String, String> businessIdsAndQueueName) {
        this.businessIdsAndQueueName = businessIdsAndQueueName;
    }

    public Map<String, Integer> getExchangeAndQueueNumber() {
        return exchangeAndQueueNumber;
    }

    public Map<String, Integer> getExchangeAndUsedQueueNumber() {
        return exchangeAndUsedQueueNumber;
    }

    public Map<String, String> getBusinessIdsAndQueueName() {
        return businessIdsAndQueueName;
    }

    public MessageServiceImpl() {
        // 反序列化
        CodeUtil.code4UnSerialize();
    }

    private static boolean isAssignUnSerialize;

    @Override
    public ResponseInterface putMessageToQueue(String msg, String name) {
        // 首先进行，反序列化的赋值
        if (isAssignUnSerialize == false) {
            CodeUtil.code4AssignValueUnSerialize();
            isAssignUnSerialize =true;
        }

        Queue queue = new Queue();
        try {
            boolean isCreateExchange = false;
            if (name == null) {
                // 需要创建交换器
                isCreateExchange = true;
            } else {
                // 通过name查询交换器是否存在，如果不存在则则创建一个交换器
                if (!exchangeAndQueueNumber.containsKey(name)) {
                    isCreateExchange = true;
                }
            }

            String exchange = null;
            // 交换器不存在，则创建交换器和若干个队列
            if (isCreateExchange) {
                synchronized (PUT_LOCK) {
                    exchange = (name == null) ? IDAndNameProducer.newExchangeName() : name;
                }
                queue.createExchange(exchange);
                // 根据系统设置数量，创建对应数量的队列
                if (queue.firstCreateQueue(exchange)) {
                    exchangeAndQueueNumber.put(exchange, queue.defaultQueueNumber());
                    exchangeAndUsedQueueNumber.put(exchange, 0);
                    int queueNumber = exchangeAndQueueNumber.get(exchange);
                    // 向新建的交换器中发送普通消息
                    queue.putMessage(exchange, exchange + POINT + "oridinary" + POINT + new Random().nextInt(), msg);
                }
                ResponseNameInfo response = new ResponseNameInfo();
                response.setName(exchange);
                response.setRspCode(ResponseInterface.SUCCESS_CODE);
                response.setResult(ResponseInterface.SUCCESS_TIPS);
                return response;
            } else {
                // 计算交换器的兄弟交换器数量
                int brothers = queue.computeNextBrotherExchangeIndex(exchangeAndQueueNumber.get(name));
                // 交换器存在，则向现有的交换器中发消息
                queue.putMessage(name, name + POINT + "oridinary" + POINT + new Random().nextInt(), msg);
                // 向现有交换器的兄弟交换器发消息
                for (int i = 1; i <= brothers; ++i) {
                    String brotherExchange = name + "_" + i;
                    queue.putMessage(brotherExchange, brotherExchange + POINT + "oridinary" + POINT + new Random().nextInt(), msg);
                }
                ResponseBaseInfo response = new ResponseBaseInfo();
                response.setRspCode(ResponseInterface.SUCCESS_CODE);
                response.setResult(ResponseInterface.SUCCESS_TIPS);
                return response;
            }


        } catch (Exception e) {
            LOGGER.error("putMessageToQueue - {}", e.getStackTrace());
            ResponseBaseInfo response = new ResponseBaseInfo();
            response.setRspCode(ResponseInterface.SYSTEM_FAILED_CODE);
            response.setResult(ResponseInterface.SYSTEM_FAILED_TIPS);
            return response;
        } finally {
            queue.logoutQueue();
            // 序列化
            CodeUtil.code4Serialize();
        }
    }

    @Override
    public ResponseInterface pullMessage(String name, String businessId) {
        // 首先进行，反序列化的赋值
        if (isAssignUnSerialize == false) {
            CodeUtil.code4AssignValueUnSerialize();
            isAssignUnSerialize =true;
        }

        boolean isNameValid = false;
        // 首先检查name的有效性
        if (name != null && exchangeAndQueueNumber.containsKey(name)) {
            isNameValid = true;
        }

        boolean isBusinessIdValid = true;
        // 其次检查businessId的有效性
        if (businessId != null && !businessIdsAndQueueName.containsKey(businessId)) {
            isBusinessIdValid = false;
        }

        if (!isNameValid && !isBusinessIdValid) {
            // 名字为name的交换器不存在，同时businessId也不存在
            ResponseBaseInfo response = new ResponseBaseInfo();
            response.setRspCode(ResponseInterface.BUSINESS_ID_AND_NAME_INVALID_CODE);
            response.setResult(ResponseInterface.BUSINESS_ID_AND_NAME_INVALID_TIPS);
            return response;
        } else if (!isNameValid) {
            ResponseBaseInfo response = new ResponseBaseInfo();
            response.setRspCode(ResponseInterface.NAME_INVALID_CODE);
            response.setResult(ResponseInterface.NAME_INVALID_TIPS);
            return response;
        } else if (!isBusinessIdValid) {
            // businessId无效，则返回错误提示
            ResponseBaseInfo response = new ResponseBaseInfo();
            response.setRspCode(ResponseInterface.BUSINESS_ID_INVALID_CODE);
            response.setResult(ResponseInterface.BUSINESS_ID_INVALID_TIPS);
            return response;
        }

        Queue queue = new Queue();
        try {

            // 现在开始取消息
            if (businessId != null) {
                // 向businessId对应的消息队列取消息
                String queueName = businessIdsAndQueueName.get(businessId);
                String msg = queue.pullMessage(queueName);
                ResponseMsgInfo response = new ResponseMsgInfo();
                response.setMsg(msg);
                response.setRspCode(ResponseInterface.SUCCESS_CODE);
                response.setResult(ResponseInterface.SUCCESS_TIPS);
                return response;
            } else {
                // businessId为空，表示系统第一次取消息，则为该系统分配一个消息队列(在分配消息队列的时候存在扩容问题)
                // 从没有被消费的队列中分配一个队列
                String msg = null;
                synchronized (PULL_LOCK) {
                    int queueNumber = exchangeAndQueueNumber.get(name);
                    int usedQueueNumber = exchangeAndUsedQueueNumber.get(name);
                    if (usedQueueNumber >=  queueNumber) {
                        // 系统进行队列扩容
                        int increasedQueueNumber = queue.resizeQueueNumber(name, queueNumber, usedQueueNumber);
                        // 更新容量记录
                        if (increasedQueueNumber != 0) {
                            exchangeAndQueueNumber.put(name, queueNumber + increasedQueueNumber);
                        }

                        // 系统繁忙，返回提示
                        ResponseBaseInfo response = new ResponseBaseInfo();
                        response.setRspCode(ResponseInterface.SYSTEM_BUSY_CODE);
                        response.setResult(ResponseInterface.SYSTEM_BUSY_TIPS);
                        return response;
                    } else {
                        /**
                         * 给系统分配一个队列，并从队列中取消息
                         */
                        businessId = IDAndNameProducer.newBusinessId();
                        // 用过的队列数量自增1
                        usedQueueNumber++;
                        String newQueueName = IDAndNameProducer.newQueueName(name, usedQueueNumber);
                        //将该系统的businessId和对应的队列记录下来
                        businessIdsAndQueueName.put(businessId, newQueueName);
                        // 更新使用过的队列记录
                        exchangeAndUsedQueueNumber.put(name, usedQueueNumber);
                        // 向指定的队列去取消息
                        msg = queue.pullMessage(newQueueName);
                        // 最后消息队列进行扩容
                        int increasedQueueNumber = queue.resizeQueueNumber(name, queueNumber, usedQueueNumber);
                        // 更新容量记录
                        if (increasedQueueNumber != 0) {
                            exchangeAndQueueNumber.put(name, queueNumber + increasedQueueNumber);
                        }

                    }
                }
                // 系统第一次取消息成功，返回消息，还要返回业务ID
                ResponseBusinessIdInfo response = new ResponseBusinessIdInfo();
                response.setBusinessId(businessId);
                response.setMsg(msg);
                response.setRspCode(ResponseInterface.SUCCESS_CODE);
                response.setResult(ResponseInterface.SUCCESS_TIPS);
                return response;
            }

        } catch (Exception e) {
            LOGGER.error("pullMessage - {}", e.getStackTrace());
            ResponseBaseInfo response = new ResponseBaseInfo();
            response.setRspCode(ResponseInterface.SYSTEM_FAILED_CODE);
            response.setResult(ResponseInterface.SYSTEM_FAILED_TIPS);
            return response;
        } finally {
            queue.logoutQueue();
            // 序列化
            CodeUtil.code4Serialize();
        }

    }

    @Override
    public String toString() {
        return "MessageServiceImpl{" +
                "exchangeAndQueueNumber=" + exchangeAndQueueNumber +
                ", exchangeAndUsedQueueNumber=" + exchangeAndUsedQueueNumber +
                ", businessIdsAndQueueName=" + businessIdsAndQueueName +
                '}';
    }
}
