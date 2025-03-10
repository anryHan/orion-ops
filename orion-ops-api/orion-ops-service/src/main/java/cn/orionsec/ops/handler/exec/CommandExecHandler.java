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
package cn.orionsec.ops.handler.exec;

import cn.orionsec.kit.lang.constant.Letters;
import cn.orionsec.kit.lang.exception.DisabledException;
import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.Threads;
import cn.orionsec.kit.lang.utils.collect.Maps;
import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.kit.lang.utils.io.Streams;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.kit.net.host.SessionStore;
import cn.orionsec.kit.net.host.ssh.ExitCode;
import cn.orionsec.kit.net.host.ssh.command.CommandExecutor;
import cn.orionsec.kit.net.host.ssh.command.CommandExecutors;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.SchedulerPools;
import cn.orionsec.ops.constant.command.ExecStatus;
import cn.orionsec.ops.constant.common.StainCode;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.constant.message.MessageType;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.dao.CommandExecDAO;
import cn.orionsec.ops.entity.domain.CommandExecDO;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.handler.tail.TailSessionHolder;
import cn.orionsec.ops.service.api.MachineInfoService;
import cn.orionsec.ops.service.api.WebSideMessageService;
import cn.orionsec.ops.utils.Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

/**
 * 命令执行器 基类
 *
 * @author 
 * @version 1.0.0
 * @since 2021/6/7 17:17
 */
@Slf4j
public class CommandExecHandler implements IExecHandler {

    private static final CommandExecDAO commandExecDAO = SpringHolder.getBean(CommandExecDAO.class);

    private static final MachineInfoService machineInfoService = SpringHolder.getBean(MachineInfoService.class);

    private static final ExecSessionHolder execSessionHolder = SpringHolder.getBean(ExecSessionHolder.class);

    private static final TailSessionHolder tailSessionHolder = SpringHolder.getBean(TailSessionHolder.class);

    private static final WebSideMessageService webSideMessageService = SpringHolder.getBean(WebSideMessageService.class);

    private final Long execId;

    private CommandExecDO record;

    private MachineInfoDO machine;

    private SessionStore sessionStore;

    private CommandExecutor executor;

    private int exitCode;

    private String logPath;

    private OutputStream logOutputStream;

    private Date startTime, endTime;

    private volatile boolean terminated;

    protected CommandExecHandler(Long execId) {
        this.execId = execId;
    }

    @Override
    public void exec() {
        log.info("execHandler-提交 execId: {}", execId);
        Threads.start(this, SchedulerPools.EXEC_SCHEDULER);
    }

    @Override
    public void run() {
        log.info("execHandler-执行开始 execId: {}", execId);
        // 获取执行数据
        this.getExecData();
        // 检查状态
        if (record == null || !ExecStatus.WAITING.getStatus().equals(record.getExecStatus())) {
            return;
        }
        // 执行
        Exception ex = null;
        try {
            // 更新状态
            this.updateStatus(ExecStatus.RUNNABLE);
            execSessionHolder.addSession(execId, this);
            // 打开日志
            this.openLogger();
            // 打开executor
            this.sessionStore = machineInfoService.openSessionStore(machine);
            this.executor = sessionStore.getCommandExecutor(Strings.replaceCRLF(record.getExecCommand()));
            // 执行命令
            CommandExecutors.execCommand(executor, logOutputStream);
        } catch (Exception e) {
            ex = e;
        }
        // 回调
        try {
            if (terminated) {
                // 停止回调
                this.terminatedCallback();
            } else if (ex == null) {
                // 完成回调
                this.completeCallback();
            } else if (ex instanceof DisabledException) {
                // 机器未启用回调
                this.machineDisableCallback();
            } else {
                // 执行失败回调
                this.exceptionCallback(ex);
            }
        } finally {
            // 释放资源
            this.close();
        }
    }

