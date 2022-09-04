package io.doeasy.xcar.controller;

import io.doeasy.xcar.entity.Referrer;
import io.doeasy.xcar.entity.UserNftCards;
import io.doeasy.xcar.service.XcarContractService;
import io.doeasy.xcar.vo.PageQueryVo;
import io.doeasy.xcar.vo.base.BasePageInfo;
import io.doeasy.xcar.vo.base.BaseResponse;
import io.doeasy.xcar.vo.request.ReferrerQueryRequestVo;
import io.doeasy.xcar.vo.request.UserNftCardsRequestVo;
import io.doeasy.xcar.vo.response.ReferrerQueryResponseVo;
import io.doeasy.xcar.vo.response.UserNftCardsResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author kris.wang
 */
@RestController
@RequestMapping("/xracer")
@Slf4j
public class XracerNftController extends BaseController{

    @Autowired
    private XcarContractService service;

    @PostMapping(value = "/referrer/weekly" , consumes = {"application/json"})
    public BaseResponse<List<ReferrerQueryResponseVo>> getReferrersWeeklyList(@RequestBody @Validated ReferrerQueryRequestVo referrerQueryVo) {
        return objectSuccessResponseWhenDataNotEmpty(service.getReferrersTopNList(referrerQueryVo));
    }

    @PostMapping(value = "/referrer/monthly" , consumes = {"application/json"})
    public BaseResponse<List<ReferrerQueryResponseVo>> getReferrersMonthlyList(@RequestBody @Validated ReferrerQueryRequestVo referrerQueryVo) {
        return objectSuccessResponseWhenDataNotEmpty(service.getReferrersTopNList(referrerQueryVo));
    }

    @PostMapping(value ="/cards/getNftCardsByPage", consumes = {"application/json"}, produces = {"application/json"})
    public BaseResponse<BasePageInfo<UserNftCardsResponseVo>> getNftCardsByPage(@RequestBody UserNftCardsRequestVo request,
                                                                                String q,
                                                                                @RequestParam(defaultValue = "1") int currentPage,
                                                                                @RequestParam(defaultValue = "10") int pageSize,
                                                                                @RequestParam(defaultValue = "0") long start,
                                                                                @RequestParam(defaultValue = "0") long end) {
        return BaseResponse.success(service.pageQueryUserCards(PageQueryVo.builder().currentPage(currentPage).pageSize(pageSize)
                .build(), request, q, start, end));

    }


}
