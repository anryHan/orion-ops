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
package cn.orionsec.ops.handler.scheduler.machine;

import cn.orionsec.kit.lang.constant.Letters;
import cn.orionsec.kit.lang.exception.DisabledException;
import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.kit.lang.utils.io.Streams;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.kit.net.host.SessionStore;
import cn.orionsec.kit.net.host.ssh.ExitCode;
import cn.orionsec.kit.net.host.ssh.command.CommandExecutor;
import cn.orionsec.kit.net.host.ssh.command.CommandExecutors;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.common.StainCode;
import cn.orionsec.ops.constant.scheduler.SchedulerTaskMachineStatus;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.dao.SchedulerTaskMachineRecordDAO;
import cn.orionsec.ops.entity.domain.SchedulerTaskMachineRecordDO;
import cn.orionsec.ops.handler.tail.TailSessionHolder;
import cn.orionsec.ops.service.api.MachineInfoService;
import cn.orionsec.ops.utils.Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.OutputStream;
import java.util.Date;

/**
 * 任务机器操作
 *
 * @author 
 * @version 1.0.0
 * @since 2022/2/24 22:22
 */
@Slf4j
public class TaskMachineHandler implements ITaskMachineHandler {

    private static final SchedulerTaskMachineRecordDAO schedulerTaskMachineRecordDAO = SpringHolder.getBean(SchedulerTaskMachineRecordDAO.class);

    private static final MachineInfoService machineInfoService = SpringHolder.getBean(MachineInfoService.class);

    private static final TailSessionHolder tailSessionHolder = SpringHolder.getBean(TailSessionHolder.class);

    private final Long machineRecordId;

    private String logPath;

    private OutputStream logOutputStream;

    private SessionStore sessionStore;

    private CommandExecutor executor;

    private SchedulerTaskMachineRecordDO machineRecord;

    private Date startTime, endTime;

    private Integer exitCode;

    private volatile boolean terminated;

    @Getter
    private volatile SchedulerTaskMachineStatus status;

    public TaskMachineHandler(Long machineRecordId) {
        this.machineRecordId = machineRecordId;
        this.status = SchedulerTaskMachineStatus.WAIT;
    }