    /**
     * 获取执行数据
     */
    private void getExecData() {
        this.record = commandExecDAO.selectById(execId);
        if (record == null) {
            return;
        }
        if (!ExecStatus.WAITING.getStatus().equals(record.getExecStatus())) {
            return;
        }
        // 查询机器信息
        this.machine = machineInfoService.selectById(record.getMachineId());
        // 设置日志信息
        File logFile = new File(Files1.getPath(SystemEnvAttr.LOG_PATH.getValue(), record.getLogPath()));
        Files1.touch(logFile);
        this.logPath = logFile.getAbsolutePath();
    }

    @Override
    public void write(String out) {
        executor.write(out);
    }

    @Override
    public void terminate() {
        log.info("execHandler-停止 execId: {}", execId);
        this.terminated = true;
        Streams.close(executor);
    }

    /**
     * 打开日志
     */
    @SneakyThrows
    private void openLogger() {
        // 打开日志流
        log.info("execHandler-打开日志流 {} {}", execId, logPath);
        File logFile = new File(logPath);
        this.logOutputStream = Files1.openOutputStreamFast(logFile);
        StringBuilder sb = new StringBuilder()
                .append(Utils.getStainKeyWords("# 准备执行命令", StainCode.GLOSS_GREEN))
                .append(Letters.LF)
                .append("@ssh:     ")
                .append(StainCode.prefix(StainCode.GLOSS_BLUE))
                .append(machine.getUsername()).append("@")
                .append(machine.getMachineHost()).append(":")
                .append(machine.getSshPort())
                .append(StainCode.SUFFIX)
                .append(Letters.LF);
        sb.append("执行用户: ")
                .append(Utils.getStainKeyWords(record.getUserName(), StainCode.GLOSS_BLUE))
                .append(Letters.LF);
        sb.append("执行任务: ")
                .append(Utils.getStainKeyWords(execId, StainCode.GLOSS_BLUE))
                .append(Letters.LF);
        sb.append("执行机器: ")
                .append(Utils.getStainKeyWords(machine.getMachineName(), StainCode.GLOSS_BLUE))
                .append(Letters.LF);
        sb.append("开始时间: ")
                .append(Utils.getStainKeyWords(Dates.format(startTime), StainCode.GLOSS_BLUE))
                .append(Letters.LF);
        String description = record.getDescription();
        if (!Strings.isBlank(description)) {
            sb.append("执行描述: ")
                    .append(Utils.getStainKeyWords(description, StainCode.GLOSS_BLUE))
                    .append(Letters.LF);
        }
        sb.append(Letters.LF)
                .append(Utils.getStainKeyWords("# 执行命令", StainCode.GLOSS_GREEN))
                .append(Letters.LF)
                .append(StainCode.prefix(StainCode.GLOSS_CYAN))
                .append(Utils.getEndLfWithEof(record.getExecCommand()))
                .append(StainCode.SUFFIX)
                .append(Utils.getStainKeyWords("# 开始执行", StainCode.GLOSS_GREEN))
                .append(Letters.LF);
        logOutputStream.write(Strings.bytes(sb.toString()));
        logOutputStream.flush();
    }

    /**
     * 停止回调
     */
    private void terminatedCallback() {
        log.info("execHandler-执行停止 execId: {}", execId);
        // 更新状态
        this.updateStatus(ExecStatus.TERMINATED);
        // 拼接日志
        StringBuilder log = new StringBuilder(Const.LF)
                .append(Utils.getStainKeyWords("# 命令执行停止", StainCode.GLOSS_YELLOW))
                .append(Letters.TAB)
                .append(Utils.getStainKeyWords(Dates.format(endTime), StainCode.GLOSS_BLUE))
                .append(Const.LF);
        this.appendLog(log.toString());
    }

