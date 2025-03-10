/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.handler.webhook;

import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.kit.lang.utils.collect.Lists;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 钉钉机器人推送器
 *
 * @author 
 * @version 1.0.0
 * @since 2022/8/29 18:19
 */
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingRobotPusher implements IWebhookPusher {

    /**
     * 推送 url
     */
    private String url;

    /**
     * 推送标题
     */
    private String title;

    /**
     * 推送内容
     */
    private String text;

    /**
     * @ 用户的手机号
     */
    private List<String> atMobiles;

    @Override
    public void push() {
        OapiRobotSendRequest.Markdown content = new OapiRobotSendRequest.Markdown();
        content.setTitle(title);
        content.setText(text);
        // 执行推送请求
        DingTalkClient client = new DefaultDingTalkClient(url);
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");
        request.setMarkdown(content);
        if (!Lists.isEmpty(atMobiles)) {
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            at.setAtMobiles(atMobiles);
            request.setAt(at);
        }
        try {
            OapiRobotSendResponse response = client.execute(request);
            if (!response.isSuccess()) {
                log.error("钉钉机器人推送失败 url: {}", url);
            }
        } catch (Exception e) {
            log.error("钉钉机器人推送异常 url: {}", url, e);
            throw Exceptions.httpRequest(url, "ding push error", e);
        }
    }

}
