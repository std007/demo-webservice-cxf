package com.test.demo.util;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据处理
 *
 * @author zhangt
 */
@Slf4j
public class ReceiveMsgUtil {

    /**
     * xml转换为list
     *
     * @param xmlStr
     * @return
     * @throws DocumentException
     */
    public static List<Map<String, Object>> xmlToList(String xmlStr) throws DocumentException {

        Document document = DocumentHelper.parseText(xmlStr);
        Element root = document.getRootElement();
        List<Element> elements = root.elements();
        if (CollUtil.isEmpty(elements)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> list = new ArrayList(elements.size());
        elements.forEach(element -> {
            List<Element> subElements = element.elements();
            if (CollUtil.isNotEmpty(subElements) && subElements.size() > 1) {
                Map<String, Object> subMap = new HashMap<>(subElements.size());
                subElements.forEach(subElement -> subMap.put(subElement.getName(), subElement.getText()));
                list.add(subMap);
            }
        });
        return list;
    }

    /**
     * 创建xml 返回xml字符串
     * 注：不含头信息 <?xml version="1.0" encoding="UTF-8"?>
     *
     * @param rootKey 根节点key
     * @param subKey  子节点key
     * @param list    需要转换的数据
     * @return
     */
    private static String createXml(String rootKey, String subKey, List<Map<String, String>> list) {
        //DocumentHelper创建document对象
        Document doc = DocumentHelper.createDocument();
        //创建根节点
        Element root = doc.addElement(rootKey);
        //遍历集合 创建子节点
        for (Map<String, String> map : list) {
            Element subElement = root.addElement(subKey);
            map.forEach((key, value) -> {
                Element element = subElement.addElement(key);
                element.addEntity(key, value);
            });
        }
        //去掉头信息
        return doc.getRootElement().asXML();
    }


    /**
     * base64加密
     *
     * @param str
     * @return
     */
    public static String encodeStr(String str) {
        return Base64Encoder.encode(str, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * base64解密
     *
     * @param str
     * @return
     */
    public static String decodeStr(String str) {
        return Base64Decoder.decodeStr(str, CharsetUtil.CHARSET_UTF_8);
    }


    /**
     * MAC算法可选以下多种算法
     *
     * <pre>
     * HmacMD5/HmacSHA1/HmacSHA256
     * HmacSHA384
     * HmacSHA512
     * </pre>
     */
    public static final String KEY_MAC = "HmacMD5";

    /**
     * HMAC加密
     *
     * @param data 待加密数据
     * @param key  秘钥
     * @return
     * @throws Exception
     */
    public static byte[] encryptHMAC(byte[] data, String key) throws Exception {

        SecretKey secretKey = new SecretKeySpec(key.getBytes(), KEY_MAC);
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        return mac.doFinal(data);
    }

    /**
     * HMAC加密
     * algorithms: MD5/SHA-1/SHA-256
     *
     * @param data 待加密数据
     * @return 摘要信息
     */
    public static byte[] encryptHMAC(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data);
        return md.digest();
    }

    /**
     * byte数组转换为HexString
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString();
    }

    /**
     * 通过签名检验数据是否一致
     *
     * @param data 数据
     * @param sign 数据的签名
     * @return
     * @throws Exception
     */
    public static boolean HMACcheck(String data, String sign) throws Exception {

        String dataSign = byteArrayToHexString(encryptHMAC(data.getBytes()));
        log.info("上传签名：{}，生成签名{}", sign, dataSign);
        return sign.equals(dataSign);

    }

    /**
     * 通过签名检验数据是否一致
     *
     * @param data
     * @param sign
     * @param secretKey
     * @return
     * @throws Exception
     */
    public static boolean HMACcheck(String data, String sign, String secretKey) throws Exception {

        String dataSign = byteArrayToHexString(encryptHMAC(data.getBytes(), secretKey));
        log.info("上传签名:{}，生成签名:{}", sign, dataSign);
        return sign.equals(dataSign);
    }


    /**
     * 获取解密后的业务数据
     *
     * @param sec  加密标识（1 表示业务数据为密文传输，0 表示明文）
     * @param data
     * @return
     * @throws Exception
     */
    public static List<Map<String, Object>> getData(int sec, String data) throws DocumentException {
        if (sec == 1) {
            data = decodeStr(data);
        }
        return xmlToList(data);
    }


    public static void main(String[] args) throws Exception {
        List<Map<String, String>> list = new ArrayList();
        for (int i = 0; i < 2; i++) {
            Map<String, String> map = new HashMap<>(4);
            map.put("key", "val" + i);
            map.put("kk", "val" + i);
            list.add(map);
        }
        String xml = createXml("rows", "row", list);
//        xml = "<rows><row><ID>000001</ID><DATE>20150626092315</DATE><AL>1:2;2:1;3:2;4:0;5:0;6:0;7:0;8:0;</AL><MB>0</MB><YZ>0</YZ><YGLY>0</YGLY><PVZT>0</PVZT><PVLJZT>0</PVLJZT><HCLZT>0</HCLZT>T></row></rows>";
        log.info("xml = " + xml);
        String encode = encodeStr(xml);
        log.info("encode:{}", encode);

        log.info("签名1：{}", byteArrayToHexString(encryptHMAC(encode.getBytes())));
        //签名1：93998aea8696bd841787a9478ed7e73e
        String secretKey = "1234567890abcdef";
//        HMACcheck(encode, "93998aea8696bd841787a9478ed7e73e");
        log.info("签名2：{}", byteArrayToHexString(encryptHMAC(encode.getBytes(), secretKey)));
        //签名2：964302be5d08895ce16d5fe5d51e5a14
//        HMACcheck(encode, "964302be5d08895ce16d5fe5d51e5a14", secretKey);

        String decode = decodeStr(encode);
        log.info("decode = " + decode);

        List<Map<String, Object>> list1 = xmlToList(decode);
        log.info("list1 = " + list1);

        //签名
        String inputStr = "123456789il7B0BSEjFdzpyKzfOFpvg/Se1CP802RItKYFPfSLRxJ3jf0bVl9hvYOEktPAYW2nd7S8MBcyHYyacHKbISq5iTmDzG+ivnR+SZJv3USNTYVMz9rCQVSxd0cLlqsJauko79NnwQJbzDTyLooYoIwz75qBOH2/xOMirpeEqRJrF/EQjWekJmGk9RtboXePu2rka+Xm51syBPhiXJAq0GfbfaFu9tNqs/e2Vjja/ltE1M0lqvxfXQ6da6HrThsm5id4ClZFIi0acRfrsPLRixS/IQYtksxghvJwbqOsbIsITail9Ayy4tKcogeEZiOO+4Ed264NSKmk7l3wKwJLAFjCFogBx8GE3OBz4pqcAn/ydA=201607291424000001";
        byte[] inputData = inputStr.getBytes();
        String key = "1234567890abcdef";
        log.info(byteArrayToHexString(encryptHMAC(inputData, key)));
    }

    public static void main2(String[] args) {
        String str = "0 20210909135700 20210909135700 3 4 5 6 7 8";
//        20210909135700
        byte[] bytes = str.getBytes(StandardCharsets.US_ASCII);
        System.out.println("bytes = " + bytes);

        String s = "20150626092315";
        DateTime time = DateUtil.parse(s, "yyyyMMddHHmmss");
        System.out.println("time = " + time);
    }

}
