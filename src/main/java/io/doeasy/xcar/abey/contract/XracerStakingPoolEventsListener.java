package io.doeasy.xcar.abey.contract;

import io.doeasy.xcar.entity.ContractOffset;
import io.doeasy.xcar.entity.UserNftCardsStaking;
import io.doeasy.xcar.mapper.ContractOffsetMapper;
import io.doeasy.xcar.service.Web3jService;
import io.doeasy.xcar.service.XcarContractService;
import io.doeasy.xcar.util.EthLogsParser;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author kris.wang
 */
@Slf4j
public class XracerStakingPoolEventsListener implements EventListener {
    @Autowired
    private Web3jService web3jService;

    @Autowired
    private XcarContractService xcarContractService;

    @Autowired
    private ContractOffsetMapper mapper;

    @Value("${abey.contracts.XracerStakingPool.address}")
    private String xRracerStakingPoolAddress;

    @Value("${abey.contracts.XracerStakingPool.name}")
    private String xRracerStakingPoolName;

    @Value("${abey.contracts.step}")
    private BigInteger step;

    @Value("${abey.contracts.start}")
    private BigInteger startBlock;


    @Override
    public void handle() throws IOException, InterruptedException {
        log.info("Begin to scan staking event...");
        BigInteger start = mapper.selectContractOffset(xRracerStakingPoolAddress);

        if(start.compareTo(BigInteger.ZERO) == 0 || start.compareTo(startBlock) < 0) {
            start = startBlock;
        }
        BigInteger now = web3jService.getBlockNumber();

        while(true) {
            if(now.compareTo(BigInteger.ZERO) == 0) {
                log.warn("Current block number is zero!");
                break;
            }
            BigInteger end = start.add(step).compareTo(now) > 0 ? now : start.add(step);
            // parse ethlogs
            stakingPoolFilter(start, end);

            // update offset in db
            start = end;
            updateOffset(start);

            if(end.compareTo(now) > 0) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(2000);
        }
        log.info("Scan staking event finished...");
    }

    private void stakingPoolFilter(BigInteger start, BigInteger end) throws IOException {
        Event event = new ContractsEventBuilder().build(ContractsEventEnum.STAKING);
        EthLog ethLog = web3jService.filterEthLog(start, end, event, xRracerStakingPoolAddress);

        if(!ObjectUtils.isEmpty(ethLog)) {
            for(EthLog.LogResult l : ethLog.getLogs()) {
                Log result = (Log) l.get();
                List<Type> args = FunctionReturnDecoder.decode(result.getData(), event.getParameters());
                List<String> topics = result.getTopics();

                if (!CollectionUtils.isEmpty(args)) {
                    List tokenIdList = (List)args.get(2).getValue();
                    for(Object tokenId : tokenIdList) {
                        UserNftCardsStaking staking = UserNftCardsStaking.builder()
                                .ownerAddress(args.get(0).getValue().toString())
                                .txHash(result.getTransactionHash())
                                .tokenId(new Integer(((Uint256)tokenId).getValue().toString()))
                                .poolId(new Integer(args.get(1).getValue().toString()))
                                .build();
                        xcarContractService.createUserNftCardsStaking(staking);
                    }

                } else if(!CollectionUtils.isEmpty(topics) && topics.size() == 4){
                    log.error("====== please parse topics.");
                }
            }
        }
    }

    private void updateOffset(BigInteger offset) {
        mapper.update(
                ContractOffset.builder()
                        .contractAddress(xRracerStakingPoolAddress)
                        .contractName(xRracerStakingPoolName)
                        .blockOffset(offset).build());
    }
}
