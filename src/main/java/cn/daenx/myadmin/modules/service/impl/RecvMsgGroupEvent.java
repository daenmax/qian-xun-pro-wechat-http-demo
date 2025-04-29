package cn.daenx.myadmin.modules.service.impl;

import cn.daenx.myadmin.common.constant.EventConstant;
import cn.daenx.myadmin.modules.domain.BaseEventVo;
import cn.daenx.myadmin.modules.domain.event.RecvMsgReqVo;
import cn.daenx.myadmin.modules.service.EventHandleService;
import cn.daenx.myadmin.modules.utils.QianXunApi;
import cn.daenx.myadmin.modules.utils.QianXunText;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 收到群聊消息（10008）
 */
@Slf4j
@Service("event:" + EventConstant.recvMsgGroup)
public class RecvMsgGroupEvent implements EventHandleService {

    @Override
    public void handle(BaseEventVo baseEventVo, JSONObject jsonObject) {
        RecvMsgReqVo data = jsonObject.toJavaObject(RecvMsgReqVo.class);
        log.info("机器人wxid：{}，收到群聊消息，群wxid：{}，发送人wxid：{}，消息内容：{}", baseEventVo.getWxid(), data.getFromWxid(), data.getFinalFromWxid(), data.getMsg());
        if ("你好".equals(data.getMsg())) {
            String msg = QianXunText.at(data.getFinalFromWxid()) + " \\uD83D\\uDE00 你也好" + QianXunText.br() + "最近怎么样？";
            QianXunApi.sendText(baseEventVo.getWxid(), data.getFromWxid(), msg);
        }
    }
}
