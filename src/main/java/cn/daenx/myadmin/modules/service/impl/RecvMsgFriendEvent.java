package cn.daenx.myadmin.modules.service.impl;

import cn.daenx.myadmin.common.constant.EventConstant;
import cn.daenx.myadmin.modules.domain.BaseEventVo;
import cn.daenx.myadmin.modules.domain.event.RecvMsgReqVo;
import cn.daenx.myadmin.modules.service.EventHandleService;
import cn.daenx.myadmin.modules.utils.EmojiEncoder;
import cn.daenx.myadmin.modules.utils.QianXunApi;
import cn.daenx.myadmin.modules.utils.QianXunText;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 收到私聊消息（10009）
 */
@Slf4j
@Service("event:" + EventConstant.recvMsgFriend)
public class RecvMsgFriendEvent implements EventHandleService {

    @Override
    public void handle(BaseEventVo baseEventVo, JSONObject jsonObject) {
        RecvMsgReqVo data = jsonObject.toJavaObject(RecvMsgReqVo.class);
        log.info("机器人wxid：{}，收到私聊消息，发送人wxid：{}，消息内容：{}", baseEventVo.getWxid(), data.getFromWxid(), data.getMsg());
        if ("你好".equals(data.getMsg())) {
            //在发消息时，需要把emoji转换为UTF-16的编码，例如"😀"需要转换为"\uD83D\uDE00"，下面提供转换的演示

            //使用编码过的emoji消息格式
            String msg = " \\uD83D\\uDE00 你也好" + QianXunText.br() + "嘿嘿";
            QianXunApi.sendText(baseEventVo.getWxid(), data.getFromWxid(), msg);

            //使用未编码过的emoji消息格式，我们自己编码成符合千寻API要求的格式
            String msg2 = " 😀 你也好" + QianXunText.br() + "嘿嘿";
            msg2 = EmojiEncoder.encodeEmojisOnly(msg2);
            QianXunApi.sendText(baseEventVo.getWxid(), data.getFromWxid(), msg2);
        }
        if ("发送文件".equals(data.getMsg())) {
            String path = "C:\\Users\\Microsoft\\Desktop\\测试文件.txt";
            QianXunApi.sendFile(baseEventVo.getWxid(), data.getFromWxid(), path, null);
        }
    }
}
