package io.doeasy.xcar.service;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import io.doeasy.xcar.entity.Referrer;
import io.doeasy.xcar.entity.UserNftCards;
import io.doeasy.xcar.entity.UserNftCardsStaking;
import io.doeasy.xcar.entity.UserNftCardsTransfer;
import io.doeasy.xcar.exception.XcarBizException;
import io.doeasy.xcar.mapper.ReferrerControlMapper;
import io.doeasy.xcar.mapper.UserNftCardsMapper;
import io.doeasy.xcar.mapper.UserNftCardsStakingMapper;
import io.doeasy.xcar.mapper.UserNftCardsTransferMapper;
import io.doeasy.xcar.util.DataQueryUtils;
import io.doeasy.xcar.vo.PageQueryVo;
import io.doeasy.xcar.vo.base.BasePageInfo;
import io.doeasy.xcar.vo.request.ReferrerQueryRequestVo;
import io.doeasy.xcar.vo.request.UserNftCardsRequestVo;
import io.doeasy.xcar.vo.response.ReferrerQueryResponseVo;
import io.doeasy.xcar.vo.response.UserNftCardsResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kris.wang
 */
@Service
@Slf4j
public class XcarContractService {

    @Autowired
    private UserNftCardsMapper userNftCardsMapper;

    @Autowired
    private UserNftCardsStakingMapper userNftCardsStakingMapper;

    @Autowired
    private UserNftCardsTransferMapper userNftCardsTransferMapper;

    @Autowired
    private ReferrerControlMapper referrerControlMapper;

    public Long createUserNftCards(UserNftCards userNftCards) {
        Assert.notNull(userNftCards, "UserNftCards cannot be null");
        return userNftCardsMapper.insert(userNftCards);
    }

    @Transactional
    public Long createUserNftCardsStaking(UserNftCardsStaking staking) {
        Assert.notNull(staking, "Staking cannot be null");
        Long stakingId= userNftCardsStakingMapper.insert(staking);

        Map<String, Object> args = new HashMap<String, Object>();
        args.put("ownerAddress", staking.getOwnerAddress());
        args.put("tokenId", staking.getTokenId());
        args.put("poolId", staking.getPoolId());
        args.put("event", staking.getEvent());
        userNftCardsMapper.updateStakedStatus(args);

        return stakingId;
    }

    @Transactional
    public void transferNftCard(UserNftCardsTransfer transfer) {
        Assert.notNull(transfer, "transfer cannot be null");
        userNftCardsTransferMapper.insert(transfer);

        //查询用户卡牌是否存在，如果存在，删除fromAddress对应卡牌，并将卡牌存入toAddress对应的记录
        Map<String,Object> fromCardsArgs = new HashMap<String,Object>();
        fromCardsArgs.put("ownerAddress", transfer.getFromAddress());
        fromCardsArgs.put("tokenId", transfer.getTokenId());
        UserNftCards fromCard = userNftCardsMapper.selectOneByMap(fromCardsArgs);

        if(ObjectUtils.isEmpty(fromCard)) {
            log.error("Can't find card with ownerAddress is {} and tokenId is {}", transfer.getFromAddress(), transfer.getTokenId());
            throw new XcarBizException("Can't find card");
        } else {
            userNftCardsMapper.deleteByCondition(fromCardsArgs);
            fromCard.setOwnerAddress(transfer.getToAddress());
            UserNftCards toCard = new UserNftCards();
            BeanUtils.copyProperties(fromCard, toCard);
            userNftCardsMapper.insert(toCard);
        }
    }

    public Long addReferral(Referrer referrer) {
        Assert.notNull(referrer, "Referrer cannot be null");
        return referrerControlMapper.insert(referrer);
    }

    public void activeReferral(Referrer referrer) {
        Assert.notNull(referrer, "request cannot be empty.");
        referrerControlMapper.update(referrer);
    }

    /**
     * 获取推荐关系
     * @param queryVo
     * {
     *     {
     *     "startDate": "08/26/2022",
     *     "interval": 7, //只能是7天或者30天
     *     "topN": 10
     * }
     * }
     * @return
     */
    public List<ReferrerQueryResponseVo> getReferrersTopNList(ReferrerQueryRequestVo queryVo) {
        Assert.notNull(queryVo, "queryVo cannot be null");
        return referrerControlMapper.listTopnReferres(queryVo);
    }

    public BasePageInfo<UserNftCardsResponseVo> pageQueryUserCards(PageQueryVo pageQueryVo, UserNftCardsRequestVo requestVo, String fuzzyQueryValue,
                                                          long queryStartTime, long queryEndTime) {
        return DataQueryUtils.pageQuery(userNftCardsMapper, requestVo, pageQueryVo, UserNftCards::new
                , UserNftCardsResponseVo::new, null, null);
    }

    public void upgrade(Map<String, Object> args) {
        Assert.notEmpty(args, "args cannot be empty.");
        userNftCardsMapper.upgrade(args);
    }



}
