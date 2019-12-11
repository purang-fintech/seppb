package com.pr.sepp.common.exception.handler;

import com.pr.sepp.common.exception.SeppClientException;
import com.pr.sepp.common.exception.SeppException;
import com.pr.sepp.common.exception.SeppServerException;
import com.pr.sepp.common.threadlocal.GenericResponse;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class ControllerExceptionHandleAdvice {

    @ExceptionHandler
    public ResponseEntity<GenericResponse> handleException(Exception e) {
        if (e instanceof SeppException) {
            SeppException ex = (SeppException) e;
            GenericResponse genericResponse = GenericResponse.builder()
                    .code(ex.getCode())
                    .status(ex.getStatus())
                    .message(ex.getMessage())
                    .stackTrace(ex.getMessage())
                    .build();
            if (e instanceof SeppServerException) {
                log.error("[ServerError]: ", e);
            } else if (e instanceof SeppClientException) {
                log.warn("[ClientWarn]: {};用户ID:{}", e.getMessage(), ParameterThreadLocal.getUserId());
            }
            return new ResponseEntity<>(genericResponse, HttpStatus.valueOf(ex.getStatus()));
        }
        log.error("", e);
        return new ResponseEntity<>(GenericResponse.SERVER_ERROR.setMessage("后端服务出现异常，请查看日志").setStackTrace(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public GenericResponse valid(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        List<String> errorMessages = allErrors.stream().map(ObjectError::getDefaultMessage).collect(toList());
        String message = StringUtils.join(errorMessages, ";");
        return GenericResponse.CLIENT_ERROR.setMessage(message);
    }

}