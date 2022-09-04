package io.doeasy.xcar.validator;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author kris.wang
 */
public class IntervalValidator implements ConstraintValidator<Interval, Integer> {

    private Interval interval;

    @Override
    public void initialize(Interval constraintAnnotation) {
        interval = constraintAnnotation;
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (null == value) {
            return false;
        }

        if (StringUtils.isBlank(interval.value())) {
            return true;
        }

        String[] split = interval.value().split(",");
        for (String item : split) {
            if (item.trim().equals(value.toString())) {
                return true;
            }
        }

        return false;
    }
}
