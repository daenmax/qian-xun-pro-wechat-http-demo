package cn.daenx.myadmin.modules.utils;

import cn.daenx.myadmin.modules.domain.BaseReqVo;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 千寻微信框架Pro 接口请求工具类
 */
@Component
@Slf4j
public class QianXunApi {
    public static String url;

    @Value("${qian-xun-pro.url}")
    public void setUrl(String url) {
        QianXunApi.url = url;
    }

    /**
     * 发送文本消息（sendText）
     *
     * @param robotWxid 要使用哪个微信机器人的wxid
     * @param wxid      对方wxid 要给谁，支持好友、群聊、公众号等
     * @param msg       消息内容
     * @return
     */
    public static JSONObject sendText(String robotWxid, String wxid, String msg) {
        JSONObject req = new JSONObject();
        req.put("wxid", wxid);
        req.put("msg", msg);
        String reqBody = JSONUtil.toJsonStr(new BaseReqVo("sendText", req));
        String reqUrl = url + "?wxid=" + robotWxid;
        String resStr = HttpUtil.createPost(reqUrl).body(reqBody).execute().body();
        JSONObject res = JSONObject.parseObject(resStr);
        boolean isFail = res == null || 200 != res.getInteger("code");
        log.info((isFail ? "请求失败->" : "请求成功->" )+ "请求地址：{}\n请求报文：{}\n响应报文：{}", reqUrl, reqBody, resStr);
        return res;
    }

    /**
     * 发送文件（sendFile）
     *
     * @param robotWxid 要使用哪个微信机器人的wxid
     * @param wxid      对方wxid 要给谁，支持好友、群聊、公众号等
     * @param path       文件本地路径或者网络直链或者base64，本地文件时，如果D:\daen\restart.sh提示文件不存在，就用D:/daen/restart.sh试试
     * base64时，需要以 base64, 开头，例如：base64,iVBORw0KGgoAAAANSUh
     * @param fileName       保存文件名，当path是网络直链时，必填
     * @return
     */
    public static JSONObject sendFile(String robotWxid, String wxid, String path, String fileName) {
        JSONObject req = new JSONObject();
        req.put("wxid", wxid);
        req.put("path", path);
        req.put("fileName", fileName);
        String reqBody = JSONUtil.toJsonStr(new BaseReqVo("sendFile", req));
        String reqUrl = url + "?wxid=" + robotWxid;
        String resStr = HttpUtil.createPost(reqUrl).body(reqBody).execute().body();
        JSONObject res = JSONObject.parseObject(resStr);
        boolean isFail = res == null || 200 != res.getInteger("code");
        log.info((isFail ? "请求失败->" : "请求成功->" )+ "请求地址：{}\n请求报文：{}\n响应报文：{}", reqUrl, reqBody, resStr);
        return res;
    }

    /**
     * 退还收款（returnTrans）
     *
     * @param robotWxid     要使用哪个微信机器人的wxid
     * @param wxid          对方wxid
     * @param transcationid 从事件中获取
     * @param transferid    从事件中获取
     * @return
     */
    public static JSONObject returnTrans(String robotWxid, String wxid, String transcationid, String transferid) {
        JSONObject req = new JSONObject();
        req.put("wxid", wxid);
        req.put("transcationid", transcationid);
        req.put("transferid", transferid);
        String reqBody = JSONUtil.toJsonStr(new BaseReqVo("returnTrans", req));
        String reqUrl = url + "?wxid=" + robotWxid;
        String resStr = HttpUtil.createPost(reqUrl).body(reqBody).execute().body();
        JSONObject res = JSONObject.parseObject(resStr);
        boolean isFail = res == null || 200 != res.getInteger("code");
        log.info(isFail ? "请求失败" : "请求成功->" + "请求地址：{}\n请求报文：{}\n响应报文：{}", reqUrl, reqBody, resStr);
        return res;
    }


    // 其他更多接口同上，请自行添加
}
