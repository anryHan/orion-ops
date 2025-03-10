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
package cn.orionsec.ops.entity.request.app;

import cn.orionsec.kit.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 应用发布请求
 *
 * @author 
 * @version 1.0.0
 * @since 2021/12/20 9:40
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "应用发布请求")
@SuppressWarnings("ALL")
public class ApplicationReleaseRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "环境id")
    private Long profileId;

    @ApiModelProperty(value = "构建id")
    private Long buildId;

    @ApiModelProperty(value = "应用机器id列表")
    private List<Long> machineIdList;

    /**
     * @see cn.orionsec.ops.constant.app.ReleaseStatus
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * @see cn.orionsec.ops.constant.app.TimedType
     */
    @ApiModelProperty(value = "是否是定时发布 10普通发布 20定时发布")
    private Integer timedRelease;

    @ApiModelProperty(value = "定时发布时间")
    private Date timedReleaseTime;

    /**
     * @see cn.orionsec.ops.constant.Const#ENABLE
     */
    @ApiModelProperty(value = "只看自己")
    private Integer onlyMyself;

    @ApiModelProperty(value = "发布机器id")
    private Long releaseMachineId;

    @ApiModelProperty(value = "发布机器id")
    private List<Long> releaseMachineIdList;

    @ApiModelProperty(value = "id列表")
    private List<Long> idList;

    @ApiModelProperty(value = "是否查询机器")
    private Integer queryMachine;

    @ApiModelProperty(value = "是否查询操作")
    private Integer queryAction;

    @ApiModelProperty(value = "命令")
    private String command;

}
