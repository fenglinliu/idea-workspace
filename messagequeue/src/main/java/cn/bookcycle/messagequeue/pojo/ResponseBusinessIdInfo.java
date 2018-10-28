package cn.bookcycle.messagequeue.pojo;

/**
 * ResponseBusinessIdInfo
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/28
 */
public class ResponseBusinessIdInfo implements  ResponseInterface {
    private String rspCode;

    private String result;

    private String msg;

    private String businessId;

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

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    @Override
    public String toString() {
        return "ResponseBusinessIdInfo{" +
                "rspCode='" + rspCode + '\'' +
                ", result='" + result + '\'' +
                ", msg='" + msg + '\'' +
                ", businessId='" + businessId + '\'' +
                '}';
    }
}
