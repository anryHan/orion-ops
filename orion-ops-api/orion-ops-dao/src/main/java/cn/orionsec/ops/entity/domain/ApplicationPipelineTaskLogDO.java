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
 * 应用流水线任务日志
 *
 * @author 
 * @since 2022-04-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "应用流水线任务日志")
@TableName("application_pipeline_task_log")
@SuppressWarnings("ALL")
public class ApplicationPipelineTaskLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "流水线任务id")
    @TableField("task_id")
    private Long taskId;

    @ApiModelProperty(value = "流水线任务详情id")
    @TableField("task_detail_id")
    private Long taskDetailId;

    /**
     * @see cn.orionsec.ops.constant.app.PipelineLogStatus
     */
    @ApiModelProperty(value = "日志状态 10创建 20执行 30成功 40失败 50停止 60跳过")
    @TableField("log_status")
    private Integer logStatus;

    /**
     * @see cn.orionsec.ops.constant.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布")
    @TableField("stage_type")
    private Integer stageType;

    @ApiModelProperty(value = "日志详情")
    @TableField("log_info")
    private String logInfo;

    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}
