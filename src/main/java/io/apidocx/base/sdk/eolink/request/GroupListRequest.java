package io.apidocx.base.sdk.eolink.request;

import lombok.Data;

@Data
public class GroupListRequest {

    /**
     * 项目标识
     */
    private String projectHashKey;

    /**
     * 空间标识
     */
    private String spaceKey;

}
