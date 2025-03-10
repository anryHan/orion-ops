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
package cn.orionsec.ops.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 检查应用是否已配置
 *
 * @author 
 * @version 1.0.0
 * @since 2021/12/29 23:41
 */
@Data
@ApiModel(value = "检查应用是否已配置")
public class ApplicationActionConfigDTO {

    @ApiModelProperty(value = "appId")
    private Long appId;

    @ApiModelProperty(value = "构建阶段数量")
    private Integer buildStageCount;

    @ApiModelProperty(value = "发布阶段数量")
    private Integer releaseStageCount;

}
