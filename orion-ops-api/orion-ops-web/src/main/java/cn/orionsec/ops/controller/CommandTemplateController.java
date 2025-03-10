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

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.ops.annotation.DemoDisableApi;
import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.entity.request.template.CommandTemplateRequest;
import cn.orionsec.ops.entity.vo.template.CommandTemplateVO;
import cn.orionsec.ops.service.api.CommandTemplateService;
import cn.orionsec.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 命令模板 api
 *
 * @author 
 * @version 1.0.0
 * @since 2021/6/9 17:04
 */
@Api(tags = "命令模板")
@RestController
@RestWrapper
@RequestMapping("/orion/api/template")
public class CommandTemplateController {

    @Resource
    private CommandTemplateService commandTemplateService;

    @DemoDisableApi
    @PostMapping("/add")
    @ApiOperation(value = "新增命令模板")
    @EventLog(EventType.ADD_TEMPLATE)
    public Long add(@RequestBody CommandTemplateRequest request) {
        Valid.notBlank(request.getName());
        Valid.notBlank(request.getValue());
        return commandTemplateService.addTemplate(request);
    }

    @DemoDisableApi
    @PostMapping("/update")
    @ApiOperation(value = "修改命令模板")
    @EventLog(EventType.UPDATE_TEMPLATE)
    public Integer update(@RequestBody CommandTemplateRequest request) {
        Valid.notNull(request.getId());
        Valid.notBlank(request.getValue());
        return commandTemplateService.updateTemplate(request);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取命令模板列表")
    public DataGrid<CommandTemplateVO> list(@RequestBody CommandTemplateRequest request) {
        return commandTemplateService.listTemplate(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取命令模板详情")
    public CommandTemplateVO detail(@RequestBody CommandTemplateRequest request) {
        Long id = Valid.notNull(request.getId());
        return commandTemplateService.templateDetail(id);
    }

    @DemoDisableApi
    @PostMapping("/delete")
    @ApiOperation(value = "删除命令模板")
    @EventLog(EventType.DELETE_TEMPLATE)
    public Integer delete(@RequestBody CommandTemplateRequest request) {
        List<Long> idList = Valid.notNull(request.getIdList());
        return commandTemplateService.deleteTemplate(idList);
    }

}
