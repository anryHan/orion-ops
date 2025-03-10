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
import cn.orionsec.ops.annotation.RequireRole;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.constant.user.RoleType;
import cn.orionsec.ops.entity.request.app.ApplicationProfileRequest;
import cn.orionsec.ops.entity.vo.app.ApplicationProfileFastVO;
import cn.orionsec.ops.entity.vo.app.ApplicationProfileVO;
import cn.orionsec.ops.service.api.ApplicationProfileService;
import cn.orionsec.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用环境 api
 *
 * @author 
 * @version 1.0.0
 * @since 2021/7/2 18:07
 */
@Api(tags = "应用环境")
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-profile")
public class ApplicationProfileController {

    @Resource
    private ApplicationProfileService applicationProfileService;

    @DemoDisableApi
    @PostMapping("/add")
    @ApiOperation(value = "添加应用环境")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.ADD_PROFILE)
    public Long addProfile(@RequestBody ApplicationProfileRequest request) {
        Valid.notBlank(request.getName());
        Valid.notBlank(request.getTag());
        Valid.in(request.getReleaseAudit(), Const.ENABLE, Const.DISABLE);
        return applicationProfileService.addProfile(request);
    }

    @DemoDisableApi
    @PostMapping("/update")
    @ApiOperation(value = "更新应用环境")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.UPDATE_PROFILE)
    public Integer updateProfile(@RequestBody ApplicationProfileRequest request) {
        Valid.notNull(request.getId());
        if (request.getReleaseAudit() != null) {
            Valid.in(request.getReleaseAudit(), Const.ENABLE, Const.DISABLE);
        }
        return applicationProfileService.updateProfile(request);
    }

    @DemoDisableApi
    @PostMapping("/delete")
    @ApiOperation(value = "删除应用环境")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.DELETE_PROFILE)
    public Integer deleteProfile(@RequestBody ApplicationProfileRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationProfileService.deleteProfile(id);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取应用环境列表")
    public DataGrid<ApplicationProfileVO> listProfiles(@RequestBody ApplicationProfileRequest request) {
        return applicationProfileService.listProfiles(request);
    }

    @GetMapping("/fast-list")
    @ApiOperation(value = "获取应用环境列表 (缓存)")
    public List<ApplicationProfileFastVO> listProfiles() {
        return applicationProfileService.fastListProfiles();
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取应用环境详情")
    public ApplicationProfileVO getProfile(@RequestBody ApplicationProfileRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationProfileService.getProfile(id);
    }

}
