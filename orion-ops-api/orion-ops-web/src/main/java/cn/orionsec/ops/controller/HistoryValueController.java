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
import cn.orionsec.kit.lang.define.wrapper.HttpWrapper;
import cn.orionsec.ops.annotation.DemoDisableApi;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.history.HistoryValueType;
import cn.orionsec.ops.entity.request.history.HistoryValueRequest;
import cn.orionsec.ops.entity.vo.history.HistoryValueVO;
import cn.orionsec.ops.service.api.HistoryValueService;
import cn.orionsec.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 历史值 api
 *
 * @author 
 * @version 1.0.0
 * @since 2021/6/9 17:15
 */
@Api(tags = "历史值")
@RestController
@RestWrapper
@RequestMapping("/orion/api/history-value")
public class HistoryValueController {

    @Resource
    private HistoryValueService historyValueService;

    @PostMapping("/list")
    @ApiOperation(value = "历史值列表")
    public DataGrid<HistoryValueVO> list(@RequestBody HistoryValueRequest request) {
        Valid.notNull(request.getValueId());
        Valid.notNull(HistoryValueType.of(request.getValueType()));
        return historyValueService.list(request);
    }

    @DemoDisableApi
    @PostMapping("/rollback")
    @ApiOperation(value = "回滚历史值")
    public HttpWrapper<?> rollback(@RequestBody HistoryValueRequest request) {
        Long id = Valid.notNull(request.getId());
        historyValueService.rollback(id);
        return HttpWrapper.ok();
    }

}
