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
package cn.orionsec.ops.entity.vo.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ip过滤器配置响应
 *
 * @author 
 * @version 1.0.0
 * @since 2022/2/15 22:09
 */
@Data
@ApiModel(value = "ip过滤器配置响应")
public class IpListConfigVO {

    @ApiModelProperty(value = "当前ip")
    private String currentIp;

    @ApiModelProperty(value = "当前ip位置")
    private String ipLocation;

    @ApiModelProperty(value = "ip白名单")
    private String whiteIpList;

    @ApiModelProperty(value = "ip黑名单")
    private String blackIpList;

    @ApiModelProperty(value = "是否启用ip过滤器")
    private Boolean enableIpFilter;

    @ApiModelProperty(value = "是否启用ip白名单")
    private Boolean enableWhiteIpList;

}
