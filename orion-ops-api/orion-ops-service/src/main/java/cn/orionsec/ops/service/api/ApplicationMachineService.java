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

import cn.orionsec.ops.entity.domain.ApplicationMachineDO;
import cn.orionsec.ops.entity.request.app.ApplicationConfigRequest;
import cn.orionsec.ops.entity.vo.app.ApplicationMachineVO;

import java.util.List;

/**
 * 应用机器服务
 *
 * @author 
 * @version 1.0.0
 * @since 2021/7/9 18:24
 */
public interface ApplicationMachineService {

    /**
     * 获取应用环境的机器
     *
     * @param machineId machineId
     * @param appId     appId
     * @param profileId profileId
     * @return machineId
     */
    Long getAppProfileMachineId(Long machineId, Long appId, Long profileId);

    /**
     * 获取应用环境的机器id
     *
     * @param appId               appId
     * @param profileId           profileId
     * @param filterMachineStatus 是否过滤已禁用的机器状态
     * @return machineIdList
     */
    List<Long> getAppProfileMachineIdList(Long appId, Long profileId, boolean filterMachineStatus);

    /**
     * 获取应用环境的机器数量
     *
     * @param appId     appId
     * @param profileId profileId
     * @return count
     */
    Integer getAppProfileMachineCount(Long appId, Long profileId);

    /**
     * 获取应用环境的机器
     *
     * @param appId     appId
     * @param profileId profileId
     * @return machineList
     */
    List<ApplicationMachineVO> getAppProfileMachineDetail(Long appId, Long profileId);

    /**
     * 获取应用环境的机器
     *
     * @param appId     appId
     * @param profileId profileId
     * @return machineList
     */
    List<ApplicationMachineDO> getAppProfileMachineList(Long appId, Long profileId);

    /**
     * 通过机器 id 删除应用机器
     *
     * @param machineIdList machineIdList
     * @return effect
     */
    Integer deleteAppMachineByMachineIdList(List<Long> machineIdList);

    /**
     * 通过 appId profileId 删除应用机器
     *
     * @param appId     appId
     * @param profileId profileId
     * @return effect
     */
    Integer deleteAppMachineByAppProfileId(Long appId, Long profileId);

    /**
     * 通过 appId profileId machineId 删除应用机器
     *
     * @param appId     appId
     * @param profileId profileId
     * @param machineId machineId
     * @return effect
     */
    Integer deleteAppMachineByAppProfileMachineId(Long appId, Long profileId, Long machineId);

    /**
     * 通过 id 删除
     *
     * @param id id
     * @return effect
     */
    Integer deleteById(Long id);

    /**
     * 通过 appId profileId 查询应用机器id列表
     *
     * @param appId     appId
     * @param profileId profileId
     * @return 机器id列表
     */
    List<Long> selectAppProfileMachineIdList(Long appId, Long profileId);

    /**
     * 配置app机器
     *
     * @param request request
     */
    void configAppMachines(ApplicationConfigRequest request);

    /**
     * 同步 app 机器
     *
     * @param appId         appId
     * @param profileId     profileId
     * @param syncProfileId 需要同步的profileId
     */
    void syncAppProfileMachine(Long appId, Long profileId, Long syncProfileId);

    /**
     * 复制 app 机器
     *
     * @param appId       appId
     * @param targetAppId targetAppId
     */
    void copyAppMachine(Long appId, Long targetAppId);

    /**
     * 更新 app releaseId
     *
     * @param appId         appId
     * @param profileId     profileId
     * @param releaseId     releaseId
     * @param machineIdList machineIdList
     */
    void updateAppMachineReleaseId(Long appId, Long profileId, Long releaseId, List<Long> machineIdList);

}
