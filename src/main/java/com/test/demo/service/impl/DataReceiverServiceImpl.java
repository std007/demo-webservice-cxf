package com.test.demo.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.test.demo.entity.ReceiveMsg;
import com.test.demo.entity.ResultMsg;
import com.test.demo.entity.UploadData;
import com.test.demo.service.IDataReceiverService;
import com.test.demo.util.ReceiveMsgUtil;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.util.List;
import java.util.Map;

@WebService(serviceName = "receiverService", // 与接口中指定的name一致
        targetNamespace = "http://service.demo.test.com", // 与接口中的命名空间一致,一般是接口的包名倒序排
        endpointInterface = "com.test.demo.service.IDataReceiverService"// 接口地址
)
@Slf4j
@Component
public class DataReceiverServiceImpl implements IDataReceiverService {

    /**
     * 接收数据接口
     *
     * @param receiveMsg 数据
     * @return
     */
    @Override
    public String getReceiveMsg(ReceiveMsg receiveMsg) {

        //解析报文，转化为需要写成文件的数据
        UploadData uploadData = new UploadData();
        String dataId = receiveMsg.getDATAID();
        uploadData.setDataId(dataId);
        //是否加密 （1 表示业务数据为密文传输，0 表示明文）
        int sec = Convert.toInt(receiveMsg.getSEC());
        uploadData.setHavePwd(sec);

        //数据为明文时
        if (1 == sec) {
            //解码业务报文
            try {
                //获取解密后的业务数据
                String data = ReceiveMsgUtil.decodeStr(receiveMsg.getBUSINESSCONTENT());
                List<Map<String, Object>> list = ReceiveMsgUtil.xmlToList(data);
                log.info("数据为list：{}", list);
                uploadData.setBusinessData(list);
                if (CollUtil.isEmpty(list)) {
                    return ResultMsg.getMessage(ResultMsg.BUSINESS_DATA_ANALYZE_ERROR);
                }
            } catch (DocumentException e) {
                log.info("xml数据格式不正确！", e);
                return ResultMsg.getMessage(ResultMsg.XML_ERROR);
            } catch (Exception e) {
                log.info("base64解码错!", e);
                return ResultMsg.getMessage(ResultMsg.BASE64_ERROR);
            }
        }

        return ResultMsg.getMessage(ResultMsg.SUCCESS);
//        return uploadData.getReplyData();
    }

}
