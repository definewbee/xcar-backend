package io.doeasy.xcar.abey.contract;

import io.doeasy.xcar.entity.ContractOffset;
import io.doeasy.xcar.entity.Referrer;
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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author kris.wang
 */
@Slf4j
@Service
public class ReferrerEventListener implements EventListener{

    @Autowired
    private Web3jService web3jService;

    @Autowired
    private ContractOffsetMapper mapper;

    @Autowired
    private XcarContractService xcarContractService;

    @Value("${abey.contracts.ReferrerControl.address}")
    private String eventReferralControlAddress;

    @Value("${abey.contracts.ReferrerControl.name}")
    private String eventReferralControlName;

    @Value("${abey.contracts.step}")
    private BigInteger step;

    @Value("${abey.contracts.start}")
    private BigInteger startBlock;

    @Value("${abey.contracts.ReferrerControl.enabled}")
    private boolean enabled;

    @Override
    @Async
    @Scheduled(cron = "${abey.scheduler.cron-expression}")
    @SchedulerLock(name = "EventReferralRewardTaskLock")
    public void handle() throws IOException, InterruptedException {

        if(!enabled) {
            log.info("ReferrerEventListener is disabled........");
            return;
        }

        log.info("Begin to scan AddReferral event...");
        BigInteger start = mapper.selectContractOffset(this.eventReferralControlAddress);

        if(start.compareTo(BigInteger.ZERO) == 0 || start.compareTo(startBlock) < 0) {
            start = startBlock;
        }
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
            referrerEventFilter(start, end);
            activationReferralEventFilter(start, end);

            // update offset in db
            updateOffset(end);
            start = end;

            if(end.compareTo(now) >= 0) {
                log.warn("====== > exit.");
                break;
            }

            TimeUnit.MILLISECONDS.sleep(100);
        }
        log.info("Scan AddReferral event finished...");
    }

    private void referrerEventFilter(BigInteger start, BigInteger end) throws IOException {
        Event event = new ContractsEventBuilder().build(ContractsEventEnum.ADD_REFERRER);
        EthLog ethLog = web3jService.filterEthLog(start, end, event, eventReferralControlAddress);



        if(!ObjectUtils.isEmpty(ethLog)) {
            List<EventValues> eventValues = EthLogsParser.extractEventParameters(event, ethLog);
            if(!CollectionUtils.isEmpty(eventValues)) {
                eventValues.stream().filter(Objects::nonNull).collect(Collectors.toList());
            }

            for(EthLog.LogResult l : ethLog.getLogs()) {
                Log result = (Log) l.get();
                List<Type> args = FunctionReturnDecoder.decode(result.getData(), event.getParameters());
                List<String> topics = result.getTopics();

                if (!CollectionUtils.isEmpty(args)) {
                    log.warn("please parse result.getData()...");
                } else if(!CollectionUtils.isEmpty(topics) && topics.size() == 3){
                    //被推荐人
                    String referal = EthLogsParser.hexToAddress(topics.get(1));
                    //推荐人
                    String referrer = EthLogsParser.hexToAddress(topics.get(2));
                    log.info("referal = {}\n referrer = {}", referal, referrer);
                    Referrer r = Referrer.builder()
                            .txHash(result.getTransactionHash())
                            .referrerAddress(referrer)
                            .referalAddress(referal)
                            .isActived(false)
                            .build();
                    xcarContractService.addReferral(r);
                }
            }
        }
    }

    private void activationReferralEventFilter(BigInteger start, BigInteger end) throws IOException {
        Event event = new ContractsEventBuilder().build(ContractsEventEnum.ACTIVATION_REFERRAL);
        EthLog ethLog = web3jService.filterEthLog(start, end, event, eventReferralControlAddress);

        if(!ObjectUtils.isEmpty(ethLog)) {
            for(EthLog.LogResult l : ethLog.getLogs()) {
                Log result = (Log) l.get();
                List<Type> args = FunctionReturnDecoder.decode(result.getData(), event.getParameters());
                List<String> topics = result.getTopics();

                if (!CollectionUtils.isEmpty(args)) {
                    log.warn("please parse result.getData()...");
                } else if(!CollectionUtils.isEmpty(topics) && topics.size() == 3){
                    //被推荐人
                    String referal = EthLogsParser.hexToAddress(topics.get(1));
                    //推荐人
                    String referrer = EthLogsParser.hexToAddress(topics.get(2));
                    log.info("referal = {}\n referrer = {}", referal, referrer);
                    Referrer r = Referrer.builder()
                            .referrerAddress(referrer)
                            .referalAddress(referal)
                            .build();
                    xcarContractService.activeReferral(r);
                }
            }
        }
    }

    private void updateOffset(BigInteger offset) {
        mapper.update(
                ContractOffset.builder()
                        .contractAddress(eventReferralControlAddress)
                        .contractName(eventReferralControlName)
                        .blockOffset(offset).build());
    }
}
