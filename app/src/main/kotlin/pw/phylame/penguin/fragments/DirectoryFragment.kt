package pw.phylame.penguin.fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import pw.phylame.penguin.R
import pw.phylame.penguin.support.*
import java.io.File
import java.util.*

class DirectoryFragment : Fragment() {
    companion object {
        const val PATH_KEY = "path"
        const val PATH_NAME_KEY = "path_name"

        init {
            PenguinSorter
        }

        fun newInstance(path: String, name: String): DirectoryFragment {
            val fragment = DirectoryFragment()
            val args = Bundle()
            args.putString(PATH_KEY, path)
            args.putString(PATH_NAME_KEY, name)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var top: File

    private lateinit var item: Item

    private val adapter by lazy { Adapter(context) }

    private val infoBar: View by lazyView(R.id.info_bar)

    private val progressRefresh: ProgressBar by lazyView(R.id.progress_refresh)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_directory, container, false)

        val recycler: RecyclerView = view[R.id.recycler]
        recycler.adapter = adapter
        recycler.itemAnimator = DefaultItemAnimator()
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val path = arguments.getString(PATH_KEY)
        val name = arguments.getString(PATH_NAME_KEY)

        top = File(path)
        item = Item(top)
    }

    override fun onStart() {
        super.onStart()
        refreshItems()
    }

    fun refreshItems() {
        RefreshTask(item).execute()
    }

    inner class RefreshTask(val item: Item) : AsyncTask<Unit, Unit, List<Item>>() {
        var isProgressShown = false

        override fun doInBackground(vararg params: Unit): List<Item> {
            val items = ArrayList<Item>()

            val names = item.file.list() ?: return items
            names.map { File(item.file, it) }
                    .filter { PenguinFilter.accept(it) }
                    .sortedWith(PenguinSorter)
                    .mapTo(items) {
                        val item = Item(it)
                        item.count = item.file.list()?.size ?: 0
                        item
                    }
            item.count = names.size
            return items
        }

        override fun onPreExecute() {
            if (item.count < 0 || item.count > context.resources.getInteger(R.integer.show_progress_limit)) {
                isProgressShown = true
                progressRefresh.showAnimated(true)
            }
        }

        override fun onPostExecute(items: List<Item>) {
            adapter.items = items
            if (item.file == top) {
                infoBar.visibility = View.VISIBLE
            } else {
                infoBar.visibility = View.GONE
            }
            if (isProgressShown) {
                progressRefresh.showAnimated(false)
            }
        }
    }
}

class Holder(view: View) : RecyclerView.ViewHolder(view) {
    val icon: ImageView = view[R.id.icon]
    val name: TextView = view[R.id.name]
    val info: TextView = view[R.id.info]
    val option: ImageView = view[R.id.option]

    fun bindData(item: Item, isSelectionMode: Boolean) {
        val file = item.file
        name.text = file.name
        if (file.isDirectory) {
            icon.setImageResource(if (item.count <= 0) R.mipmap.ic_folder_empty else R.mipmap.ic_folder)
            option.visibility = View.VISIBLE
            if (isSelectionMode) {
                option.setImageResource(if (item.isSelected) android.R.drawable.checkbox_on_background else android.R.drawable.checkbox_off_background)
            } else {
                option.setImageResource(R.mipmap.ic_arrow)
            }
        } else {
            icon.setImageResource(R.mipmap.ic_file)
            if (isSelectionMode) {
                option.visibility = View.VISIBLE
                option.setImageResource(if (item.isSelected) android.R.drawable.checkbox_on_background else android.R.drawable.checkbox_off_background)
            } else {
                option.visibility = View.GONE
            }
        }
    }
}

class Adapter(ctx: Context) : RecyclerView.Adapter<Holder>() {
    val inflater: LayoutInflater = LayoutInflater.from(ctx)

    var isSelectionMode = false

    var items = emptyList<Item>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindData(items[position], isSelectionMode)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
        return Holder(inflater.inflate(R.layout.file_item, parent, false))
    }

}