    @Override
    public void run() {
        // 检查状态
        log.info("调度任务-机器操作-开始 machineRecordId: {}", machineRecordId);
        this.machineRecord = schedulerTaskMachineRecordDAO.selectById(machineRecordId);
        this.status = SchedulerTaskMachineStatus.of(machineRecord.getExecStatus());
        if (!SchedulerTaskMachineStatus.WAIT.equals(status)) {
            return;
        }
        // 执行
        Exception ex = null;
        try {
            this.updateStatus(SchedulerTaskMachineStatus.RUNNABLE);
            // 打开日志
            this.openLogger();
            // 打开机器
            this.sessionStore = machineInfoService.openSessionStore(machineRecord.getMachineId());
            // 获取执行器
            this.executor = sessionStore.getCommandExecutor(Strings.replaceCRLF(machineRecord.getExecCommand()));
            // 开始执行
            CommandExecutors.execCommand(executor, logOutputStream);
            this.exitCode = executor.getExitCode();
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
                // 执行异常回调
                this.exceptionCallback(ex);
                throw Exceptions.runtime(ex);
            }
        } finally {
            // 释放资源
            this.close();
        }
    }

    /**
     * 停止回调
     */
    private void terminatedCallback() {
        log.error("调度任务-机器操作-停止 machineRecordId: {}", machineRecordId);
        // 更新状态
        this.updateStatus(SchedulerTaskMachineStatus.TERMINATED);
        // 拼接日志
        StringBuilder log = new StringBuilder(Const.LF_2)
                .append(Utils.getStainKeyWords("# 调度任务执行停止", StainCode.GLOSS_YELLOW))
                .append(Letters.TAB)
                .append(Utils.getStainKeyWords(Dates.format(endTime), StainCode.GLOSS_BLUE))
                .append(Const.LF);
        this.appendLog(log.toString());
    }

    /**
     * 完成回调
     */
    private void completeCallback() {
        log.info("调度任务-机器操作-完成 machineRecordId: {}, exitCode: {}", machineRecordId, exitCode);
        final boolean execSuccess = ExitCode.isSuccess(exitCode);
        // 更新状态
        if (execSuccess) {
            this.updateStatus(SchedulerTaskMachineStatus.SUCCESS);
        } else {
            this.updateStatus(SchedulerTaskMachineStatus.FAILURE);
        }
        // 拼接日志
        long used = endTime.getTime() - startTime.getTime();
        StringBuilder log = new StringBuilder()
                .append(Letters.LF)
                .append(Utils.getStainKeyWords("# 调度任务执行完成", StainCode.GLOSS_GREEN))
                .append(Letters.LF);
        log.append("exitcode: ")
                .append(execSuccess
                        ? Utils.getStainKeyWords(exitCode, StainCode.GLOSS_BLUE)
                        : Utils.getStainKeyWords(exitCode, StainCode.GLOSS_RED))
                .append(Letters.LF);
        log.append("结束时间: ")
                .append(Utils.getStainKeyWords(Dates.format(endTime), StainCode.GLOSS_BLUE))
                .append("  used ")
                .append(Utils.getStainKeyWords(Utils.interval(used), StainCode.GLOSS_BLUE))
                .append(" (")
                .append(StainCode.prefix(StainCode.GLOSS_BLUE))
                .append(used)
                .append("ms")
                .append(StainCode.SUFFIX)
                .append(")\n");
        this.appendLog(log.toString());
    }

    /**
     * 机器未启用回调
     */
    private void machineDisableCallback() {
        log.error("调度任务-机器操作-机器停用停止 machineRecordId: {}", machineRecordId);
        // 更新状态
        this.updateStatus(SchedulerTaskMachineStatus.TERMINATED);
        // 拼接日志
        StringBuilder log = new StringBuilder()
                .append(Const.LF)
                .append(Utils.getStainKeyWords("# 调度任务执行机器未启用", StainCode.GLOSS_YELLOW))
                .append(Letters.TAB)
                .append(Utils.getStainKeyWords(Dates.format(endTime), StainCode.GLOSS_BLUE))
                .append(Const.LF);
        this.appendLog(log.toString());
    }

    /**
     * 异常回调
     */
    private void exceptionCallback(Exception e) {
        log.error("调度任务-机器操作-失败 machineRecordId: {}", machineRecordId, e);
        // 更新状态
        this.updateStatus(SchedulerTaskMachineStatus.FAILURE);
        // 拼接日志
        StringBuilder log = new StringBuilder()
                .append(Const.LF)
                .append(Utils.getStainKeyWords("# 调度任务执行失败", StainCode.GLOSS_RED))
                .append(Letters.TAB)
                .append(Utils.getStainKeyWords(Dates.format(endTime), StainCode.GLOSS_BLUE))
                .append(Letters.LF)
                .append(Exceptions.getStackTraceAsString(e))
                .append(Const.LF);
        this.appendLog(log.toString());
    }

    @Override
    public void skip() {
        log.info("调度任务-机器操作-跳过 machineRecordId: {}, status: {}", machineRecordId, status);
        if (SchedulerTaskMachineStatus.WAIT.equals(status)) {
            // 只能跳过等待中的任务
            this.updateStatus(SchedulerTaskMachineStatus.SKIPPED);
        }
    }

    @Override
    public void terminate() {
        log.info("调度任务-机器操作-停止 machineRecordId: {}", machineRecordId);
        // 只能停止进行中的任务
        if (SchedulerTaskMachineStatus.RUNNABLE.equals(status)) {
            this.terminated = true;
            Streams.close(this.executor);
        }
    }

    @Override
    public void write(String command) {
        executor.write(command);
    }

    /**
     * 打开日志
     */
    private void openLogger() {
        File logFile = new File(Files1.getPath(SystemEnvAttr.LOG_PATH.getValue(), machineRecord.getLogPath()));
        Files1.touch(logFile);
        this.logPath = logFile.getAbsolutePath();
        // 打开日志流
        log.info("TaskMachineHandler-打开日志流 {} {}", machineRecordId, logPath);
        this.logOutputStream = Files1.openOutputStreamFastSafe(logFile);
        // 拼接开始日志
        StringBuilder log = new StringBuilder()
                .append(Utils.getStainKeyWords("# 开始执行调度任务 ", StainCode.GLOSS_GREEN))
                .append(Const.LF);
        log.append("执行机器: ")
                .append(Utils.getStainKeyWords(machineRecord.getMachineName(), StainCode.GLOSS_BLUE))
                .append(Const.LF);
        log.append("开始时间: ")
                .append(Utils.getStainKeyWords(Dates.format(startTime), StainCode.GLOSS_BLUE))
                .append(Const.LF_2);
        log.append(Utils.getStainKeyWords("# 执行命令", StainCode.GLOSS_GREEN))
                .append(Const.LF)
                .append(StainCode.prefix(StainCode.GLOSS_CYAN))
                .append(Utils.getEndLfWithEof(machineRecord.getExecCommand()))
                .append(StainCode.SUFFIX)
                .append(Utils.getStainKeyWords("# 开始执行", StainCode.GLOSS_GREEN))
                .append(Const.LF);
        this.appendLog(log.toString());
    }

    /**
     * 拼接日志
     *
     * @param log log
     */
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
    private void updateStatus(SchedulerTaskMachineStatus status) {
        Date now = new Date();
        this.status = status;
        SchedulerTaskMachineRecordDO update = new SchedulerTaskMachineRecordDO();
        update.setId(machineRecordId);
        update.setExecStatus(status.getStatus());
        update.setUpdateTime(now);
        switch (status) {
            case RUNNABLE:
                this.startTime = now;
                update.setStartTime(now);
                break;
            case SUCCESS:
            case FAILURE:
            case TERMINATED:
                if (startTime != null) {
                    this.endTime = now;
                    update.setEndTime(now);
                    update.setExitCode(exitCode);
                }
                break;
            default:
        }
        schedulerTaskMachineRecordDAO.updateById(update);
    }

    @Override
    public void close() {
        // 释放资源
        Streams.close(executor);
        Streams.close(sessionStore);
        Streams.close(logOutputStream);
        // 异步关闭正在tail的日志
        tailSessionHolder.asyncCloseTailFile(Const.HOST_MACHINE_ID, logPath);
    }

}
