package io.doeasy.xcar.util;

import io.doeasy.xcar.bean.FuzzySearchItem;
import io.doeasy.xcar.bean.OrderByItem;
import io.doeasy.xcar.mapper.BaseMapper;
import io.doeasy.xcar.vo.PageQueryVo;
import io.doeasy.xcar.vo.base.BasePageInfo;
import io.doeasy.xcar.vo.base.BaseResponseVo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author kris.wang
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataQueryUtils {

    /**
     * 共用的分页查询方法
     *
     * @param mapper                   当前业务实体对应的mapper
     * @param requestVo                查询入参
     * @param pageQueryVo              分页查询参数
     * @param generateEntity           构造业务实体的方式
     * @param generateResponseVo       构造responseVo的方式
     * @param entityTransferToResponse entity转化为responseVo的方式，若为空，则选用默认的属性拷贝
     * @param <Entity>                 业务实体对象类型
     * @param <RequestVo>              该业务请求入参对象类
     * @param <ResponseVo>             该业务响应对象类型
     * @param fuzzySearchItemList  自定义模糊查询条件
     * @return 分页结果
     */
    public static <Entity, RequestVo, ResponseVo extends BaseResponseVo> BasePageInfo<ResponseVo> pageQuery(
            @NonNull BaseMapper<Entity> mapper, @NonNull RequestVo requestVo, @NonNull PageQueryVo pageQueryVo,
            @NonNull Supplier<Entity> generateEntity, @NonNull Supplier<ResponseVo> generateResponseVo,
            Function<Entity, ResponseVo> entityTransferToResponse, List<FuzzySearchItem> fuzzySearchItemList) {
        return pageQueryInner(mapper, requestVo, pageQueryVo, generateEntity, generateResponseVo, entityTransferToResponse, null, fuzzySearchItemList);
    }

    private static <Entity, RequestVo, ResponseVo extends BaseResponseVo> BasePageInfo<ResponseVo> pageQueryInner(
            @NonNull BaseMapper<Entity> mapper, @NonNull RequestVo requestVo, @NonNull PageQueryVo pageQueryVo,
            @NonNull Supplier<Entity> generateEntity, @NonNull Supplier<ResponseVo> generateResponseVo,
            Function<Entity, ResponseVo> entityTransferToResponse, List<OrderByItem> orderByItems, List<FuzzySearchItem> fuzzySearchItemList) {

        Entity entity = generateEntity.get();
        BeanUtils.copyProperties(requestVo, entity);
        List<Entity> packages = mapper.listPage(entity, pageQueryVo.getOffset(), pageQueryVo.getPageSize(),
                orderByItems == null ? Collections.emptyList() : orderByItems,
                fuzzySearchItemList == null ? Collections.emptyList() : fuzzySearchItemList);

        BasePageInfo<ResponseVo> basePageInfo = BasePageInfo.<ResponseVo>builder().currentPage(pageQueryVo.getCurrentPage())
                .pageSize(pageQueryVo.getPageSize()).build();
        if (!CollectionUtils.isEmpty(packages)) {
            List<ResponseVo> dataList = packages.stream().map(innerEntity -> {
                if (entityTransferToResponse == null) {
                    ResponseVo responseVo = generateResponseVo.get();
                    BeanUtils.copyProperties(innerEntity, responseVo);
                    return responseVo;
                } else {
                    return entityTransferToResponse.apply(innerEntity);
                }
            }).collect(Collectors.toList());
            basePageInfo.setDataList(dataList);
            basePageInfo.setTotal(mapper.count(entity, fuzzySearchItemList == null ? Collections.emptyList() : fuzzySearchItemList));
        }
        return basePageInfo;
    }
}
