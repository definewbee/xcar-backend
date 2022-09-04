package io.doeasy.xcar.abey.contract;

import io.doeasy.xcar.entity.ContractOffset;
import io.doeasy.xcar.entity.UserNftCards;
import io.doeasy.xcar.entity.UserNftCardsStaking;
import io.doeasy.xcar.entity.UserNftCardsTransfer;
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
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author kris.wang
 */
@Slf4j
@Component
public class CarNftEventListener implements EventListener {

    @Autowired
    private Web3jService web3jService;

    @Autowired
    private XcarContractService xcarContractService;

    @Autowired
    private ContractOffsetMapper mapper;

    @Value("${abey.contracts.XCarNFT.address}")
    private String carNftAddress;

    @Value("${abey.contracts.XracerStakingPool.address}")
    private String xRracerStakingPoolAddress;

    @Value("${abey.contracts.Upgrader.address}")
    private String upgraderContractAddress;

    @Value("${abey.contracts.XCarNFT.name}")
    private String carNftName;

    @Value("${abey.contracts.step}")
    private BigInteger step;

    @Value("${abey.contracts.start}")
    private BigInteger startBlock;

    @Value("${abey.contracts.XCarNFT.enabled}")
    private boolean enabled;

    @Override
    @Async
    @Scheduled(cron = "${abey.scheduler.cron-expression}")
    @SchedulerLock(name = "XracerCarNftTaskLock")
    public void handle() throws IOException, InterruptedException {

        if(!enabled) {
            log.info("CarNftEventListener is disabled........");
            return;
        }

        log.info("Begin to scan mint related event...");
        BigInteger start = mapper.selectContractOffset(carNftAddress);

        if(start.compareTo(BigInteger.ZERO) == 0 || start.compareTo(startBlock) < 0) {
            start = startBlock;
        }
        //从链上拿当前块号
        BigInteger now = web3jService.getBlockNumber();

        if(start.compareTo(now) > 0) {
            log.error("please reset the offset that should be litter than {}", now);
            return;
        }

        while(true) {
            if(now.compareTo(BigInteger.ZERO) == 0) {
                log.warn("Current block number is zero!");
                break;
            }
            BigInteger end = start.add(step).compareTo(now) > 0 ? now : start.add(step);
            log.info(" ======> 当前扫块, start={}, end={}", start, end);

            // parse ethlogs
            mintFilter(start, end);
            stakingPoolFilter(start, end);
            stakingPoolWithdrawFilter(start, end);
            upgradeEventFilter(start, end);
            transferFilter(start, end);

            // update offset in db
            updateOffset(end);
            start = end;

            if(end.compareTo(now) >= 0) {
                log.warn("====== > exit.");
                break;
            }

            TimeUnit.MILLISECONDS.sleep(100);
        }
        log.info("Scan mint related event finished...");
    }

    /**
     * 监听Mint事件
     * @param start
     * @param end
     * @throws IOException
     */
    private void mintFilter(BigInteger start, BigInteger end) throws IOException {
        Event event = new ContractsEventBuilder().build(ContractsEventEnum.MINT);
        EthLog ethLog = web3jService.filterEthLog(start, end, event, carNftAddress);

        if(!ObjectUtils.isEmpty(ethLog)) {
            for(EthLog.LogResult l : ethLog.getLogs()) {
                Log result = (Log) l.get();
                List<Type> args = FunctionReturnDecoder.decode(result.getData(), event.getParameters());
                List<String> topics = result.getTopics();

                if (!CollectionUtils.isEmpty(args)) {
                    UserNftCards card = new UserNftCards()
                            .setOwnerAddress(args.get(0).getValue().toString())
                            .setTxHash(result.getTransactionHash())
                            .setTokenId(new Integer(args.get(1).getValue().toString()))
                            .setRarity(new Integer(args.get(2).getValue().toString()))
                            .setLevel(new Integer(args.get(3).getValue().toString()))
                            .setSpeed(new Integer(args.get(4).getValue().toString()));
                    xcarContractService.createUserNftCards(card);
                } else if(!CollectionUtils.isEmpty(topics) && topics.size() > 0){
                    String from = EthLogsParser.hexToAddress(topics.get(1));
                    String to = EthLogsParser.hexToAddress(topics.get(2));
                    BigInteger tokenId = EthLogsParser.hexToBigInteger(topics.get(3));
                    log.info("from = {}\n to = {} \n tokenId = {}", from, to, tokenId);
                }
            }
        }
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
                                .event("Staking")
                                .build();
                        xcarContractService.createUserNftCardsStaking(staking);
                    }

                } else if(!CollectionUtils.isEmpty(topics) && topics.size() == 4){
                    log.error("====== please parse topics.");
                }
            }
        }
    }

    private void stakingPoolWithdrawFilter(BigInteger start, BigInteger end) throws IOException {

        Event event = new ContractsEventBuilder().build(ContractsEventEnum.WITHDRAW);
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
                                .event("Withdraw")
                                .build();
                        xcarContractService.createUserNftCardsStaking(staking);
                    }

                } else if(!CollectionUtils.isEmpty(topics) && topics.size() == 4){
                    log.error("====== please parse topics.");
                }
            }
        }
    }

    /**
     * 监听Updrade事件
     * @param start
     * @param end
     * @throws IOException
     */
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

    /**
     * 监听Transfer事件
     * @param start
     * @param end
     * @throws IOException
     */
    private void transferFilter(BigInteger start, BigInteger end) throws IOException {
        Event event = new ContractsEventBuilder().build(ContractsEventEnum.NFT_TRANSFER);
        EthLog ethLog = web3jService.filterEthLog(start, end, event, carNftAddress);
        if(!ObjectUtils.isEmpty(ethLog)) {
            for(EthLog.LogResult l : ethLog.getLogs()) {
                Log result = (Log) l.get();
                List<Type> args = FunctionReturnDecoder.decode(result.getData(), event.getParameters());
                List<String> topics = result.getTopics();

                if (!CollectionUtils.isEmpty(args)) {
                    UserNftCardsTransfer transfer = UserNftCardsTransfer.builder()
                            .fromAddress(args.get(0).getValue().toString())
                            .toAddress(args.get(1).getValue().toString())
                            .tokenId(new Integer(args.get(2).getValue().toString()))
                            .build();
                    xcarContractService.transferNftCard(transfer);

                } else if(!CollectionUtils.isEmpty(topics) && topics.size() == 4){
                    // TODO: 如果log data为空，需要解析topics数据
                }
            }
        }
    }

    private void updateOffset(BigInteger offset) {
        mapper.update(
                ContractOffset.builder()
                        .contractAddress(carNftAddress)
                        .contractName(carNftName)
                        .blockOffset(offset).build());
    }

}
