package io.doeasy.xcar.abey.contract;

import io.doeasy.xcar.entity.ContractOffset;
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
import org.web3j.abi.DefaultFunctionReturnDecoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author kris.wang
 */
@Slf4j
//@Component
public class UpgraderEventListener implements EventListener{

    @Autowired
    private Web3jService web3jService;

    @Autowired
    private ContractOffsetMapper mapper;

    @Autowired
    private XcarContractService xcarContractService;

    @Value("${abey.contracts.Upgrader.address}")
    private String upgraderContractAddress;

    @Value("${abey.contracts.Upgrader.name}")
    private String upgraderContractName;

    @Value("${abey.contracts.step}")
    private BigInteger step;

    @Value("${abey.contracts.start}")
    private BigInteger startBlock;

    @Value("${abey.contracts.Upgrader.enabled}")
    private boolean enabled;

//    @Override
//    @Async
//    @Scheduled(cron = "${abey.scheduler.cron-expression}")
//    @SchedulerLock(name = "UpgraderTaskLock")
    public void handle() throws IOException, InterruptedException {

        if(!enabled) {
            log.info("UpgraderEventListener is disabled........");
            return;
        }

        log.info("Begin to scan Upgrader's upgrade event...");
        BigInteger start = mapper.selectContractOffset(this.upgraderContractAddress);

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
            upgradeEventFilter(start, end);
            // update offset in db
            updateOffset(end);
            start = end;

            if(end.compareTo(now) > 0) {
                break;
            }

            TimeUnit.MILLISECONDS.sleep(100);
        }
        log.info("Scan Upgrader's upgrade event finished...");
    }

    private void upgradeEventFilter(BigInteger start, BigInteger end)  throws IOException{
        Event event = new ContractsEventBuilder().build(ContractsEventEnum.UPGRADE);
        EthLog ethLog = web3jService.filterEthLog(start, end, event, upgraderContractAddress);

        if(!ObjectUtils.isEmpty(ethLog)) {
            for (EthLog.LogResult l : ethLog.getLogs()) {
                Log result = (Log) l.get();
                //tokenId
                Integer tokenId = EthLogsParser.hexToBigInteger(result.getTopics().get(1)).intValue();
                event.getParameters().remove(event.getParameters().size() - 1);
                List<Type> args = FunctionReturnDecoder.decode(result.getData(), event.getParameters());
                if(!CollectionUtils.isEmpty(args)) {
                    String ownerAddress = args.get(0).getValue().toString();
                    Integer targetLevel = new Integer(args.get(1).getValue().toString());
                    Integer targetSpeed = new Integer(args.get(2).getValue().toString());
                    Map<String, Object> props = new HashMap<>();
                    props.put("ownerAddress", ownerAddress);
                    props.put("targetLevel", targetLevel);
                    props.put("targetSpeed", targetSpeed);
                    props.put("tokenId", tokenId);
                    xcarContractService.upgrade(props);
                }

            }
        }
    }

    private void updateOffset(BigInteger offset) {
        mapper.update(
                ContractOffset.builder()
                        .contractAddress(this.upgraderContractAddress)
                        .contractName(this.upgraderContractName)
                        .blockOffset(offset).build());
    }
}
