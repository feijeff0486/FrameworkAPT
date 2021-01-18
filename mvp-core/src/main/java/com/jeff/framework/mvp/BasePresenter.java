package com.jeff.framework.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 *
 * <p>
 * @author Jeff
 * @date 2019/5/20. 19:26
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class BasePresenter<V extends IBaseContract.IBaseView> implements IBaseContract.IBasePresenter<V> {

    private WeakReference<V> mViewRef;

    @Override
    public V getView() {
        if (isViewAttached()) {
            return mViewRef.get();
        }
        return null;
    }

    @Override
    public void attach(V view) {
        mViewRef=new WeakReference<>(view);
    }

    @Override
    public void onCreatePresenter(@Nullable Bundle savedState) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void detach() {
        if (mViewRef!=null){
            mViewRef.clear();
            mViewRef=null;
        }
    }

    protected void onPresenterCleared(){

    }

    protected boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }
}
