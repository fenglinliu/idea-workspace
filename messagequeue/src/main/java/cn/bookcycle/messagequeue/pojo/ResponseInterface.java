package cn.bookcycle.messagequeue.pojo;

public interface ResponseInterface {
    String REQUEST_BODY_INVALID_CODE = "40001";

    String REQUEST_BODY_INVALID_TIPS = "request body is invalid";

    String MSG_INVALID_CODE = "40002";

    String MSG_INVALID_TIPS = "msg is invalid";

    String NAME_INVALID_CODE = "40003";

    String NAME_INVALID_TIPS = "name is invalid";

    String MSG_AND_NAME_INVALID_CODE = "40004";

    String MSG_AND_NAME_INVALID_TIPS = "msg and name are invalid";

    String BUSINESS_ID_INVALID_CODE = "40005";

    String BUSINESS_ID_INVALID_TIPS = "business id is invalid";

    String BUSINESS_ID_AND_NAME_INVALID_CODE = "40006";

    String BUSINESS_ID_AND_NAME_INVALID_TIPS = "business id and name is invalid";

    String SYSTEM_FAILED_CODE = "50001";

    String SYSTEM_FAILED_TIPS = "Message Queue is down";

    String SYSTEM_BUSY_CODE = "20001";

    String SYSTEM_BUSY_TIPS = "system is busy now, please retry later";

    String SUCCESS_CODE = "0";

    String SUCCESS_TIPS = "success";


}
