package com.jeff.framework.apt.demo;

import android.util.Log;

import com.jeff.framework.mvp.BasePresenter;

/**
 * <p>
 *
 * @author Jeff
 * @date 2021/1/8
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class BookPresenter extends BasePresenter<BookView> {
    private static final String TAG = "BookPresenter";
    public void bookTest(){
        if (getView()==null)return;
        Log.d(TAG, "bookTest: ");
        getView().bookTest();
    }
}
