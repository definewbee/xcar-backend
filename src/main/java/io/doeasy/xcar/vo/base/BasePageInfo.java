package io.doeasy.xcar.vo.base;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kris.wang
 */
@Data
@Builder
public class BasePageInfo<T> {
    private Integer currentPage;
    private Integer pageSize;
    private long total;

    private List<T> dataList;
}
