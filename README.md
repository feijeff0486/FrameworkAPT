# FrameworkAPT
通过注解自动生成代码，目前支持生成MVP，MVVM常用代码的生成
## 使用说明
1.在项目根目录的build.gradle中添加代码仓库
```groovy
   maven { url 'https://dl.bintray.com/feijeff0486/JFramework/' }
```
2.在需要使用到APT机制的module中的build.gradle中添加架包依赖
```groovy
   implementation 'com.jeff.framework:mvp-core:0.0.1'
   implementation 'com.jeff.framework:framework-annotation:0.0.2'
   annotationProcessor 'com.jeff.framework:framework-annotationprocessor:0.0.2'
```
3.在代码中使用注解
```java
@PresenterGenerator({MainPresenter.class, BookPresenter.class})
public class MainActivity extends AbstractCompatActivity implements MainView, BookView {
    private static final String TAG = "MainActivity";
    private MainActivityPresenterProvider mainActivityPresenterProvider;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        super.init(savedInstanceState);
        mainActivityPresenterProvider = MainActivityPresenterProvider.attach(this, savedInstanceState);
        mainActivityPresenterProvider.setLogging(true);
    }

    @Override
    protected void initView() {
        findViewById(R.id.btn_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: mainTest");
                mainActivityPresenterProvider.getPresenter(MainPresenter.class).mainTest();
            }
        });
        findViewById(R.id.btn_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: bookTest");
                mainActivityPresenterProvider.getBookPresenter().bookTest();
            }
        });
    }

    @Override
    protected void handleExtraParams(int from) {

    }

    @Override
    public void mainTest() {
        Log.d(TAG, "mainTest: ");
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void bookTest() {
        Log.d(TAG, "bookTest: ");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mainActivityPresenterProvider.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        mainActivityPresenterProvider.detach();
        super.onDestroy();
    }
}
```

build后生成的MainActivityPresenterProvider类
```java
public class MainActivityPresenterProvider extends AbstractPresenterProvider {
  private MainPresenter mMainPresenter;

  private BookPresenter mBookPresenter;

  protected MainActivityPresenterProvider(@NonNull MainActivity target,
      @Nullable Bundle savedInstanceState) {
    super(target, savedInstanceState);
  }

  public MainPresenter getMainPresenter() {
    return mMainPresenter;
  }

  public BookPresenter getBookPresenter() {
    return mBookPresenter;
  }

  @Override
  public void storePresenters() {
    mMainPresenter = new MainPresenter();
    getPresenterStore().put("com.jeff.framework.apt.demo.MainPresenter",mMainPresenter);
    mBookPresenter = new BookPresenter();
    getPresenterStore().put("com.jeff.framework.apt.demo.BookPresenter",mBookPresenter);
  }

  public static MainActivityPresenterProvider attach(@NonNull MainActivity target,
      @Nullable Bundle savedInstanceState) {
    return new MainActivityPresenterProvider(target,savedInstanceState);
  }

  @Override
  public void detach() {
    super.detach();
    mMainPresenter=null;
    mBookPresenter=null;
  }
}
```