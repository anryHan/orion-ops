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
package cn.orionsec.ops.task.fixed;

import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.ops.service.api.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 系统占用磁盘分析任务
 *
 * @author 
 * @version 1.0.0
 * @since 2022/2/17 13:57
 */
@Slf4j
@Component
public class SystemSpaceAnalysisTask {

    @Resource
    private SystemService systemService;

    @Scheduled(cron = "0 0 0,1,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23 * * ?")
    private void analysisSystemSpace() {
        log.info("task-执行占用磁盘空间统计-开始 {}", Dates.current());
        // 不考虑多线程计算
        systemService.analysisSystemSpace();
        log.info("task-执行占用磁盘空间统计-结束 {}", Dates.current());
    }

}
