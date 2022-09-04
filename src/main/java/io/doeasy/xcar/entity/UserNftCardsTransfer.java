package io.doeasy.xcar.entity;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author kris.wang
 */
@Data
@Builder
public class UserNftCardsTransfer {
    private Long id;
    private String txHash;
    private String fromAddress;
    private String toAddress;
    private Integer tokenId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
