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
package cn.orionsec.ops.handler.tail.impl;

import cn.orionsec.kit.ext.tail.Tracker;
import cn.orionsec.kit.ext.tail.delay.DelayTrackerListener;
import cn.orionsec.kit.ext.tail.handler.DataHandler;
import cn.orionsec.kit.ext.tail.mode.FileNotFoundMode;
import cn.orionsec.kit.ext.tail.mode.FileOffsetMode;
import cn.orionsec.kit.lang.define.thread.HookRunnable;
import cn.orionsec.kit.lang.utils.Threads;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.SchedulerPools;
import cn.orionsec.ops.constant.ws.WsCloseCode;
import cn.orionsec.ops.handler.tail.ITailHandler;
import cn.orionsec.ops.handler.tail.TailFileHint;
import cn.orionsec.ops.utils.WebSockets;
import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * tracker 方式
 *
 * @author 
 * @version 1.0.0
 * @since 2021/6/18 17:36
 */
@Slf4j
public class TrackerTailFileHandler implements ITailHandler, DataHandler {

    @Getter
    private final String token;

    /**
     * session
     */
    private final WebSocketSession session;

    /**
     * hint
     */
    private final TailFileHint hint;

    private DelayTrackerListener tracker;

    private volatile boolean close;

    @Getter
    private final String filePath;

    public TrackerTailFileHandler(TailFileHint hint, WebSocketSession session) {
        this.token = hint.getToken();
        this.hint = hint;
        this.filePath = hint.getPath();
        this.session = session;
        log.info("tail TRACKER 监听文件初始化 token: {}, hint: {}", token, JSON.toJSONString(hint));
    }

    @Override
    public void start() {
        this.tracker = new DelayTrackerListener(filePath, this);
        tracker.delayMillis(hint.getDelay());
        tracker.charset(hint.getCharset());
        tracker.offset(FileOffsetMode.LINE, hint.getOffset());
        tracker.notFoundMode(FileNotFoundMode.WAIT_COUNT, 10);
        Threads.start(new HookRunnable(() -> {
            log.info("tail TRACKER 开始监听文件 token: {}", token);
            tracker.tail();
        }, this::callback), SchedulerPools.TAIL_SCHEDULER);
    }

    @Override
    public void setLastModify() {
        tracker.setFileLastModifyTime();
    }

    @Override
    public Long getMachineId() {
        return Const.HOST_MACHINE_ID;
    }

    /**
     * 回调
     */
    @SneakyThrows
    private void callback() {
        log.info("tail TRACKER 监听文件结束 token: {}", token);
        WebSockets.close(session, WsCloseCode.EOF);
    }

    @SneakyThrows
    @Override
    public void read(byte[] bytes, int len, Tracker tracker) {
        if (session.isOpen()) {
            session.sendMessage(new BinaryMessage(bytes, 0, len, true));
        }
    }

    @Override
    @SneakyThrows
    public void close() {
        if (close) {
            return;
        }
        this.close = true;
        if (tracker != null) {
            tracker.stop();
        }
    }

    @Override
    public String toString() {
        return filePath;
    }

}
