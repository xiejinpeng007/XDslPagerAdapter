package xiejinpeng.xdslpageradapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


typealias OnClick = Function<Any>
typealias Handle = Pair<Int, OnClick>
typealias Model = Pair<Int, Any>
typealias Action = ((ViewDataBinding) -> Unit)

class XDslPagerAdapter : PagerAdapter() {

    private val items: MutableList<Item> = mutableListOf()

    lateinit var layoutInflater: LayoutInflater

    fun item(layoutId: Int, init: Item.() -> Unit) {
        val item = Item(layoutId)
        item.init()
        items.add(item)
    }

    fun create(viewPager: ViewPager) {
        layoutInflater = LayoutInflater.from(viewPager.context)
        viewPager.adapter = this
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return items[position].apply {

            binding?.let {
                container.addView(it.root)
            } ?: run {
                binding = DataBindingUtil.inflate(layoutInflater, layoutId, container, false)

                model?.run { binding?.setVariable(first, second) }

                handle.forEach {
                    binding?.setVariable(it.first, it.second)
                }

                binding?.let { mAction.invoke(it) }

                binding?.executePendingBindings()

                container.addView(binding?.root)
            }
        }
    }


    override fun isViewFromObject(view: View, `object`: Any) = view == (`object` as Item).binding?.root

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView((`object` as Item).binding?.root)
    }

    override fun getItemPosition(`object`: Any): Int {
        val pos = items.indexOf(`object` as Item)
        return if (pos == -1) POSITION_NONE
        else pos
    }

    override fun getCount() = items.size
}

class Item(val layoutId: Int,
           var model: Model? = null,
           var handle: MutableList<Handle> = mutableListOf(),
           var mAction: Action = { _ -> },
           var binding: ViewDataBinding? = null) {


    fun model(model: Model, init: (Model.() -> Unit)? = null): Model? {
        this.model = model
        init?.run { model.init() }
        return model
    }

    fun handle(handle: Handle, init: (Handle.() -> Unit?)? = null): Handle? {
        this.handle.add(handle)
        init?.run { handle.init() }
        return handle
    }

    //TODO Generic the Binding
    fun action(action: Action) {
        this.mAction = action
    }


}


fun ViewPager.xDslPagerAdapter(build: XDslPagerAdapter.() -> Unit) {
    XDslPagerAdapter().apply {
        build()
        create(this@xDslPagerAdapter)
    }
}
