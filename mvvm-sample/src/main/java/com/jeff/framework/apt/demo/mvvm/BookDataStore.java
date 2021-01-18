package com.jeff.framework.apt.demo.mvvm;

import android.databinding.ObservableField;

/**
 * <p>
 *
 * @author Jeff
 * @date 2021/1/7
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class BookDataStore {
    private static final String TAG = "BookDataStore";
    public ObservableField<Book> book=new ObservableField<Book>();

    public void notifyBook(){
        book.set(new Book("第一行MMVM",(int)(Math.random()*50) + 50));
    }

}
