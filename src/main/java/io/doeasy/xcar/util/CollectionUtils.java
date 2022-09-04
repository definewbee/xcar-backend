package io.doeasy.xcar.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.util.Collection;
import java.util.function.Function;

/**
 * @description:
 * @author: kris.wang
 * @date: 2022/8/15
 **/
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionUtils {

    public static <T> String mkString(@NonNull Collection<T> list, @NonNull Function<T, String> stringify, @NonNull String delimiter) {
        int i = 0;
        StringBuilder builder = new StringBuilder();
        for (T t : list) {
            if (i != 0) {
                builder.append(delimiter);
            }
            builder.append(stringify.apply(t));
            i++;
        }
        return builder.toString();
    }

}
