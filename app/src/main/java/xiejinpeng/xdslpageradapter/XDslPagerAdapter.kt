package xiejinpeng.xdslpageradapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.contains

@DslMarker
annotation class XPagerAdapterDsL

typealias OnClick = Function<Any>
typealias Click = Pair<Int, OnClick>
typealias Model = Pair<Int, Any>
typealias Action = ((ViewDataBinding) -> Unit)

@XPagerAdapterDsL
class XDslPagerAdapter : PagerAdapter() {

    private val items: MutableList<Item> = mutableListOf()

    private lateinit var layoutInflater: LayoutInflater

    fun create(viewPager: ViewPager) {
        layoutInflater = LayoutInflater.from(viewPager.context)
        viewPager.adapter = this
    }

    fun item(layoutId: Int, init: Item.() -> Unit) {
        val item = Item(layoutId)
        item.init()
        items.add(item)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return items[position].apply {

            binding?.let {
                if (!container.contains(it.root))
                    container.addView(it.root)
            } ?: run {
                binding = DataBindingUtil.inflate(
                    this@XDslPagerAdapter.layoutInflater,
                    layoutId, container, false
                )

                model?.run { binding?.setVariable(first, second) }
                clicks.forEach { binding?.setVariable(it.first, it.second) }
                binding?.let { action?.invoke(it) }
                binding?.executePendingBindings()
                container.addView(binding?.root)
            }
        }
    }


    override fun isViewFromObject(view: View, `object`: Any) =
        view == (`object` as Item).binding?.root

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView((`object` as Item).binding?.root)
    }

    override fun getItemPosition(`object`: Any): Int {
        val pos = items.indexOf(`object` as Item)
        return if (pos == -1) POSITION_NONE else pos
    }

    override fun getCount() = items.size
}

@XPagerAdapterDsL
class Item(
    val layoutId: Int,
    var model: Model? = null,
    var clicks: MutableList<Click> = mutableListOf(),
    var action: Action? = null,
    var binding: ViewDataBinding? = null
) {

    fun model(model: Model, init: (Model.() -> Unit)? = null): Model? {
        this.model = model
        init?.run { model.init() }
        return model
    }

    fun click(click: Click, init: (Click.() -> Unit?)? = null): Click? {
        this.clicks.add(click)
        init?.run { click.init() }
        return click
    }

    //TODO Generic the Binding
    fun action(action: Action) {
        this.action = action
    }


}


fun ViewPager.xDslPagerAdapter(build: XDslPagerAdapter.() -> Unit) {
    XDslPagerAdapter().apply {
        build()
        create(this@xDslPagerAdapter)
    }
}
