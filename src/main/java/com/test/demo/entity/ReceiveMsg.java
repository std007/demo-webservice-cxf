package com.test.demo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 接收报文
 */
@Data
public class ReceiveMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通信协议版本
     */
    private String VERSION;
    /**
     * 数据序号（6 位）
     */
    private String DATAID;
    /**
     * 加密标识（1 表示业务数据为密文传输，0 表示明文）
     */
    private String SEC;
    /**
     * 业务报文（数据需转化为 base64 编码）
     */
    private String BUSINESSCONTENT;
    /**
     * 整个安全报文的 HMAC 校验码（数据需转化为 base64 编码）
     */
    private String HMAC;

}
