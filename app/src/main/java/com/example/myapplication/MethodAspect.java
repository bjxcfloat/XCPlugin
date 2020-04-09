package com.example.myapplication;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class MethodAspect {
    @Pointcut("call(* com.example.myapplication.MainActivity.testAspect(..))")//②
    public void callMethod() {
    }

    @Before("callMethod()")//③
    public void beforeMethodCall(JoinPoint joinPoint) {
        Log.e("xc", "before->" + joinPoint.getTarget().toString()); //④
    }
}
