package io.yapix.base.sdk.yapi.model;

import lombok.Data;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * 表单参数
 */
@Data
public class YapiParameter {

    /**
     * 参数名字
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 描述
     */
    private String desc;

    /**
     * 是否必填
     */
    private String required;

    /**
     * 示例
     */
    private String example;

    /**
     * 值
     */
    private String value;

}
