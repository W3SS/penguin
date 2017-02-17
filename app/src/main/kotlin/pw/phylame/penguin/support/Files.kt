package pw.phylame.penguin.support

import android.app.Application
import android.content.Context
import java.io.File
import java.io.FileFilter
import java.util.*
import java.util.regex.Pattern

object PenguinFilter : FileFilter {
    var isHiddenShown = false
    var isDirShown = true
    var isFileShown = true
    var pattern: Pattern? = null

    override fun accept(file: File): Boolean {
        if (file.isHidden) {
            return isHiddenShown
        }
        if (!isDirShown && file.isDirectory) {
            return false
        }
        if (!isFileShown && file.isFile) {
            return false
        }
        return pattern?.matcher(file.name)?.matches() ?: true
    }
}

enum class ItemMode(val id: Int) {
    None(0), DirFirst(1), FileFirst(2)
}

enum class SortMode(val id: Int) {
    Name(0), Type(1), Size(2), SizeDesc(3), Date(4), DateDesc(5)
}

object PenguinSorter : Comparator<Item> {
    const val FRONTAL = -1
    const val POSTERIOR = 1

    var itemMode = ItemMode.DirFirst
    var dirMode = SortMode.Name
    var fileMode = SortMode.Name

    override fun compare(a: Item, b: Item): Int {
        val dirA = a.file.isDirectory
        val dirB = b.file.isDirectory

        when (itemMode) {
            ItemMode.DirFirst -> {
                if (dirA) {
                    if (!dirB) {
                        return FRONTAL
                    }
                } else if (dirB) {
                    return POSTERIOR
                }
            }
            ItemMode.FileFirst -> {
                if (!dirA) {
                    if (dirB) {
                        return FRONTAL
                    }
                } else if (!dirB) {
                    return POSTERIOR
                }
            }
        }

        if (dirA) { // a, b are dir

        } else { // a, b are file

        }

        return 0
    }
}