package com.suerte.lostandfound.controller;

import com.suerte.lostandfound.exception.CustomException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Author: Demon
 * @Date: 2022/2/4
 * @Description:
 */
@ControllerAdvice
public class CustomErrorHandler {


    @ExceptionHandler(CustomException.class)
    public ModelAndView testException(Exception e){
//        System.out.println(e);
        return new ModelAndView("404");
    }
}
