package io.doeasy.xcar.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author kris.wang
 */
@Data
@Builder
public class PageQueryVo {

    private int currentPage;

    private int pageSize;

    /**
     * 计算mysql下limit语句的offset
     * @return offset
     */
    public long getOffset() {
        return Math.max(0, (long) (currentPage - 1) * pageSize);
    }
}
