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
package cn.orionsec.ops.constant.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流水线状态
 *
 * @author 
 * @version 1.0.0
 * @since 2022/4/7 13:47
 */
@AllArgsConstructor
@Getter
public enum PipelineStatus {

    /**
     * 待审核
     */
    WAIT_AUDIT(10),

    /**
     * 审核驳回
     */
    AUDIT_REJECT(20),

    /**
     * 待执行
     */
    WAIT_RUNNABLE(30),

    /**
     * 待调度
     */
    WAIT_SCHEDULE(35),

    /**
     * 执行中
     */
    RUNNABLE(40),

    /**
     * 执行完成
     */
    FINISH(50),

    /**
     * 执行停止
     */
    TERMINATED(60),

    /**
     * 执行失败
     */
    FAILURE(70),

    ;

    private final Integer status;

    public static PipelineStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (PipelineStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
