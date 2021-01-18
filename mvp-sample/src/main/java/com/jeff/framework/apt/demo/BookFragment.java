package com.jeff.framework.apt.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.jeff.framework.apt.annotation.PresenterGenerator;
import com.jeff.framework.core.AbstractV4Fragment;

/**
 * <p>
 *
 * @author Jeff
 * @date 2021/1/12
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
@PresenterGenerator({BookPresenter.class})
public class BookFragment extends AbstractV4Fragment implements BookView{
    private static final String TAG = "BookFragment";
    private BookFragmentPresenterProvider bookFragmentPresenterProvider;

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        super.init(savedInstanceState);
        bookFragmentPresenterProvider=BookFragmentPresenterProvider.attach(this,savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_book;
    }

    @Override
    protected void initView() {
        findViewById(R.id.btn_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: bookTest");
                bookFragmentPresenterProvider.getPresenter(BookPresenter.class).bookTest();
            }
        });
    }

    @Override
    public void bookTest() {
        Log.d(TAG, "bookTest: ");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        bookFragmentPresenterProvider.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        bookFragmentPresenterProvider.detach();
        super.onDestroy();
    }
}
