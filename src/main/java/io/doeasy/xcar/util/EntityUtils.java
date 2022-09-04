package io.doeasy.xcar.util;

import org.springframework.beans.BeanUtils;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author kris.wang
 */
public class EntityUtils {

    public static <From, Target> Target copyBean(From from, Supplier<Target> generateTarget,
                                                  Function<From, Target> customTransfer) {
        if (from == null) {
            return null;
        }
        if (generateTarget == null) {
            return null;
        }
        if (customTransfer == null) {
            Target target = generateTarget.get();
            BeanUtils.copyProperties(from, target);
            return target;
        } else {
            return customTransfer.apply(from);
        }
    }
}
