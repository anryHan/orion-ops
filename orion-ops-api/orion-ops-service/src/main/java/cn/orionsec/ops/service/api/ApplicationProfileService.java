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
package cn.orionsec.ops.service.api;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.ops.entity.request.app.ApplicationProfileRequest;
import cn.orionsec.ops.entity.vo.app.ApplicationProfileFastVO;
import cn.orionsec.ops.entity.vo.app.ApplicationProfileVO;

import java.util.List;

/**
 * 应用服务
 *
 * @author 
 * @version 1.0.0
 * @since 2021/7/2 17:52
 */
public interface ApplicationProfileService {

    /**
     * 添加环境
     *
     * @param request request
     * @return id
     */
    Long addProfile(ApplicationProfileRequest request);

    /**
     * 更新环境
     *
     * @param request request
     * @return effect
     */
    Integer updateProfile(ApplicationProfileRequest request);

    /**
     * 删除环境
     *
     * @param id id
     * @return effect
     */
    Integer deleteProfile(Long id);

    /**
     * 环境列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<ApplicationProfileVO> listProfiles(ApplicationProfileRequest request);

    /**
     * 环境列表 (缓存)
     *
     * @return rows
     */
    List<ApplicationProfileFastVO> fastListProfiles();

    /**
     * 环境详情
     *
     * @param id id
     * @return rows
     */
    ApplicationProfileVO getProfile(Long id);

    /**
     * 清空环境缓存
     */
    void clearProfileCache();

}
