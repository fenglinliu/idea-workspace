package cn.bookcycle.messagequeue.pojo;

/**
 * ResponseNameInfo
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/27
 */
public class ResponseNameInfo implements  ResponseInterface {
    private String rspCode;

    private String result;

    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ResponseNameInfo{" +
                "rspCode='" + rspCode + '\'' +
                ", result='" + result + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
