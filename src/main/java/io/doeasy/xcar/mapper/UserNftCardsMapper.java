package io.doeasy.xcar.mapper;

import io.doeasy.xcar.entity.UserNftCards;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @author kris
 */
@Mapper
public interface UserNftCardsMapper extends BaseMapper<UserNftCards>{

    /**
     * 质押卡牌，修改卡牌质押状态为true
     * @return
     * @param args
     */
    void updateStakedStatus(Map<String, Object> args);

    /**
     * 升级卡牌
     * @param args
     */
    void upgrade(Map<String, Object> args);

}
