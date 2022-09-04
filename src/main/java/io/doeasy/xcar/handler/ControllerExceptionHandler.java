package io.doeasy.xcar.handler;

import io.doeasy.xcar.util.CollectionUtils;
import io.doeasy.xcar.vo.base.BaseResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @description:
 * @author: kris.wang
 **/
@RestControllerAdvice
@Log4j2
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public BaseResponse<Void> handleException(HttpServletRequest req, Exception e) {
        log.error("[ExceptionHandler] request method: {}, request uri: {}, warn message: {}", req.getMethod(), req.getRequestURI(), e.getMessage(), e);
        if (e instanceof DataIntegrityViolationException) {
            return BaseResponse.failed(e.getCause().getMessage(), null);
        }
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException e1 = (MethodArgumentNotValidException) e;
            List<FieldError> fieldErrors = e1.getBindingResult().getFieldErrors();
            if (!org.springframework.util.CollectionUtils.isEmpty(fieldErrors)) {
                return BaseResponse.failed(CollectionUtils.mkString(fieldErrors, fieldError ->
                    fieldError.getField() + ":" + fieldError.getDefaultMessage(), ","), null);
            }
        }
        return BaseResponse.failed(e.getMessage(), null);
    }
}
