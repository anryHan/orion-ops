package com.orion.ops.service.api;

import com.orion.ops.entity.request.DataExportRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 数据导出服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 16:18
 */
public interface DataExportService {

    /**
     * 导出机器
     *
     * @param request  request
     * @param response response
     * @throws IOException IOException
     */
    void exportMachine(DataExportRequest request, HttpServletResponse response) throws IOException;

}
