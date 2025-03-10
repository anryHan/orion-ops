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
package cn.orionsec.ops.service.impl;

import cn.orionsec.kit.lang.define.collect.MutableLinkedHashMap;
import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.collect.Lists;
import cn.orionsec.kit.lang.utils.convert.Converts;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.env.EnvConst;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.constant.scheduler.SchedulerTaskMachineStatus;
import cn.orionsec.ops.constant.scheduler.SchedulerTaskStatus;
import cn.orionsec.ops.dao.SchedulerTaskDAO;
import cn.orionsec.ops.dao.SchedulerTaskMachineRecordDAO;
import cn.orionsec.ops.dao.SchedulerTaskRecordDAO;
import cn.orionsec.ops.entity.domain.*;
import cn.orionsec.ops.entity.request.scheduler.SchedulerTaskRecordRequest;
import cn.orionsec.ops.entity.vo.scheduler.SchedulerTaskMachineRecordStatusVO;
import cn.orionsec.ops.entity.vo.scheduler.SchedulerTaskMachineRecordVO;
import cn.orionsec.ops.entity.vo.scheduler.SchedulerTaskRecordStatusVO;
import cn.orionsec.ops.entity.vo.scheduler.SchedulerTaskRecordVO;
import cn.orionsec.ops.handler.scheduler.ITaskProcessor;
import cn.orionsec.ops.handler.scheduler.TaskSessionHolder;
import cn.orionsec.ops.service.api.*;
import cn.orionsec.ops.utils.DataQuery;
import cn.orionsec.ops.utils.EventParamsHolder;
import cn.orionsec.ops.utils.PathBuilders;
import cn.orionsec.ops.utils.Valid;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 调度任务执行日志 服务实现类
 * </p>
 *
 * @author 
 * @since 2022-02-22
 */
@Service("schedulerTaskRecordService")
public class SchedulerTaskRecordServiceImpl implements SchedulerTaskRecordService {

    @Resource
    private SchedulerTaskDAO schedulerTaskDAO;

    @Resource
    private SchedulerTaskMachineService schedulerTaskMachineService;

    @Resource
    private SchedulerTaskRecordDAO schedulerTaskRecordDAO;

    @Resource
    private SchedulerTaskMachineRecordDAO schedulerTaskMachineRecordDAO;

    @Resource
    private SchedulerTaskMachineRecordService schedulerTaskMachineRecordService;

    @Resource
    private MachineInfoService machineInfoService;

    @Resource
    private MachineEnvService machineEnvService;

    @Resource
    private SystemEnvService systemEnvService;

    @Resource
    private TaskSessionHolder taskSessionHolder;

