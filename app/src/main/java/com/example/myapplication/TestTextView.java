package com.example.myapplication;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.RequiresApi;

public class TestTextView extends androidx.appcompat.widget.AppCompatTextView {
    public TestTextView(Context context) {
        super(context);
    }
    public TestTextView(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.textViewStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:

                Log.e("asd",this.getClass().toString()+"-dispatchTouchEvent-ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("asd",this.getClass().toString()+"-dispatchTouchEvent-ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("asd",this.getClass().toString()+"-dispatchTouchEvent-ACTION_UP");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.e("asd",this.getClass().toString()+"-dispatchTouchEvent-ACTION_CANCEL");
                break;
        }


        return super.dispatchTouchEvent(ev);
    }



    @Override
    public boolean onTouchEvent(MotionEvent ev) {


        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:

                Log.e("asd",this.getClass().toString()+"-onTouchEvent-ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("asd",this.getClass().toString()+"-onTouchEvent-ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("asd",this.getClass().toString()+"-onTouchEvent-ACTION_UP");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.e("asd",this.getClass().toString()+"-onTouchEvent-ACTION_CANCEL");
                break;
        }

        return super.onTouchEvent(ev);
    }

}
