package io.doeasy.xcar.vo.request;

import io.doeasy.xcar.validator.Interval;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


/**
 * @author kris.wang
 */
@Data
@Accessors
public class ReferrerQueryRequestVo {


    @Pattern(regexp = "\\d{2}\\/\\d{2}\\/\\d{4}")
    private String startDate;

    @NotNull
    @Interval(value = "7, 30", message = "{interval.message}")
    private Integer interval;

    @NotNull
    private Integer topN;
}