    @Override
    public Integer deleteByTaskId(Long taskId) {
        LambdaQueryWrapper<SchedulerTaskRecordDO> wrapper = new LambdaQueryWrapper<SchedulerTaskRecordDO>()
                .eq(SchedulerTaskRecordDO::getTaskId, taskId);
        return schedulerTaskRecordDAO.delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTaskRecord(Long taskId) {
        // 查询任务
        SchedulerTaskDO task = schedulerTaskDAO.selectById(taskId);
        Valid.notNull(task);
        List<SchedulerTaskMachineDO> machines = schedulerTaskMachineService.selectByTaskId(taskId);
        boolean emptyMachine = machines.isEmpty();
        Date now = new Date();
        // 创建明细
        SchedulerTaskRecordDO record = new SchedulerTaskRecordDO();
        record.setTaskId(taskId);
        record.setTaskName(task.getTaskName());
        record.setTaskStatus(emptyMachine ? SchedulerTaskStatus.SUCCESS.getStatus() : SchedulerTaskStatus.WAIT.getStatus());
        record.setCreateTime(now);
        schedulerTaskRecordDAO.insert(record);
        Long recordId = record.getId();
        // 创建机器明细
        if (!emptyMachine) {
            String command = task.getTaskCommand();
            final boolean containsEnv = command.contains(EnvConst.SYMBOL);
            if (containsEnv) {
                // 获取系统变量
                Map<String, String> systemEnv = systemEnvService.getFullSystemEnv();
                command = Strings.format(command, EnvConst.SYMBOL, systemEnv);
            }
            for (SchedulerTaskMachineDO taskMachine : machines) {
                Long machineId = taskMachine.getMachineId();

                // 查询机器信息
                MachineInfoDO machine = machineInfoService.selectById(machineId);
                if (machine == null) {
                    continue;
                }
                // 设置机器明细
                SchedulerTaskMachineRecordDO machineRecord = new SchedulerTaskMachineRecordDO();
                machineRecord.setTaskId(taskId);
                machineRecord.setRecordId(recordId);
                machineRecord.setTaskMachineId(taskMachine.getId());
                machineRecord.setMachineId(machineId);
                machineRecord.setMachineName(machine.getMachineName());
                machineRecord.setMachineHost(machine.getMachineHost());
                machineRecord.setMachineTag(machine.getMachineTag());
                if (containsEnv) {
                    // 查询机器变量
                    MutableLinkedHashMap<String, String> machineEnv = machineEnvService.getFullMachineEnv(machineId);
                    machineRecord.setExecCommand(Strings.format(command, EnvConst.SYMBOL, machineEnv));
                } else {
                    machineRecord.setExecCommand(command);
                }
                machineRecord.setExecStatus(SchedulerTaskMachineStatus.WAIT.getStatus());
                machineRecord.setLogPath(PathBuilders.getSchedulerTaskLogPath(taskId, recordId, machineId));
                schedulerTaskMachineRecordDAO.insert(machineRecord);
            }
        }
        // 更新任务状态
        SchedulerTaskDO updateTask = new SchedulerTaskDO();
        updateTask.setId(taskId);
        updateTask.setLatelyStatus(record.getTaskStatus());
        updateTask.setLatelyScheduleTime(now);
        updateTask.setUpdateTime(now);
        schedulerTaskDAO.updateById(updateTask);
        return recordId;
    }

    @Override
    public DataGrid<SchedulerTaskRecordVO> listTaskRecord(SchedulerTaskRecordRequest request) {
        LambdaQueryWrapper<SchedulerTaskRecordDO> wrapper = new LambdaQueryWrapper<SchedulerTaskRecordDO>()
                .eq(Objects.nonNull(request.getTaskId()), SchedulerTaskRecordDO::getTaskId, request.getTaskId())
                .eq(Objects.nonNull(request.getStatus()), SchedulerTaskRecordDO::getTaskStatus, request.getStatus())
                .like(Strings.isNotBlank(request.getTaskName()), SchedulerTaskRecordDO::getTaskName, request.getTaskName())
                .orderByDesc(SchedulerTaskRecordDO::getId);
        // 查询列表
        return DataQuery.of(schedulerTaskRecordDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(SchedulerTaskRecordVO.class);
    }

    @Override
    public SchedulerTaskRecordVO getDetailById(Long id) {
        SchedulerTaskRecordDO record = schedulerTaskRecordDAO.selectById(id);
        Valid.notNull(record, MessageConst.UNKNOWN_DATA);
        SchedulerTaskRecordVO taskRecord = Converts.to(record, SchedulerTaskRecordVO.class);
        // 查询机器
        List<SchedulerTaskMachineRecordDO> machines = schedulerTaskMachineRecordService.selectByRecordId(id);
        List<SchedulerTaskMachineRecordVO> machineRecords = Converts.toList(machines, SchedulerTaskMachineRecordVO.class);
        taskRecord.setMachines(machineRecords);
        return taskRecord;
    }

    @Override
    public List<SchedulerTaskMachineRecordVO> listMachinesRecord(Long recordId) {
        List<SchedulerTaskMachineRecordDO> machines = schedulerTaskMachineRecordService.selectByRecordId(recordId);
        return Converts.toList(machines, SchedulerTaskMachineRecordVO.class);
    }

    @Override
    public List<SchedulerTaskRecordStatusVO> listRecordStatus(List<Long> idList, List<Long> machineRecordIdList) {
        // 查询任务状态
        List<SchedulerTaskRecordDO> recordStatus = schedulerTaskRecordDAO.selectTaskStatusByIdList(idList);
        List<SchedulerTaskRecordStatusVO> recordStatusList = Converts.toList(recordStatus, SchedulerTaskRecordStatusVO.class);
        if (!Lists.isEmpty(machineRecordIdList)) {
            // 查询机器状态
            List<SchedulerTaskMachineRecordDO> machineRecords = schedulerTaskMachineRecordDAO.selectStatusByIdList(machineRecordIdList);
            Map<Long, List<SchedulerTaskMachineRecordStatusVO>> machineStatusMap = machineRecords.stream()
                    .map(s -> Converts.to(s, SchedulerTaskMachineRecordStatusVO.class))
                    .collect(Collectors.groupingBy(SchedulerTaskMachineRecordStatusVO::getRecordId));
            // 设置机器状态
            for (SchedulerTaskRecordStatusVO record : recordStatusList) {
                record.setMachines(machineStatusMap.get(record.getId()));
            }
        }
        return recordStatusList;
    }

    @Override
    public List<SchedulerTaskMachineRecordStatusVO> listMachineRecordStatus(List<Long> idList) {
        List<SchedulerTaskMachineRecordDO> status = schedulerTaskMachineRecordDAO.selectStatusByIdList(idList);
        return Converts.toList(status, SchedulerTaskMachineRecordStatusVO.class);
    }

    @Override
    public Integer deleteTaskRecord(List<Long> idList) {
        // 获取数据
        List<SchedulerTaskRecordDO> taskList = schedulerTaskRecordDAO.selectBatchIds(idList);
        Valid.notEmpty(taskList, MessageConst.UNKNOWN_DATA);
        boolean canDelete = taskList.stream()
                .map(SchedulerTaskRecordDO::getTaskStatus)
                .noneMatch(s -> SchedulerTaskStatus.WAIT.getStatus().equals(s)
                        || SchedulerTaskStatus.RUNNABLE.getStatus().equals(s));
        Valid.isTrue(canDelete, MessageConst.ILLEGAL_STATUS);
        // 删除主表
        int effect = schedulerTaskRecordDAO.deleteBatchIds(idList);
        // 删除机器执行明细
        effect += schedulerTaskMachineRecordDAO.deleteByRecordIdList(idList);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID_LIST, idList);
        EventParamsHolder.addParam(EventKeys.COUNT, idList.size());
        return effect;
    }

    @Override
    public void terminateAll(Long id) {
        // 查询数据
        SchedulerTaskRecordDO record = schedulerTaskRecordDAO.selectById(id);
        Valid.notNull(record, MessageConst.TASK_ABSENT);
        // 检查状态
        Valid.isTrue(SchedulerTaskStatus.RUNNABLE.getStatus().equals(record.getTaskStatus()), MessageConst.ILLEGAL_STATUS);
        // 停止
        ITaskProcessor session = taskSessionHolder.getSession(id);
        Valid.notNull(session, MessageConst.SESSION_PRESENT);
        session.terminateAll();
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.NAME, record.getTaskName());
    }

