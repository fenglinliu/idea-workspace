package cn.bookcycle.messagequeue.util;

import java.util.UUID;

/**
 * IDAndNameProducer
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/28
 */
public class IDAndNameProducer {

    private static final String EXCHANGE = "exchange";

    private static final String LINE = "-";

    private static final String SYSTEM = "sys";

    private static final String NEW_CHAR = "";

    public static String newExchangeName() {
        return EXCHANGE + System.currentTimeMillis();
    }

    /**
     * 生成队列名字
     *
     * @param exchange 交换器名字
     * @param index 该队列在交换器的若干个队列中的索引
     * @return
     */
    public static String newQueueName(String exchange, int index) {
        return exchange + LINE + index;
    }

    public static String newBusinessId() {
        return SYSTEM + UUID.randomUUID().toString().replace(LINE, NEW_CHAR).substring(0, 29);
    }


    public static void main(String[] args) {
        String s1 = "abcde";
        String s2 = new String ("abcde");
        System.out.print(s1== s2);
    }
}
