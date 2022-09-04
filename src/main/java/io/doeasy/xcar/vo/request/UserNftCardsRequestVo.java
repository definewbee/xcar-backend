package io.doeasy.xcar.vo.request;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author kris.wang
 */
@Data
@Accessors
public class UserNftCardsRequestVo {
    private String ownerAddress;
    private Boolean isStaked;
    private Integer poolId;
}
