package io.doeasy.xcar.bean;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * @author kris.wang
 */
@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FuzzySearchItem {

    /**
     * 条件匹配的目标列名
     */
    String colName;

    /**
     * 模糊条件内容
     */
    String fuzzyValue;
}
