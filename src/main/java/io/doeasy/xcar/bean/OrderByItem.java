package io.doeasy.xcar.bean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * @author kris.wang
 */
@Getter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderByItem {

    @Setter
    String colName;

    String order;

    public OrderByItem asc() {
        this.order = "asc";
        return this;
    }

    public OrderByItem desc() {
        this.order = "desc";
        return this;
    }
}
