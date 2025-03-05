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
package cn.orionsec.ops.entity.request.alarm;

import cn.orionsec.kit.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 报警组请求
 *
 * @author 
 * @version 1.0.0
 * @since 2022/8/25 15:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "报警组请求")
public class AlarmGroupRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "报警组名称")
    private String name;

    @ApiModelProperty(value = "报警组描述")
    private String description;

    @ApiModelProperty(value = "报警组员id")
    private List<Long> userIdList;

    @ApiModelProperty(value = "报警通知方式")
    private List<Long> notifyIdList;

}
