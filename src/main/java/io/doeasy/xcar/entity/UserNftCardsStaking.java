package io.doeasy.xcar.entity;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author kris.wang
 */
@Data
@Builder
public class UserNftCardsStaking {
    private Long id;
    private String ownerAddress;
    private String txHash;
    private Integer tokenId;
    private Integer poolId;
    private String event;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
