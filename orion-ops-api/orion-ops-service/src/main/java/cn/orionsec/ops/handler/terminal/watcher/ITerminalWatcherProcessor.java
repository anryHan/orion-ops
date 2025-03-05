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
package cn.orionsec.ops.handler.terminal.watcher;

import cn.orionsec.kit.lang.able.SafeCloseable;
import cn.orionsec.kit.lang.able.Watchable;
import org.springframework.web.socket.WebSocketSession;

/**
 * terminal watcher 处理器接口
 *
 * @author 
 * @version 1.0.0
 * @since 2022/7/29 11:27
 */
public interface ITerminalWatcherProcessor extends Watchable, SafeCloseable {

    /**
     * 发送消息
     *
     * @param message message
     */
    void sendMessage(byte[] message);

    /**
     * 添加 watcher
     *
     * @param session session
     */
    void addWatcher(WebSocketSession session);

    /**
     * 移除 watcher
     *
     * @param id id
     */
    void removeWatcher(String id);

}
