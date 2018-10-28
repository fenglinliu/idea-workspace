package cn.bookcycle.messagequeue.pojo;

/**
 * ResponseMsgInfo
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/27
 */
public class ResponseMsgInfo implements  ResponseInterface {
    private String rspCode;

    private String result;

    private String msg;

    public String getRspCode() {
        return rspCode;
    }

    public void setRspCode(String rspCode) {
        this.rspCode = rspCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ResponseMsgInfo{" +
                "rspCode='" + rspCode + '\'' +
                ", result='" + result + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
