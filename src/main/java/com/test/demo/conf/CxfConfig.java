package com.test.demo.conf;

import com.test.demo.service.IDataReceiverService;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

/**
 * 〈cxf配置〉
 * 默认:services
 * http://127.0.0.1:6689/services/receiveData
 * cxf-path配置路径：
 * http://127.0.0.1:6689/soap/receiveData
 */
@Configuration
public class CxfConfig {

    @Autowired
    private Bus bus;

    @Autowired
    private IDataReceiverService dataReceiverService;


    /**
     * 数据站点服务（总入口）
     *
     * @return
     */
    @Bean
    public Endpoint getReceiveData() {
        EndpointImpl endpoint = new EndpointImpl(bus, dataReceiverService);
        endpoint.publish("/receiveData");
        return endpoint;
    }

}
