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
package cn.orionsec.ops.mapping.scheduler;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.ops.entity.domain.SchedulerTaskMachineRecordDO;
import cn.orionsec.ops.entity.domain.SchedulerTaskRecordDO;
import cn.orionsec.ops.entity.dto.SchedulerTaskRecordStatisticsDTO;
import cn.orionsec.ops.entity.vo.scheduler.*;
import cn.orionsec.ops.utils.Utils;

import java.util.Date;
import java.util.Optional;

/**
 * 定时任务执行明细 对象转换器
 *
 * @author 
 * @version 1.0.0
 * @since 2022/8/10 18:18
 */
public class SchedulerTaskRecordConversion {

    static {
        TypeStore.STORE.register(SchedulerTaskRecordDO.class, SchedulerTaskRecordVO.class, p -> {
            SchedulerTaskRecordVO vo = new SchedulerTaskRecordVO();
            vo.setId(p.getId());
            vo.setTaskId(p.getTaskId());
            vo.setTaskName(p.getTaskName());
            vo.setStatus(p.getTaskStatus());
            Date startTime = p.getStartTime();
            Date endTime = p.getEndTime();
            vo.setStartTime(startTime);
            vo.setEndTime(endTime);
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            Optional.ofNullable(startTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setStartTimeAgo);
            Optional.ofNullable(endTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setEndTimeAgo);
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(SchedulerTaskRecordDO.class, SchedulerTaskRecordStatusVO.class, p -> {
            SchedulerTaskRecordStatusVO vo = new SchedulerTaskRecordStatusVO();
            vo.setId(p.getId());
            vo.setStatus(p.getTaskStatus());
            Date startTime = p.getStartTime();
            Date endTime = p.getEndTime();
            vo.setStartTime(startTime);
            vo.setEndTime(endTime);
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            Optional.ofNullable(startTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setStartTimeAgo);
            Optional.ofNullable(endTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setEndTimeAgo);
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(SchedulerTaskRecordStatisticsDTO.class, SchedulerTaskRecordStatisticsChartVO.class, p -> {
            SchedulerTaskRecordStatisticsChartVO vo = new SchedulerTaskRecordStatisticsChartVO();
            vo.setDate(Dates.format(p.getDate(), Dates.YMD));
            vo.setScheduledCount(p.getScheduledCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(SchedulerTaskRecordStatisticsDTO.class, SchedulerTaskRecordStatisticsVO.class, p -> {
            SchedulerTaskRecordStatisticsVO vo = new SchedulerTaskRecordStatisticsVO();
            vo.setScheduledCount(p.getScheduledCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            vo.setAvgUsed(p.getAvgUsed());
            vo.setAvgUsedInterval(Utils.interval(p.getAvgUsed()));
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(SchedulerTaskMachineRecordDO.class, SchedulerTaskMachineRecordVO.class, p -> {
            SchedulerTaskMachineRecordVO vo = new SchedulerTaskMachineRecordVO();
            vo.setId(p.getId());
            vo.setRecordId(p.getRecordId());
            vo.setMachineId(p.getMachineId());
            vo.setMachineName(p.getMachineName());
            vo.setMachineHost(p.getMachineHost());
            vo.setMachineTag(p.getMachineTag());
            vo.setCommand(p.getExecCommand());
            vo.setStatus(p.getExecStatus());
            vo.setExitCode(p.getExitCode());
            Date startTime = p.getStartTime();
            Date endTime = p.getEndTime();
            vo.setStartTime(startTime);
            vo.setEndTime(endTime);
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            Optional.ofNullable(startTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setStartTimeAgo);
            Optional.ofNullable(endTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setEndTimeAgo);
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(SchedulerTaskMachineRecordDO.class, SchedulerTaskMachineRecordStatusVO.class, p -> {
            SchedulerTaskMachineRecordStatusVO vo = new SchedulerTaskMachineRecordStatusVO();
            vo.setId(p.getId());
            vo.setRecordId(p.getRecordId());
            vo.setStatus(p.getExecStatus());
            vo.setExitCode(p.getExitCode());
            Date startTime = p.getStartTime();
            Date endTime = p.getEndTime();
            vo.setStartTime(startTime);
            vo.setEndTime(endTime);
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            Optional.ofNullable(startTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setStartTimeAgo);
            Optional.ofNullable(endTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setEndTimeAgo);
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(SchedulerTaskRecordStatisticsDTO.class, SchedulerTaskMachineRecordStatisticsVO.class, p -> {
            SchedulerTaskMachineRecordStatisticsVO vo = new SchedulerTaskMachineRecordStatisticsVO();
            vo.setMachineId(p.getMachineId());
            vo.setMachineName(p.getMachineName());
            vo.setScheduledCount(p.getScheduledCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            vo.setAvgUsed(p.getAvgUsed());
            vo.setAvgUsedInterval(Utils.interval(p.getAvgUsed()));
            return vo;
        });
    }

}
