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
package cn.orionsec.ops.service.impl;

import cn.orionsec.ops.dao.ApplicationPipelineTaskLogDAO;
import cn.orionsec.ops.entity.domain.ApplicationPipelineTaskLogDO;
import cn.orionsec.ops.entity.vo.app.ApplicationPipelineTaskLogVO;
import cn.orionsec.ops.service.api.ApplicationPipelineTaskLogService;
import cn.orionsec.ops.utils.DataQuery;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用流水线任务日志服务
 *
 * @author 
 * @version 1.0.0
 * @since 2022/4/24 20:53
 */
@Service("applicationPipelineTaskLogService")
public class ApplicationPipelineTaskLogServiceImpl implements ApplicationPipelineTaskLogService {

    @Resource
    private ApplicationPipelineTaskLogDAO applicationPipelineTaskLogDAO;

    @Override
    public List<ApplicationPipelineTaskLogVO> getLogList(Long taskId) {
        LambdaQueryWrapper<ApplicationPipelineTaskLogDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineTaskLogDO>()
                .eq(ApplicationPipelineTaskLogDO::getTaskId, taskId);
        return DataQuery.of(applicationPipelineTaskLogDAO)
                .wrapper(wrapper)
                .list(ApplicationPipelineTaskLogVO.class);
    }

    @Override
    public Integer deleteByTaskIdList(List<Long> taskIdList) {
        Wrapper<ApplicationPipelineTaskLogDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineTaskLogDO>()
                .in(ApplicationPipelineTaskLogDO::getTaskId, taskIdList);
        return applicationPipelineTaskLogDAO.delete(wrapper);
    }

}
