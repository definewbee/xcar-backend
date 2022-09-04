package io.doeasy.xcar.vo.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.doeasy.xcar.util.MessageUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

import static org.bouncycastle.asn1.cmc.CMCStatus.failed;

/**
 * @author kris.wang
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = -5229330081398026892L;

    /**
     * 返回码，成功返回200
     */
    private int code;

    /**
     * 描述信息，成功返回OK，失败返回失败信息
     */
    private String msg;

    /**
     * 返回详情数据
     */
    private T data;


    public BaseResponse(boolean success, String msg, T data) {
        if (success) {
            // 业务code，0 为成功
            this.code = 0;
        } else {
            this.code = 1;
        }
        this.msg = msg;
        this.data = data;
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, MessageUtils.get("response.success"), data);
    }

    public static <T> BaseResponse<T> successWhenDataNotEmpty(T data) {
        if (isEmpty(data)) {
            return failed(MessageUtils.get("response.datanotfound"), data);
        } else {
            return new BaseResponse<>(true, "success", data);
        }
    }

    private static boolean isEmpty(Object data) {
        if (null == data) {
            return true;
        }
        if (data instanceof List) {
            return ((List) data).size() == 0;
        }
        return false;
    }


    public static <T> BaseResponse<T> failed(String message, T data) {
        return new BaseResponse<>(false, message, data);
    }
}
