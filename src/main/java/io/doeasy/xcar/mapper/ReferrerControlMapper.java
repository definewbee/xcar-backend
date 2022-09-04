package io.doeasy.xcar.mapper;

import io.doeasy.xcar.entity.Referrer;
import io.doeasy.xcar.vo.request.ReferrerQueryRequestVo;
import io.doeasy.xcar.vo.response.ReferrerQueryResponseVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author kris.wang
 */

@Mapper
public interface ReferrerControlMapper extends BaseMapper<Referrer>{

    List<ReferrerQueryResponseVo> listTopnReferres(ReferrerQueryRequestVo queryVo);

    void update(Referrer referrer);
}
