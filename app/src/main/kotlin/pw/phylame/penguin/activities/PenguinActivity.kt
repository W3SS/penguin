package pw.phylame.penguin.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.os.EnvironmentCompat
import android.support.v4.view.ViewPager
import android.view.Menu
import pw.phylame.penguin.R
import pw.phylame.penguin.fragments.CategoryFragment
import pw.phylame.penguin.fragments.DirectoryFragment
import pw.phylame.penguin.support.BaseActivity
import pw.phylame.penguin.support.get
import java.util.*

class PenguinActivity : BaseActivity() {

    private val adapter by lazy { PagerAdapter(this, supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_penguin)
        setSupportActionBar(this[R.id.toolbar])

        val pager: ViewPager = this[R.id.pager]
        pager.adapter = adapter
        val tabs: TabLayout = this[R.id.tabs]
        tabs.setupWithViewPager(pager)
    }

    override fun onStart() {
        super.onStart()
        val devices = ArrayList<Device>()

        devices.add(Device(Environment.getExternalStorageDirectory().path, getString(R.string.device_phone)))

        adapter.devices = devices
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_penguin, menu)
        return super.onCreateOptionsMenu(menu)
    }

    class PagerAdapter(val ctx: Context, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        var devices = emptyList<Device>()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun getCount(): Int = devices.size + 1

        override fun getPageTitle(position: Int): CharSequence = when (position) {
            0 -> ctx.getString(R.string.tab_category_title)
            else -> devices[position - 1].name
        }

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> CategoryFragment()
            else -> {
                val device = devices[position - 1]
                DirectoryFragment.newInstance(device.path, device.name)
            }
        }
    }
}

class Device(val path: String, val name: String)