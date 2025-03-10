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
package cn.orionsec.ops.entity.vo.scheduler;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 调度任务统计响应
 *
 * @author 
 * @version 1.0.0
 * @since 2022/3/21 10:14
 */
@Data
@ApiModel(value = "调度任务统计响应")
public class SchedulerTaskRecordStatisticsVO {

    @ApiModelProperty(value = "调度次数")
    private Integer scheduledCount;

    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    @ApiModelProperty(value = "失败次数")
    private Integer failureCount;

    @ApiModelProperty(value = "成功机器平均执行时长毫秒")
    private Long avgUsed;

    @ApiModelProperty(value = "成功机器平均执行时长")
    private String avgUsedInterval;

    @ApiModelProperty(value = "机器执行统计")
    private List<SchedulerTaskMachineRecordStatisticsVO> machineList;

    @ApiModelProperty(value = "调度图表")
    private List<SchedulerTaskRecordStatisticsChartVO> charts;

}
