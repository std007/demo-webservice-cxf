package com.test.demo.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @description: 上传数据
 * @author: zhanget
 **/
@Data
public class UploadData {
    /**
     * id
     */
    private String dataId;
    /**
     * 数据上传时间
     */
    private String time;
    /**
     * 数据类型 00:请求数据;01:配置数据;
     */
    private String type;
    /**
     * 是否加密（1:密文;0:明文）
     */
    private int havePwd;
    /**
     * 返回数据
     *
     * @see com.test.demo.entity.ResultMsg
     */
    private String replyData;
    /**
     * 业务数据
     */
    private List<Map<String, Object>> businessData;

}
