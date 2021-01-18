package com.jeff.framework.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 *
 * <p>
 * @author Jeff
 * @date 2019/5/20 19:26
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public interface IBaseContract {

    interface IBaseView{
        Context getContext();
    }

    interface IBasePresenter<V>{
        V getView();

        void attach(V view);

        void onCreatePresenter(@Nullable Bundle savedState);

        void onSaveInstanceState(Bundle outState);

        void detach();
    }
}
