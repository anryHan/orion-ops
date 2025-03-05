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

/**
 * 报警组通知方式请求
 *
 * @author 
 * @version 1.0.0
 * @since 2022/8/25 15:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "报警组通知方式请求")
@SuppressWarnings("ALL")
public class AlarmGroupNotifyRequest extends PageRequest {

    @ApiModelProperty(value = "通知id")
    private Long notifyId;

    /**
     * @see cn.orionsec.ops.constant.alarm.AlarmGroupNotifyType
     */
    @ApiModelProperty(value = "通知类型")
    private Integer notifyType;

}