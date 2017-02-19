package pw.phylame.penguin.support

import android.app.Activity
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.tbruyelle.rxpermissions.RxPermissions

@Suppress("UNCHECKED_CAST")
operator fun <T : View> Activity.get(@IdRes id: Int): T = findViewById(id) as T

fun <T : View> Activity.lazyView(@IdRes id: Int): Lazy<T> = lazy {
    @Suppress("UNCHECKED_CAST")
    findViewById(id) as T
}

abstract class BaseActivity : AppCompatActivity() {
    val permissions by lazy { RxPermissions(this) }
}

