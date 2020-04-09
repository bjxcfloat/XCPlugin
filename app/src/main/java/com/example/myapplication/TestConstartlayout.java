package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.constraintlayout.widget.ConstraintLayout;

public class TestConstartlayout extends ConstraintLayout {
    public static  int count = 0;
    public TestConstartlayout(Context context) {
        super(context);
    }
    public TestConstartlayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        Log.e("asd","TestConstartlayout init");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {



        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                count++;
                Log.e("asd",(this.getClass().toString()+count)+"-dispatchTouchEvent-ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("asd",(this.getClass().toString()+count)+"-dispatchTouchEvent-ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("asd",(this.getClass().toString()+count)+"-dispatchTouchEvent-ACTION_UP");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.e("asd",(this.getClass().toString()+count)+"-dispatchTouchEvent-ACTION_CANCEL");
                break;
        }


        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:

                Log.e("asd",(this.getClass().toString()+count)+"-onInterceptTouchEvent-ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("asd",(this.getClass().toString()+count)+"-onInterceptTouchEvent-ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("asd",(this.getClass().toString()+count)+"-onInterceptTouchEvent-ACTION_UP");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.e("asd",(this.getClass().toString()+count)+"-onInterceptTouchEvent-ACTION_CANCEL");
                break;
        }


        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {


        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:

                Log.e("asd",(this.getClass().toString()+count)+"-onTouchEvent-ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("asd",(this.getClass().toString()+count)+"-onTouchEvent-ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("asd",(this.getClass().toString()+count)+"-onTouchEvent-ACTION_UP");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.e("asd",(this.getClass().toString()+count)+"-onTouchEvent-ACTION_CANCEL");
                break;
        }

        return super.onTouchEvent(ev);
    }
}
