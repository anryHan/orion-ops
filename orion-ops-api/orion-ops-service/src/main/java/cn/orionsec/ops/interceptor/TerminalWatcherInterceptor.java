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
package cn.orionsec.ops.interceptor;

import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.ops.constant.KeyConst;
import cn.orionsec.ops.entity.dto.terminal.TerminalWatcherDTO;
import cn.orionsec.ops.utils.WebSockets;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import java.util.Map;

/**
 * terminal 监视拦截器
 *
 * @author 
 * @version 1.0.0
 * @since 2022/7/29 10:48
 */
@Component
@Slf4j
public class TerminalWatcherInterceptor implements HandshakeInterceptor {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // 获取 token
        String token = WebSockets.getToken(request);
        String tokenKey = Strings.format(KeyConst.TERMINAL_WATCHER_TOKEN, token);
        String tokenValue = redisTemplate.opsForValue().get(tokenKey);
        boolean access = false;
        if (!Strings.isBlank(tokenValue)) {
            access = true;
            TerminalWatcherDTO watcher = JSON.parseObject(tokenValue, TerminalWatcherDTO.class);
            attributes.put(WebSockets.UID, watcher.getUserId());
            attributes.put(WebSockets.TOKEN, watcher.getToken());
            attributes.put(WebSockets.READONLY, watcher.getReadonly());
            // 删除 token
            redisTemplate.delete(tokenKey);
        }
        log.info("terminal尝试监视ws连接开始 token: {}, 结果: {}", token, access);
        return access;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

}
