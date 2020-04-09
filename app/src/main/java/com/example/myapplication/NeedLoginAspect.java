package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class NeedLoginAspect {

    @Pointcut("execution(" +//执行语句
            "@com.example.myapplication.JudgeLogin" +//注解筛选
            " * " + //类路径,*为任意路径
            "*" +   //方法名,*为任意方法名
            "(..)" +//方法参数,'..'为任意个任意类型参数
            ")" +
            " && " +//并集
            "@annotation(needLogin)"//注解筛选,这里主要用于下面方法的'NeedLogin'参数获取
    )
    public int pointcutNeedLogin(JudgeLogin needLogin) {

        return 0;
    }

    @Around("pointcutNeedLogin(needLogin)")
    public void aroundNeedLogin(ProceedingJoinPoint joinPoint, final JudgeLogin needLogin) throws Throwable {
        Log.e("xc", "aroundNeedLogin-start");
        if (MainActivity.isLogin) {
            //方法执行
            joinPoint.proceed();
        } else {
            final Context context = MainActivity.ctx;
            switch (needLogin.tipType()) {
                case JudgeLogin.SHOW_TOAST:
                    Toast.makeText(context, needLogin.tipToast(), Toast.LENGTH_SHORT).show();
                    break;
                case JudgeLogin.SHOW_DIALOG:
                    /*
                        判断context是否为Activity
                        如果不是 , 使用Dialog会crash
                     */
                    if (context instanceof Activity) {
                        new AlertDialog.Builder(context)
                                .setTitle("登录提示")
                                .setMessage(needLogin.tipDialog())
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton("前往登录", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        Intent intent = new Intent(context, needLogin.loginActivity());
//                                        context.startActivity(intent);
                                    }
                                }).show();
                    } else {
                        // 这里处理直接跳到登录界面
                        Toast.makeText(context, needLogin.tipToast(), Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(context, needLogin.loginActivity());
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
                    }
                    break;
                //无响应类型
                case JudgeLogin.NO_RESPONSE:
                default:
                    break;
            }
        }
        Log.e("xc", "aroundNeedLogin-end");
    }

}