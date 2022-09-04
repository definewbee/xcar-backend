package io.doeasy.xcar.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * @author kris.wang
 */
@Data
@Accessors(chain = true)
public class UserNftCards {
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
