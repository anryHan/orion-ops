package com.orion.ops.handler.terminal;

import com.alibaba.fastjson.JSON;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.consts.terminal.TerminalConst;
import com.orion.ops.consts.terminal.TerminalOperate;
import com.orion.ops.consts.ws.WsCloseCode;
import com.orion.ops.consts.ws.WsProtocol;
import com.orion.ops.entity.domain.MachineTerminalLogDO;
import com.orion.ops.entity.dto.TerminalConnectDTO;
import com.orion.ops.entity.dto.TerminalDataTransferDTO;
import com.orion.ops.service.api.MachineTerminalService;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.ssh.BaseRemoteExecutor;
import com.orion.remote.channel.ssh.ShellExecutor;
import com.orion.spring.SpringHolder;
import com.orion.utils.Arrays1;
import com.orion.utils.Objects1;
import com.orion.utils.Strings;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.utils.time.Dates;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 终端处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/17 22:46
 */
@Slf4j
public class TerminalOperateHandler implements IOperateHandler {

    private static final MachineTerminalService machineTerminalService = SpringHolder.getBean("machineTerminalService");

    @Getter
    private String token;

    /**
     * 终端配置
     */
    @Getter
    private TerminalConnectHint hint;

    /**
     * ws
     */
    private WebSocketSession session;

    /**
     * sessionStore
     */
    private SessionStore sessionStore;

    /**
     * 执行器
     */
    private ShellExecutor executor;

    /**
     * 日志流
     */
    private OutputStream logStream;

    /**
     * 最后一次发送心跳的时间
     */
    private volatile long lastPing;

    protected volatile boolean close;

    public TerminalOperateHandler(String token, TerminalConnectHint hint, WebSocketSession session, SessionStore sessionStore) {
        this.token = token;
        this.hint = hint;
        this.session = session;
        this.sessionStore = sessionStore;
        this.lastPing = System.currentTimeMillis();
        this.init();
    }

    /**
     * 打开session
     */
    private void init() {
        this.executor = sessionStore.getShellExecutor();
        executor.terminalType(hint.getTerminalType());
        executor.size(hint.getCols(), hint.getRows(), hint.getWidth(), hint.getHeight());
        String logPath = "/" + TerminalConst.TERMINAL + "/"
                + Dates.current(Dates.YMDHMS2) + "_" + MachineTerminalService.getTokenUserId(token) + ".log";
        String realLogPath = Files1.getPath(MachineEnvAttr.LOG_PATH.getValue() + logPath);
        this.logStream = Files1.openOutputStreamSafe(realLogPath);
        log.info("terminal 开始记录用户操作日志: {} {}", token, logPath);
        // 记录日志
        MachineTerminalLogDO logEntity = new MachineTerminalLogDO();
        logEntity.setAccessToken(token);
        logEntity.setUserId(hint.getUserId());
        logEntity.setUsername(hint.getUsername());
        logEntity.setMachineId(hint.getMachineId());
        logEntity.setMachineHost(hint.getMachineHost());
        logEntity.setConnectedTime(hint.getConnectedTime());
        logEntity.setOperateLogFile(logPath);
        Long logId = machineTerminalService.addAccessLog(logEntity);
        hint.setLogId(logId);
        log.info("terminal 用户操作日志入库: {} logId: {}", token, logId);
    }

    @Override
    public void connect() {
        executor.connect()
                .scheduler(SchedulerPools.TERMINAL_SCHEDULER)
                .callback(this::callback)
                .streamHandler(this::streamHandler)
                .exec();
    }

    /**
     * 回调
     *
     * @param executor executor
     */
    private void callback(BaseRemoteExecutor executor) {
        if (close) {
            return;
        }
        this.sendClose(WsCloseCode.EOF_CALLBACK);
        log.info("terminal eof回调 {}", token);
    }

