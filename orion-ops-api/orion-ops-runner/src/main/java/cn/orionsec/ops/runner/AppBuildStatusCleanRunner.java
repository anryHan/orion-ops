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
package cn.orionsec.ops.runner;

import cn.orionsec.ops.constant.app.ActionStatus;
import cn.orionsec.ops.constant.app.BuildStatus;
import cn.orionsec.ops.constant.app.StageType;
import cn.orionsec.ops.dao.ApplicationActionLogDAO;
import cn.orionsec.ops.dao.ApplicationBuildDAO;
import cn.orionsec.ops.entity.domain.ApplicationActionLogDO;
import cn.orionsec.ops.entity.domain.ApplicationBuildDO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 清空构建状态
 *
 * @author 
 * @version 1.0.0
 * @since 2021/12/6 18:03
 */
@Component
@Order(2300)
@Slf4j
public class AppBuildStatusCleanRunner implements CommandLineRunner {

    @Resource
    private ApplicationBuildDAO applicationBuildDAO;

    @Resource
    private ApplicationActionLogDAO applicationActionLogDAO;

    @Override
    public void run(String... args) {
        log.info("重置应用构建状态-开始");
        // 更新构建状态
        Wrapper<ApplicationBuildDO> buildWrapper = new LambdaQueryWrapper<ApplicationBuildDO>()
                .in(ApplicationBuildDO::getBuildStatus, BuildStatus.WAIT.getStatus(), BuildStatus.RUNNABLE.getStatus());
        ApplicationBuildDO updateBuild = new ApplicationBuildDO();
        updateBuild.setBuildStatus(BuildStatus.TERMINATED.getStatus());
        updateBuild.setUpdateTime(new Date());
        applicationBuildDAO.update(updateBuild, buildWrapper);

        // 更新操作状态
        LambdaQueryWrapper<ApplicationActionLogDO> actionWrapper = new LambdaQueryWrapper<ApplicationActionLogDO>()
                .eq(ApplicationActionLogDO::getStageType, StageType.BUILD.getType())
                .in(ApplicationActionLogDO::getRunStatus, ActionStatus.WAIT.getStatus(), ActionStatus.RUNNABLE.getStatus());
        ApplicationActionLogDO updateAction = new ApplicationActionLogDO();
        updateAction.setRunStatus(ActionStatus.TERMINATED.getStatus());
        updateAction.setUpdateTime(new Date());
        applicationActionLogDAO.update(updateAction, actionWrapper);
        log.info("重置应用构建状态-结束");
    }

}
