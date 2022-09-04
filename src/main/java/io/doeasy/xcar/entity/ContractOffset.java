package io.doeasy.xcar.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * @author kris.wang
 */
@Data
@Builder
public class ContractOffset {
    private String contractAddress;
    private String contractName;
    private BigInteger blockOffset;
    private Timestamp recordedAt;
}
