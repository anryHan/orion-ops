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

import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.dao.MachineAlarmHistoryDAO;
import cn.orionsec.ops.dao.MachineInfoDAO;
import cn.orionsec.ops.entity.domain.MachineAlarmHistoryDO;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.entity.request.machine.MachineAlarmRequest;
import cn.orionsec.ops.handler.alarm.MachineAlarmContext;
import cn.orionsec.ops.handler.alarm.MachineAlarmExecutor;
import cn.orionsec.ops.service.api.MachineAlarmService;
import cn.orionsec.ops.utils.EventParamsHolder;
import cn.orionsec.ops.utils.Valid;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 机器报警服务
 *
 * @author 
 * @version 1.0.0
 * @since 2022/8/26 17:52
 */
@Service("machineAlarmService")
public class MachineAlarmServiceImpl implements MachineAlarmService {

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Resource
    private MachineAlarmHistoryDAO machineAlarmHistoryDAO;

    @Override
    public void triggerMachineAlarm(Long alarmHistoryId) {
        // 查询数据
        MachineAlarmHistoryDO history = machineAlarmHistoryDAO.selectById(alarmHistoryId);
        Valid.notNull(history, MessageConst.UNKNOWN_DATA);
        // 查询机器信息
        Long machineId = history.getMachineId();
        MachineInfoDO machine = machineInfoDAO.selectById(machineId);
        Valid.notNull(machine, MessageConst.INVALID_MACHINE);
        // 执行通知操作
        MachineAlarmContext context = new MachineAlarmContext();
        context.setMachineId(machineId);
        context.setMachineName(machine.getMachineName());
        context.setMachineHost(machine.getMachineHost());
        context.setAlarmType(history.getAlarmType());
        context.setAlarmValue(history.getAlarmValue());
        context.setAlarmTime(history.getAlarmTime());
        new MachineAlarmExecutor(context).exec();
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, alarmHistoryId);
        EventParamsHolder.addParam(EventKeys.MACHINE_ID, machineId);
        EventParamsHolder.addParam(EventKeys.NAME, machine.getMachineName());
    }

    @Override
    public void triggerMachineAlarm(MachineAlarmRequest request) {
        // 查询机器信息
        Long machineId = request.getMachineId();
        MachineInfoDO machine = machineInfoDAO.selectById(machineId);
        Valid.notNull(machine, MessageConst.INVALID_MACHINE);
        // 历史通知
        MachineAlarmHistoryDO history = new MachineAlarmHistoryDO();
        history.setMachineId(machineId);
        history.setAlarmType(request.getType());
        history.setAlarmValue(request.getAlarmValue());
        history.setAlarmTime(request.getAlarmTime());
        machineAlarmHistoryDAO.insert(history);
        // 执行通知操作
        MachineAlarmContext context = new MachineAlarmContext();
        context.setMachineId(machineId);
        context.setMachineName(machine.getMachineName());
        context.setMachineHost(machine.getMachineHost());
        context.setAlarmType(request.getType());
        context.setAlarmValue(request.getAlarmValue());
        context.setAlarmTime(request.getAlarmTime());
        new MachineAlarmExecutor(context).exec();
    }

}
