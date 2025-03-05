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
package cn.orionsec.ops.entity.exporter;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.office.excel.annotation.ExportField;
import cn.orionsec.kit.office.excel.annotation.ExportSheet;
import cn.orionsec.kit.office.excel.annotation.ExportTitle;
import cn.orionsec.kit.office.excel.type.ExcelAlignType;
import cn.orionsec.ops.constant.CnConst;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.entity.domain.ApplicationProfileDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用环境信息导出
 *
 * @author 
 * @version 1.0.0
 * @since 2022/5/29 14:48
 */
@Data
@ApiModel(value = "应用环境信息导出")
@ExportTitle(title = "应用环境导出")
@ExportSheet(name = "应用环境", height = 22, freezeHeader = true, filterHeader = true)
public class ApplicationProfileExportDTO {

    @ApiModelProperty(value = "环境名称")
    @ExportField(index = 0, header = "环境名称", width = 30)
    private String name;

    @ApiModelProperty(value = "唯一标识")
    @ExportField(index = 1, header = "唯一标识", width = 30)
    private String tag;

    @ApiModelProperty(value = "发布审核")
    @ExportField(index = 2, header = "发布审核", width = 17, align = ExcelAlignType.CENTER, selectOptions = {CnConst.OPEN, CnConst.CLOSE})
    private String releaseAudit;

    @ApiModelProperty(value = "描述")
    @ExportField(index = 3, header = "描述", width = 35, wrapText = true)
    private String description;

    static {
        TypeStore.STORE.register(ApplicationProfileDO.class, ApplicationProfileExportDTO.class, p -> {
            ApplicationProfileExportDTO dto = new ApplicationProfileExportDTO();
            dto.setName(p.getProfileName());
            dto.setTag(p.getProfileTag());
            dto.setReleaseAudit(Const.ENABLE.equals(p.getReleaseAudit()) ? CnConst.OPEN : CnConst.CLOSE);
            dto.setDescription(p.getDescription());
            return dto;
        });
    }

}
