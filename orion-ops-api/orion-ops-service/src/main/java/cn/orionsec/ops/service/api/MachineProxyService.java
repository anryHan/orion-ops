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
import cn.orionsec.ops.entity.request.machine.MachineProxyRequest;
import cn.orionsec.ops.entity.vo.machine.MachineProxyVO;

import java.util.List;

/**
 * 代理服务
 *
 * @author 
 * @version 1.0.0
 * @since 2021/4/3 21:58
 */
public interface MachineProxyService {

    /**
     * 添加代理
     *
     * @param request request
     * @return id
     */
    Long addProxy(MachineProxyRequest request);

    /**
     * 修改代理
     *
     * @param request request
     * @return effect
     */
    Integer updateProxy(MachineProxyRequest request);

    /**
     * 分页查询
     *
     * @param request request
     * @return rows
     */
    DataGrid<MachineProxyVO> listProxy(MachineProxyRequest request);

    /**
     * 查询详情
     *
     * @param id id
     * @return row
     */
    MachineProxyVO getProxyDetail(Long id);

    /**
     * 删除代理
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteProxy(List<Long> idList);

}
