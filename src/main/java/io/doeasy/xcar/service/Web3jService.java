package io.doeasy.xcar.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.datatypes.Event;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;

import java.io.IOException;
import java.math.BigInteger;

/**
 * @author kris.wang
 */
@Service
@Log4j2
public class Web3jService {

    @Autowired
    private Web3j web3j;

    public BigInteger getBlockNumber() {
         BigInteger blockNumber = new BigInteger("0");
         try {
            blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            log.info(" ==========> the current block number is {}: " ,blockNumber);
         } catch ( IOException e) {
             log.error("get block number failed, IOException: {}", e);
         }
         return blockNumber;
    }

    public EthLog filterEthLog(BigInteger start, BigInteger end, Event event, String contractAddress) throws IOException {
        DefaultBlockParameter startBlock = DefaultBlockParameter.valueOf(start);
        DefaultBlockParameter endBlock = DefaultBlockParameter.valueOf(end);
        EthFilter filter = new EthFilter(startBlock, endBlock, contractAddress);
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethGetLogs(filter).send();
    }

}
