package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 环境变量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 11:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MachineEnvRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 机器id
     */
    private String machineId;

    /**
     * key
     */
    private String key;

    /**
     * value
     */
    private String value;

    /**
     * 描述
     */
    private String description;

}
