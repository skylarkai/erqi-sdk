package com.erqi.sdk;


/**
 * ReturnMap
 *
 * @author chuhl
 * @date 2017/12/20
 */
public class ReturnMap {
    /**
     * 0:成功,1:失败
     */
    Integer flg ;
    /**
     * 消息内容
     */
    String message;
    /**
     * 结果数据
     */
    String data;
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getFlg() {
        return flg;
    }

    public void setFlg(Integer flg) {
        this.flg = flg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String ReturnMap(){
        String msg = "{\"code\":"+ flg +",\"message\":\"" + message+"\",\"data\":" + data+ "}";
        return msg;
    }
}