    /**
     * 标准输入处理
     *
     * @param executor    executor
     * @param inputStream stream
     */
    private void streamHandler(BaseRemoteExecutor executor, InputStream inputStream) {
        byte[] bs = new byte[Const.BUFFER_KB_4];
        BufferedInputStream in = new BufferedInputStream(inputStream, Const.BUFFER_KB_4);
        int read;
        try {
            while (session.isOpen() && (read = in.read(bs)) != -1) {
                session.sendMessage(new TextMessage(WsProtocol.OK.msg(Arrays1.resize(bs, read))));
            }
        } catch (IOException ex) {
            if (session.isOpen()) {
                try {
                    session.close(WsCloseCode.READ_EXCEPTION.close());
                } catch (Exception ex1) {
                    log.error("terminal 处理流失败 关闭连接失败", ex1);
                }
            } else {
                log.error("terminal 读取流失败", ex);
            }
        }
    }

    @Override
    public void disconnect() {
        if (close) {
            return;
        }
        this.close = true;
        try {
            Streams.close(logStream);
            Streams.close(executor);
            Streams.close(sessionStore);
        } catch (Exception e) {
            log.error("terminal 断开连接 失败 token: {}, {}", token, e);
        }
    }

    @Override
    public void forcedOffline() throws Exception {
        session.close(WsCloseCode.FORCED_OFFLINE.close());
        log.info("terminal 管理员强制断连 {}", token);
    }

    @Override
    public void heartDown() throws Exception {
        session.close(WsCloseCode.HEART_DOWN.close());
        log.info("terminal 心跳结束断连 {}", token);
    }

    @Override
    public boolean valid(String id) {
        return Objects1.eq(hint.getSessionId(), id);
    }

    @Override
    public boolean isDown() {
        return (System.currentTimeMillis() - lastPing) > TerminalConst.TERMINAL_CONNECT_DOWN;
    }

    @Override
    public void handleMessage(TerminalDataTransferDTO data, TerminalOperate operate) throws Exception {
        if (close) {
            return;
        }
        switch (operate) {
            case PING:
                this.lastPing = System.currentTimeMillis();
                session.sendMessage(new TextMessage(WsProtocol.PONG.get()));
                return;
            case DISCONNECT:
                this.sendClose(WsCloseCode.DISCONNECT);
                log.info("terminal 用户主动断连 {}", token);
                return;
            case RESIZE:
                this.resize(data.getBody());
                return;
            default:
                this.handleData(data, operate);
        }
    }

    /**
     * 处理用户操作
     *
     * @param data    data
     * @param operate 操作
     */
    private void handleData(TerminalDataTransferDTO data, TerminalOperate operate) {
        String body = data.getBody();
        if (body == null) {
            return;
        }
        byte[] bs;
        switch (operate) {
            case KEY:
                bs = Strings.bytes(body);
                break;
            case COMMAND:
                bs = Strings.bytes(body + Const.LF);
                break;
            case INTERRUPT:
                bs = new byte[]{3, 10};
                break;
            case HANGUP:
                bs = new byte[]{24, 10};
                break;

            default:
                return;
        }
        try {
            logStream.write(bs);
            executor.write(bs);
        } catch (Exception e) {
            log.info("terminal 处理operate失败 token: {} {}", token, e);
        }
    }

    /**
     * 重置大小
     */
    private void resize(String body) {
        // 检查参数
        TerminalConnectDTO window = null;
        try {
            window = JSON.parseObject(body, TerminalConnectDTO.class);
        } catch (Exception e) {
            // ignore
        }
        if (window == null) {
            try {
                session.sendMessage(new TextMessage(WsProtocol.ARGUMENT.get()));
            } catch (Exception e) {
                // ignore
            }
            return;
        }
        hint.setCols(window.getCols());
        hint.setRows(window.getRows());
        hint.setWidth(window.getWidth());
        hint.setHeight(window.getHeight());
        executor.size(window.getCols(), window.getRows(), window.getWidth(), window.getHeight());
    }

    /**
     * 发送关闭连接命令
     *
     * @param code close
     */
    private void sendClose(WsCloseCode code) {
        if (session.isOpen()) {
            try {
                session.close(code.close());
            } catch (IOException e) {
                log.error("terminal 发送断开连接命令 失败 token: {}, {}", token, e);
            }
        }
    }

}
