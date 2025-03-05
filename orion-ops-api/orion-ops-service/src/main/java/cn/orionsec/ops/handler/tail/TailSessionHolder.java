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
package cn.orionsec.ops.handler.tail;

import cn.orionsec.kit.lang.utils.Threads;
import cn.orionsec.kit.lang.utils.collect.Lists;
import cn.orionsec.kit.lang.utils.collect.Maps;
import cn.orionsec.ops.constant.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * tail 会话持有者
 *
 * @author 
 * @version 1.0.0
 * @since 2021/6/18 17:34
 */
@Slf4j
@Component
public class TailSessionHolder {

    /**
     * key: token
     * value: ITailHandler
     */
    private final Map<String, ITailHandler> holder = Maps.newCurrentHashMap();

    /**
     * key: machineId:filePath
     * value: token
     */
    private final Map<String, List<String>> fileTokenMapping = Maps.newCurrentHashMap();

    /**
     * 添加session
     *
     * @param token   token
     * @param session session
     */
    public void addSession(String token, ITailHandler session) {
        holder.put(token, session);
        fileTokenMapping.computeIfAbsent(session.getMachineId() + ":" + session.getFilePath(), s -> Lists.newList()).add(token);
    }

    /**
     * 获取session
     *
     * @param token token
     * @return session
     */
    public ITailHandler getSession(String token) {
        return holder.get(token);
    }

    /**
     * 获取session
     *
     * @param machineId machineId
     * @param path      path
     * @return session
     */
    public List<ITailHandler> getSession(Long machineId, String path) {
        List<String> tokenList = fileTokenMapping.get(machineId + ":" + path);
        if (Lists.isEmpty(tokenList)) {
            return Lists.empty();
        }
        return tokenList.stream()
                .map(holder::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 删除session
     *
     * @param token token
     * @return session
     */
    public ITailHandler removeSession(String token) {
        ITailHandler handler = holder.remove(token);
        if (handler != null) {
            fileTokenMapping.remove(handler.getMachineId() + ":" + handler.getFilePath());
        }
        return handler;
    }

    /**
     * 异步关闭进行中的 tail
     *
     * @param machineId machineId
     * @param path      path
     */
    public void asyncCloseTailFile(Long machineId, String path) {
        Threads.start(() -> {
            try {
                Threads.sleep(Const.MS_S_1);
                this.getSession(machineId, path).forEach(ITailHandler::setLastModify);
                Threads.sleep(Const.MS_S_5);
                this.getSession(machineId, path).forEach(ITailHandler::close);
            } catch (Exception e) {
                log.error("关闭tailingFile失败 machineId: {}, path: {}", machineId, path, e);
            }
        });
    }

}
