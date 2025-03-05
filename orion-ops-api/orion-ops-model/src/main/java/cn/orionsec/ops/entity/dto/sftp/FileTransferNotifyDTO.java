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
package cn.orionsec.ops.entity.dto.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * sftp 文件传输通知
 *
 * @author 
 * @version 1.0.0
 * @since 2021/6/27 1:02
 */
@Data
@ApiModel(value = "sftp 文件传输通知")
@SuppressWarnings("ALL")
public class FileTransferNotifyDTO {

    /**
     * @see cn.orionsec.ops.constant.sftp.SftpNotifyType
     */
    @ApiModelProperty(value = "通知类型")
    private Integer type;

    @ApiModelProperty(value = "fileToken")
    private String fileToken;

    @ApiModelProperty(value = "body")
    private Object body;

}
