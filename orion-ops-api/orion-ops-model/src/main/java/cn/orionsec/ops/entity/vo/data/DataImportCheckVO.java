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
package cn.orionsec.ops.entity.vo.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 数据导入检查响应
 *
 * @author 
 * @version 1.0.0
 * @since 2022/5/27 11:18
 */
@Data
@ApiModel(value = "数据导入检查响应")
public class DataImportCheckVO {

    @ApiModelProperty(value = "无效行")
    private List<DataImportCheckRowVO> illegalRows;

    @ApiModelProperty(value = "插入行")
    private List<DataImportCheckRowVO> insertRows;

    @ApiModelProperty(value = "更新行")
    private List<DataImportCheckRowVO> updateRows;

    @ApiModelProperty(value = "导入token")
    private String importToken;

}
