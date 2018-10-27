package cn.bookcycle.messagequeue.controller;

import cn.bookcycle.messagequeue.pojo.ResponseBaseInfo;
import cn.bookcycle.messagequeue.pojo.ResponseInterface;
import cn.bookcycle.messagequeue.service.PutMessageService;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * MsgController
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/27
 */
@Controller
public class MsgController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MsgController.class);

    public static final String MSG = "msg";

    public static final String NAME = "name";

    public static final String BLANK = "";


    @Autowired
    private PutMessageService putMessageService;

    @RequestMapping(value = "/mq", method = RequestMethod.POST)
    public ResponseInterface putMessage(@RequestBody (required = false) String requestBody) {
        LOGGER.info("putMessage - requestBody:{}", requestBody);

        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.fromObject(requestBody);
        } catch(JSONException e) {
            ResponseBaseInfo response = new ResponseBaseInfo();
            response.setRspCode(ResponseInterface.REQUEST_BODY_INVALID_CODE);
            response.setResult(ResponseInterface.REQUEST_BODY_INVALID_TIPS);
            return response;
        }

        boolean isMsgValid = false;
        String msg = null;
        if (jsonObject.containsKey(MSG)) {
            if (!msg.trim().equals(BLANK)) {
                isMsgValid = true;
            }
        }
        if (!isMsgValid) {
            ResponseBaseInfo response = new ResponseBaseInfo();
            response.setRspCode(ResponseInterface.MSG_INVALID_CODE);
            response.setResult(ResponseInterface.MSG_INVALID_TIPS);
            return response;
        }

        String name = null;
        boolean isNameValid = true;
        if (jsonObject.containsKey(NAME)) {
            name = jsonObject.getString(NAME);
            if (name.trim().equals(BLANK)) {
                isNameValid = false;
            }
        }
        if (!isNameValid) {
            ResponseBaseInfo response = new ResponseBaseInfo();
            response.setRspCode(ResponseInterface.NAME_INVALID_CODE);
            response.setResult(ResponseInterface.NAME_INVALID_TIPS);
            return response;
        }

        return putMessageService.putMessageToQueue(msg, name);
    }

}
