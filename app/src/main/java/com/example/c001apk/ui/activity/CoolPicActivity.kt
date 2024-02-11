package com.example.c001apk.ui.activity

import android.os.Bundle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.c001apk.R
import com.example.c001apk.databinding.FragmentTopicBinding
import com.example.c001apk.ui.fragment.CollectionFragment
import com.google.android.material.tabs.TabLayoutMediator

class CoolPicActivity : BaseActivity<FragmentTopicBinding>() {

    private var title: String? = null
    private val tabList = listOf("精选", "热门", "最新")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = intent.getStringExtra("title")

        initBar()
        initView()

    }

    private fun initBar() {
        binding.toolBar.apply {
            title = title
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun initView() {
        binding.viewPager.offscreenPageLimit = tabList.size
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int) =
                when (position) {
                    0 -> CollectionFragment.newInstance("recommend", title.toString())
                    1 -> CollectionFragment.newInstance("hot", title.toString())
                    2 -> CollectionFragment.newInstance("newest", title.toString())
                    else -> throw IllegalArgumentException()
                }

            override fun getItemCount() = tabList.size
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabList[position]
        }.attach()
    }

}