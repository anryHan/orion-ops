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
package cn.orionsec.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 调度任务执行日志
 *
 * @author 
 * @since 2022-02-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "调度任务执行日志")
@TableName("scheduler_task_record")
@SuppressWarnings("ALL")
public class SchedulerTaskRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "任务id")
    @TableField("task_id")
    private Long taskId;

    @ApiModelProperty(value = "任务名称")
    @TableField("task_name")
    private String taskName;

    /**
     * @see cn.orionsec.ops.constant.scheduler.SchedulerTaskStatus
     */
    @ApiModelProperty(value = "任务状态 10待调度 20调度中 30调度成功 40调度失败 50已停止")
    @TableField("task_status")
    private Integer taskStatus;

    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "开始时间")
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}