    @Override
    public void terminateMachine(Long machineRecordId) {
        this.skipOrTerminateTaskMachine(machineRecordId, true);
    }

    @Override
    public void skipMachine(Long machineRecordId) {
        this.skipOrTerminateTaskMachine(machineRecordId, false);
    }

    @Override
    public void writeMachine(Long machineRecordId, String command) {
        // 查询明细
        SchedulerTaskMachineRecordDO machine = schedulerTaskMachineRecordDAO.selectById(machineRecordId);
        Valid.notNull(machine, MessageConst.TASK_ABSENT);
        Valid.isTrue(SchedulerTaskMachineStatus.RUNNABLE.getStatus().equals(machine.getExecStatus()), MessageConst.ILLEGAL_STATUS);
        // 获取会话
        ITaskProcessor session = taskSessionHolder.getSession(machine.getRecordId());
        Valid.notNull(session, MessageConst.SESSION_PRESENT);
        session.writeMachine(machineRecordId, command);
    }

    /**
     * 跳过或终止任务
     *
     * @param machineRecordId machineRecordId
     * @param terminate       终止/跳过
     */
    private void skipOrTerminateTaskMachine(Long machineRecordId, boolean terminate) {
        // 查询数据
        SchedulerTaskMachineRecordDO machine = schedulerTaskMachineRecordDAO.selectById(machineRecordId);
        Valid.notNull(machine, MessageConst.TASK_ABSENT);
        Long id = machine.getRecordId();
        SchedulerTaskRecordDO record = schedulerTaskRecordDAO.selectById(id);
        Valid.notNull(record, MessageConst.TASK_ABSENT);
        Valid.isTrue(SchedulerTaskStatus.RUNNABLE.getStatus().equals(record.getTaskStatus()), MessageConst.ILLEGAL_STATUS);
        // 执行
        if (terminate) {
            Valid.isTrue(SchedulerTaskMachineStatus.RUNNABLE.getStatus().equals(machine.getExecStatus()), MessageConst.ILLEGAL_STATUS);
        } else {
            Valid.isTrue(SchedulerTaskMachineStatus.WAIT.getStatus().equals(machine.getExecStatus()), MessageConst.ILLEGAL_STATUS);
        }
        // 停止
        ITaskProcessor session = taskSessionHolder.getSession(id);
        Valid.notNull(session, MessageConst.SESSION_PRESENT);
        if (terminate) {
            session.terminateMachine(machineRecordId);
        } else {
            session.skipMachine(machineRecordId);
        }
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.MACHINE_ID, machineRecordId);
        EventParamsHolder.addParam(EventKeys.NAME, record.getTaskName());
        EventParamsHolder.addParam(EventKeys.MACHINE_NAME, machine.getMachineName());
    }

}
