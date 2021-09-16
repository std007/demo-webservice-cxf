package com.test.demo.service;


import com.test.demo.entity.ReceiveMsg;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * 接收数据处理服务类
 *
 * @Author: zhangt
 * @Date: 2021/9/11 11:46
 */

@WebService
public interface IDataReceiverService {


    /**
     * 接收数据接口
     *
     * @param receiveMsg 数据
     * @return
     */
    @WebMethod
    String getReceiveMsg(@WebParam(name = "ROOT") ReceiveMsg receiveMsg);

}
