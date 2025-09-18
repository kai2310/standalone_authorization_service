package com.rubicon.platform.authorization.service;

import com.rubicon.platform.authorization.service.exception.ErrorBean;
import com.rubicon.platform.authorization.service.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 */
@ControllerAdvice
public class ExceptionMapper
{
    /*
        This was added to fix an issue that has not been moved into the module. When that is done, we can remove this file
        and use the generic module
     */
    private static Logger
            logger =
            LoggerFactory.getLogger(com.rubicon.platform.authorization.service.ExceptionMapper.class);

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorBean> handleException(Throwable exception)
    {
        ErrorBean errorResponse = new ErrorBean();
        errorResponse.setStatusCode(500);
        errorResponse.setMessage(exception.getMessage());

        if (exception instanceof ServiceException)
        {
            ServiceException se = (ServiceException) exception;
            errorResponse.setStatusCode(se.getResponseCode());
        }
        else if (exception instanceof HttpRequestMethodNotSupportedException)
        {
            errorResponse.setStatusCode(405);
        }
        else if (exception instanceof HttpMediaTypeException)
        {
            errorResponse.setStatusCode(406);
        }
        else if (exception instanceof MissingServletRequestParameterException)
        {
            errorResponse.setStatusCode(400);
        }
        else if (exception instanceof TypeMismatchException)
        {
            errorResponse.setStatusCode(400);
        }
        else if (exception instanceof HttpMessageNotReadableException)
        {
            errorResponse.setStatusCode(400);
        }
        else if (exception instanceof NoHandlerFoundException)
        {
            errorResponse.setStatusCode(404);
        }

        if (errorResponse.getStatusCode() < 500)
        {
            logger.info(errorResponse.getMessage());
        }
        else
        {
            logger.error(errorResponse.getMessage(), exception);
        }

        // Do not return any response when the socket is closed
        if (StringUtils.containsIgnoreCase(exception.getClass().getSimpleName(), "ClientAbortException") ||
            (exception.getCause() != null &&
             StringUtils.containsIgnoreCase(exception.getCause().getClass().getSimpleName(), "ClientAbortException")))
        {
            return null;
        }


        return new ResponseEntity<ErrorBean>(errorResponse, HttpStatus.valueOf(errorResponse.getStatusCode()));
    }
}
