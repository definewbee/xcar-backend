package io.doeasy.xcar.entity;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author kris.wang
 */
@Data
@Builder
public class Referrer {
    private Long id;
    private String txHash;
    private String referrerAddress;
    private String referalAddress;
    private Boolean isActived;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
