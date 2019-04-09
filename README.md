# XDslPagerAdapter

## 使用 XDslPagerAdapter 快速创建 ViewPager
在 Android 开发中经常需要使用 ViewPager ，大多数的功能类似但每次都需要写 Adapter 和一堆模板代码。  
目前我们很多项目都使用了 Kotlin 和 Databinding,为了简洁快速地构建 ViewPager，我使用 DSL 和 Databinding 的特性写了一个用于快速创建 PagerAdapter 的工具`XDslPagerAdapter`。
 
下面通过创建一个欢迎画面举例说明：

```
        binding.guideViewPager.run {
            xDslPagerAdapter {
                item(R.layout.pager_guide) {
                    model(BR.model to Page(0, R.drawable.img_tutorial01, "下一页"))
                    click(BR.click to { _: Page -> currentItem = 1 })
                    action { binding ->
                        (binding as? PagerGuideBinding)?.run {
                            (nextPageButton.layoutParams as? ViewGroup.MarginLayoutParams)?.run {
                                marginStart = dp2px(30F)
                                marginEnd = dp2px(30F)
                            }
                        }
                    }
                }
                item(R.layout.pager_guide) {
                    model(BR.model to Page(1, R.drawable.img_tutorial02, "下一页"))
                    click(BR.click to { _: Page -> currentItem = 2 })
                }
                item(R.layout.pager_guide) {
                    model(BR.model to Page(2, R.drawable.img_tutorial03, "下一页"))
                    click(BR.click to { _: Page -> currentItem = 3 })
                }
                item(R.layout.pager_guide) {
                    model(BR.model to Page(3, R.drawable.img_tutorial04, "开始使用"))
                    click(BR.click to { _: Page ->
                        SharedPrefModel.isFistTime = false
                        startActivity(Intent(context, MainActivity::class.java))
                        finish()
                    })
                }
            }
        }
```

1. 首先调用 ViewPager 的拓展方法`xDslPagerAdapter()`开始创建`PagerAdapter`
2. 在`PagerAdapter`内部使用 `item()`开始创建`Item`，其中`layout`是必须的参数。
3. 在`Item`内部可以使用的方法有`model()``click()``action`
	* `model()`传入包含 DataBinding 的 `BR.Id` 和对应的 `Model`（类型为你 ViewModel 的类型，用来绑定显示相关数据）
	* `click()`传入包含 DataBinding 的 `BR.Id` 和对应的 `Click`（类型为`Function<Any>`用来绑定点击事件），多个点击事件可多次调用。
	* `action()`可用来获取 Databinding 为 layout 的 ViewDataBinding 类,用来处理其它的内容，例如动态改变布局等。

源码地址 [https://github.com/xiejinpeng007/XDslPagerAdapter](https://github.com/xiejinpeng007/XDslPagerAdapter)
