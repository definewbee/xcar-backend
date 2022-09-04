package io.doeasy.xcar.controller;

import io.doeasy.xcar.util.MessageUtils;
import io.doeasy.xcar.vo.base.BaseResponse;

import java.util.List;


/**
 * @author kris.wang
 */
public class BaseController {
    public BaseResponse<Long> longSuccessResponseByNoEqZero(long result) {
        if (result == 0) {
            return BaseResponse.failed(MessageUtils.get("response.failure"), null);
        }
        return BaseResponse.success(result);
    }

    public BaseResponse<Void> voidSuccessResponseByEqOne(long result) {
        if (result != 1) {
            return BaseResponse.failed(MessageUtils.get("response.failure"), null);
        }
        return BaseResponse.success(null);
    }

    public <T> BaseResponse<T> objectSuccessResponseWhenDataNotEmpty(T data) {
        if (isEmpty(data)) {
            return BaseResponse.failed(MessageUtils.get("response.resourcenotfound"), null);
        } else {
            return BaseResponse.success(data);
        }
    }

    private boolean isEmpty(Object data) {
        if (null == data) {
            return true;
        }
        if (data instanceof List) {
            return ((List<?>) data).size() == 0;
        }
        return false;
    }
}
