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
package cn.orionsec.ops.entity.vo.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 传输列表响应
 *
 * @author 
 * @version 1.0.0
 * @since 2021/6/27 23:30
 */
@Data
@ApiModel(value = "传输列表响应")
@SuppressWarnings("ALL")
public class FileTransferLogVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "fileToken")
    private String fileToken;

    /**
     * @see cn.orionsec.ops.constant.sftp.SftpTransferType
     */
    @ApiModelProperty(value = "传输类型 10上传 20下载 30传输")
    private Integer type;

    @ApiModelProperty(value = "远程文件")
    private String remoteFile;

    @ApiModelProperty(value = "当前大小")
    private String current;

    @ApiModelProperty(value = "文件大小")
    private String size;

    @ApiModelProperty(value = "当前进度")
    private Double progress;

    /**
     * @see cn.orionsec.ops.constant.sftp.SftpTransferStatus
     */
    @ApiModelProperty(value = "传输状态 10未开始 20进行中 30已暂停 40已完成 50已取消 60传输异常")
    private Integer status;

}
