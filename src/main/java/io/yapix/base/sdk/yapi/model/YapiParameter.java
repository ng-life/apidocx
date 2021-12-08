package io.yapix.base.sdk.yapi.model;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * 表单参数
 */
public class YapiParameter {

    /** 参数名字 */
    private String name;

    /**
     * 类型
     */
    private String type;

    /** 描述 */
    private String desc;

    /** 是否必填 */
    private String required;

    /** 示例 */
    private String example;

    /** 值 */
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        YapiParameter that = (YapiParameter) o;
        return Objects.equals(name, that.name) &&
                nullOrEquals(type, that.type) &&
                Objects.equals(desc, that.desc) &&
                Objects.equals(example, that.example) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, desc, example, value);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", YapiParameter.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("type='" + type + "'")
                .add("desc='" + desc + "'")
                .add("required='" + required + "'")
                .add("example='" + example + "'")
                .add("value='" + value + "'")
                .toString();
    }

    private boolean nullOrEquals(Object a, Object b) {
        return a == null || b == null || Objects.equals(a, b);
    }

}
