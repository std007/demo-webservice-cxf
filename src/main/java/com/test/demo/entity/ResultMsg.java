package com.test.demo.entity;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

import java.util.Date;

public class ResultMsg {

    //上传成功
    public static final String SUCCESS = "01";
    //base64解码错
    public static final String BASE64_ERROR = "1";
    //xml报文解码错
    public static final String XML_ERROR = "2";
    //业务数据解析错误
    public static final String BUSINESS_DATA_ANALYZE_ERROR = "3";
    //其他异常
    public static final String OTHER_ERROR = "4";

    /**
     * 获取返回信息
     *
     * @param code 返回数据编码
     */
    public static String getMessage(String code) {
        String message = null;
        switch (code) {
            //上传成功 0
            case SUCCESS:
                message = code;
                break;
            case BASE64_ERROR:
            case XML_ERROR:
            case BUSINESS_DATA_ANALYZE_ERROR:
                message = code;
                break;
            case OTHER_ERROR:
                message = code + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
                break;
        }
        return message;
    }


}