    /**
     * 完成回调
     */
    private void completeCallback() {
        this.exitCode = executor.getExitCode();
        log.info("execHandler-执行完成 execId: {} exitCode: {}", execId, exitCode);
        // 更新状态
        this.updateStatus(ExecStatus.COMPLETE);
        // 拼接日志
        long used = endTime.getTime() - startTime.getTime();
        StringBuilder sb = new StringBuilder()
                .append(Letters.LF)
                .append(Utils.getStainKeyWords("# 命令执行完毕", StainCode.GLOSS_GREEN))
                .append(Letters.LF);
        sb.append("exitcode: ")
                .append(ExitCode.isSuccess(exitCode)
                        ? Utils.getStainKeyWords(exitCode, StainCode.GLOSS_BLUE)
                        : Utils.getStainKeyWords(exitCode, StainCode.GLOSS_RED))
                .append(Letters.LF);
        sb.append("结束时间: ")
                .append(Utils.getStainKeyWords(Dates.format(endTime), StainCode.GLOSS_BLUE))
                .append("  used ")
                .append(Utils.getStainKeyWords(Utils.interval(used), StainCode.GLOSS_BLUE))
                .append(" (")
                .append(StainCode.prefix(StainCode.GLOSS_BLUE))
                .append(used)
                .append("ms")
                .append(StainCode.SUFFIX)
                .append(")\n");
        this.appendLog(sb.toString());
        // 发送站内信
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.ID, record.getId());
        params.put(EventKeys.NAME, record.getMachineName());
        webSideMessageService.addMessage(MessageType.EXEC_SUCCESS, record.getId(), record.getUserId(), record.getUserName(), params);
    }

    /**
     * 机器未启用回调
     */
    private void machineDisableCallback() {
        log.info("execHandler-机器停用停止 execId: {}", execId);
        // 更新状态
        this.updateStatus(ExecStatus.TERMINATED);
        // 拼接日志
        StringBuilder log = new StringBuilder()
                .append(Const.LF)
                .append(Utils.getStainKeyWords("# 命令执行机器未启用", StainCode.GLOSS_YELLOW))
                .append(Letters.TAB)
                .append(Utils.getStainKeyWords(Dates.format(endTime), StainCode.GLOSS_BLUE))
                .append(Const.LF);
        this.appendLog(log.toString());
    }

    /**
     * 异常回调
     *
     * @param e e
     */
    private void exceptionCallback(Exception e) {
        log.error("execHandler-执行失败 execId: {}", execId, e);
        // 更新状态
        this.updateStatus(ExecStatus.EXCEPTION);
        // 拼接日志
        StringBuilder log = new StringBuilder()
                .append(Const.LF)
                .append(Utils.getStainKeyWords("# 命令执行异常", StainCode.GLOSS_RED))
                .append(Letters.TAB)
                .append(Utils.getStainKeyWords(Dates.format(endTime), StainCode.GLOSS_BLUE))
                .append(Letters.LF)
                .append(Exceptions.getStackTraceAsString(e))
                .append(Const.LF);
        this.appendLog(log.toString());
        // 发送站内信
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.ID, record.getId());
        params.put(EventKeys.NAME, record.getMachineName());
        webSideMessageService.addMessage(MessageType.EXEC_FAILURE, record.getId(), record.getUserId(), record.getUserName(), params);
    }

    @SneakyThrows
    private void appendLog(String log) {
        logOutputStream.write(Strings.bytes(log));
        logOutputStream.flush();
    }

    /**
     * 更新状态
     *
     * @param status status
     */
    private void updateStatus(ExecStatus status) {
        Date now = new Date();
        // 更新
        CommandExecDO update = new CommandExecDO();
        update.setId(execId);
        update.setExecStatus(status.getStatus());
        update.setUpdateTime(now);
        switch (status) {
            case RUNNABLE:
                this.startTime = now;
                update.setStartDate(now);
                break;
            case COMPLETE:
                this.endTime = now;
                update.setEndDate(now);
                update.setExitCode(exitCode);
                break;
            case EXCEPTION:
            case TERMINATED:
                this.endTime = now;
                update.setEndDate(now);
                break;
            default:
        }
        int effect = commandExecDAO.updateById(update);
        log.info("execHandler-更新状态 id: {}, status: {}, effect: {}", execId, status, effect);
    }

    @Override
    public void close() {
        log.info("execHandler-关闭 id: {}", execId);
        // 移除会话
        execSessionHolder.removeSession(execId);
        // 释放资源
        Streams.close(executor);
        Streams.close(sessionStore);
        Streams.close(logOutputStream);
        // 异步关闭正在tail的日志
        tailSessionHolder.asyncCloseTailFile(Const.HOST_MACHINE_ID, logPath);
    }

}
