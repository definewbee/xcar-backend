package io.doeasy.xcar.mapper;

import io.doeasy.xcar.entity.ContractOffset;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;

/**
 * @author kris.wang
 */
@Mapper
public interface ContractOffsetMapper extends BaseMapper<ContractOffset>{

    /**
     * select contract offset info
     * @param contractAddress
     * @return
     */
    BigInteger selectContractOffset(String contractAddress);

    /**
     * update offset
     * @param offset
     * @return
     */
    boolean update(ContractOffset offset);
}
