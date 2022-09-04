package io.doeasy.xcar.vo.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author kris.wang
 */
@Data
@Builder
public class ReferrerQueryResponseVo {
    private String referrer;
    private Integer count;
    private Boolean isActived;
}
