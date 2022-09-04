package io.doeasy.xcar.vo.response;

import io.doeasy.xcar.vo.base.BaseResponseVo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * @author kris.wang
 */
@Data
@Accessors
public class UserNftCardsResponseVo extends BaseResponseVo {
    private Long id;
    private String ownerAddress;
    private String txHash;
    private Integer tokenId;
    private Integer poolId;
    private Integer rarity;
    private Integer level;
    private Integer speed;
    private Boolean isStaked;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
