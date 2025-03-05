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
package cn.orionsec.ops.controller;

import cn.orionsec.kit.lang.define.wrapper.HttpWrapper;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.io.Streams;
import cn.orionsec.kit.office.excel.Excels;
import cn.orionsec.kit.web.servlet.web.Servlets;
import cn.orionsec.ops.OrionApplication;
import cn.orionsec.ops.annotation.*;
import cn.orionsec.ops.constant.ImportType;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.entity.importer.DataImportDTO;
import cn.orionsec.ops.entity.request.data.DataImportRequest;
import cn.orionsec.ops.entity.vo.data.DataImportCheckVO;
import cn.orionsec.ops.handler.importer.checker.IDataChecker;
import cn.orionsec.ops.handler.importer.impl.IDataImporter;
import cn.orionsec.ops.service.api.DataImportService;
import cn.orionsec.ops.utils.EventParamsHolder;
import cn.orionsec.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * 数据导入 api
 *
 * @author 
 * @version 1.0.0
 * @since 2022/5/26 14:02
 */
@Api(tags = "数据导入")
@RestController
@RestWrapper
@RequestMapping("/orion/api/data-import")
public class DataImportController {

    @Resource
    private DataImportService dataImportService;

    @IgnoreWrapper
    @IgnoreLog
    @IgnoreAuth
    @GetMapping("/get-template")
    @ApiOperation(value = "获取导入模板")
    public void getTemplate(Integer type, HttpServletResponse response) throws IOException {
        ImportType importType = Valid.notNull(ImportType.of(type));
        String templateName = importType.getTemplateName();
        Servlets.setAttachmentHeader(response, templateName);
        // 读取文件
        InputStream in = OrionApplication.class.getResourceAsStream(importType.getTemplatePath());
        ServletOutputStream out = response.getOutputStream();
        if (in == null) {
            out.write(Strings.bytes(Strings.format(MessageConst.FILE_NOT_FOUND, templateName)));
            return;
        }
        Streams.transfer(in, out);
    }

    @PostMapping("/check-data")
    @ApiOperation(value = "检查导入信息")
    public DataImportCheckVO checkImportData(@RequestParam("file") MultipartFile file,
                                             @RequestParam("type") Integer type,
                                             @RequestParam(name = "protectPassword", required = false) String protectPassword) throws IOException {
        ImportType importType = Valid.notNull(ImportType.of(type));
        Workbook workbook;
        if (Strings.isBlank(protectPassword)) {
            workbook = Excels.openWorkbook(file.getInputStream());
        } else {
            workbook = Excels.openWorkbook(file.getInputStream(), protectPassword);
        }
        // 检查数据
        return IDataChecker.create(importType, workbook).doCheck();
    }

    @DemoDisableApi
    @PostMapping("/import")
    @ApiOperation(value = "导入数据")
    @EventLog(EventType.DATA_IMPORT)
    public HttpWrapper<?> importData(@RequestBody DataImportRequest request) {
        String token = Valid.notNull(request.getImportToken());
        // 读取导入数据
        DataImportDTO importData = dataImportService.checkImportToken(token);
        // 执行导入操作
        IDataImporter.create(importData).doImport();
        // 设置日志参数
        ImportType importType = ImportType.of(importData.getType());
        EventParamsHolder.addParam(EventKeys.TOKEN, token);
        EventParamsHolder.addParam(EventKeys.TYPE, importType.getType());
        EventParamsHolder.addParam(EventKeys.LABEL, importType.getLabel());
        return HttpWrapper.ok();
    }

    @PostMapping("/cancel-import")
    @ApiOperation(value = "取消导入")
    public HttpWrapper<?> cancelImportData(@RequestBody DataImportRequest request) {
        String token = request.getImportToken();
        if (Strings.isBlank(token)) {
            return HttpWrapper.ok();
        }
        dataImportService.clearImportToken(token);
        return HttpWrapper.ok();
    }

}
