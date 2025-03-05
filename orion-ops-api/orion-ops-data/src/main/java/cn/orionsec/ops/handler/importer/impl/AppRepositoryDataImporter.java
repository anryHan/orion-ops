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
package cn.orionsec.ops.handler.importer.impl;

import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.constant.app.RepositoryStatus;
import cn.orionsec.ops.dao.ApplicationRepositoryDAO;
import cn.orionsec.ops.entity.domain.ApplicationRepositoryDO;
import cn.orionsec.ops.entity.importer.DataImportDTO;
import cn.orionsec.ops.utils.Utils;

import java.io.File;

/**
 * 应用仓库 数据导入器
 *
 * @author 
 * @version 1.0.0
 * @since 2022/9/9 17:26
 */
public class AppRepositoryDataImporter extends AbstractDataImporter<ApplicationRepositoryDO> {

    private static final ApplicationRepositoryDAO applicationRepositoryDAO = SpringHolder.getBean(ApplicationRepositoryDAO.class);

    public AppRepositoryDataImporter(DataImportDTO importData) {
        super(importData, applicationRepositoryDAO);
    }

    @Override
    protected void insertFiller(ApplicationRepositoryDO row) {
        row.setRepoStatus(RepositoryStatus.UNINITIALIZED.getStatus());
    }

    @Override
    protected void updateFiller(ApplicationRepositoryDO row) {
        Long id = row.getId();
        ApplicationRepositoryDO beforeRepo = applicationRepositoryDAO.selectById(id);
        if (beforeRepo != null && !beforeRepo.getRepoUrl().equals(row.getRepoUrl())) {
            // 如果修改了url则状态改为未初始化
            row.setRepoStatus(RepositoryStatus.UNINITIALIZED.getStatus());
            // 删除 event 目录
            File clonePath = new File(Utils.getRepositoryEventDir(id));
            Files1.delete(clonePath);
        }
    }

}
