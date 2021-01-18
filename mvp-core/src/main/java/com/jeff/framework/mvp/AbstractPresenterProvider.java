package com.jeff.framework.mvp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * <p>
 *
 * @author Jeff
 * @date 2021/1/12
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public abstract class AbstractPresenterProvider {
    private String TAG = "P-Provider";
    private boolean logging=true;
    private PresenterStore mPresenterStore;
    private PresenterDispatcher mPresenterDispatcher;

    protected <V extends IBaseContract.IBaseView> AbstractPresenterProvider(@NonNull V target,
                                        @Nullable Bundle savedInstanceState) {
        doInit(target, savedInstanceState);
    }

    private <V extends IBaseContract.IBaseView> void doInit(V target, @Nullable Bundle savedInstanceState) {
        TAG=this.getClass().getSimpleName().replace("PresenterProvider","$P-Provider");
        log("doInit",">>>>>>");
        mPresenterStore = new PresenterStore(TAG.replace("PresenterProvider","$P-Store"));
        mPresenterDispatcher= new PresenterDispatcher(TAG.replace("PresenterProvider","$P-Dispatcher"),mPresenterStore);

        storePresenters();
        mPresenterDispatcher.attach(target);
        mPresenterDispatcher.onCreatePresenter(savedInstanceState);
    }

    protected abstract void storePresenters();

    public BasePresenter getPresenter(@NonNull String key) {
        return mPresenterStore.get(key);
    }

    public <V extends IBaseContract.IBaseView,P extends BasePresenter<V>> P getPresenter(@NonNull Class<P> cls) {
        log("getPresenter","cls= "+cls.getName());
        return (P) mPresenterStore.get(cls.getName());
    }

    public void onSaveInstanceState(Bundle outState) {
        log("onSaveInstanceState",outState.toString());
        if (mPresenterDispatcher != null) {
            mPresenterDispatcher.onSaveInstanceState(outState);
        }
    }

    public void detach() {
        log("detach","<<<<<<");
        if (mPresenterDispatcher != null) {
            mPresenterDispatcher.detach();
        }
        if (mPresenterStore != null) {
            mPresenterStore.clear();
        }
    }

    public PresenterStore getPresenterStore() {
        return mPresenterStore;
    }

    public PresenterDispatcher getPresenterDispatcher() {
        return mPresenterDispatcher;
    }

    public void setLogging(boolean logging){
        this.logging=logging;
    }

    void log(@NonNull String method,String content){
        if (!logging)return;
        Log.d(TAG, String.format("%s: %s",method, TextUtils.isEmpty(content)?"":content));
    }
}
