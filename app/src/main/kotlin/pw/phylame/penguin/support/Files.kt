package pw.phylame.penguin.support

import java.io.File
import java.io.FileFilter
import java.util.*
import java.util.regex.Pattern

fun readableSize(size: Long): String = if (size < 0x400) {
    "$size B"
} else if (size < 0x100000) {
    String.format("%.2f KB", size / 1024.0)
} else if (size < 0x40000000) {
    String.format("%.2f MB", size.toDouble() / 1024.0 / 1024.0)
} else {
    String.format("%.2f GB", size.toDouble() / 1024.0 / 1024.0 / 1024.0)
}

object PenguinFilter : FileFilter {
    var isHiddenShown = false
    var isDirShown = true
    var isFileShown = true
    var pattern: Pattern? = null

    override fun accept(file: File): Boolean {
        if (file.isHidden && !isHiddenShown) {
            return false
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

    var isHiddenFirst = true
    var isIgnoreCase = true
    var itemMode = ItemMode.DirFirst
    var dirMode = arrayOf(SortMode.Name)
    var fileMode = arrayOf(SortMode.Name)

    override fun compare(a: Item, b: Item): Int {
        val fileA = a.file
        val fileB = b.file
        val dirA = fileA.isDirectory
        val dirB = fileB.isDirectory

        var order: Int

        when (itemMode) {
            ItemMode.DirFirst -> {
                order = sortCondition(dirA, dirB)
                if (order != 0) {
                    return order
                }
            }
            ItemMode.FileFirst -> {
                order = sortCondition(!dirA, !dirB)
                if (order != 0) {
                    return order
                }
            }
        }

        if (isHiddenFirst) {
            order = sortCondition(fileA.isHidden, fileB.isHidden)
            if (order != 0) {
                return order
            }
        } else {
            order = sortCondition(!fileA.isHidden, !fileB.isHidden)
            if (order != 0) {
                return order
            }
        }

        if (dirA) { // a, b are dir
            for (mode in dirMode) {
                order = when (mode) {
                    SortMode.Name -> fileA.name.compareTo(fileB.name, isIgnoreCase)
                    SortMode.Size -> sortNumber(a.count, b.count, true)
                    SortMode.SizeDesc -> sortNumber(a.count, b.count, false)
                    SortMode.Date -> sortNumber(fileA.lastModified(), fileB.lastModified(), true)
                    SortMode.DateDesc -> sortNumber(fileA.lastModified(), fileB.lastModified(), false)
                    else -> 0
                }
                if (order != 0) {
                    return order
                }
            }
            TODO("bug occurred here")
        } else { // a, b are file
            for (mode in fileMode) {
                order = when (mode) {
                    SortMode.Name -> fileA.name.compareTo(fileB.name, isIgnoreCase)
                    SortMode.Size -> sortNumber(fileA.length(), fileB.length(), true)
                    SortMode.SizeDesc -> sortNumber(fileA.length(), fileB.length(), false)
                    SortMode.Date -> sortNumber(fileA.lastModified(), fileB.lastModified(), true)
                    SortMode.DateDesc -> sortNumber(fileA.lastModified(), fileB.lastModified(), false)
                    else -> 0
                }
                if (order != 0) {
                    return order
                }
            }
            TODO("bug occurred here")
        }
    }

    private fun sortCondition(a: Boolean, b: Boolean): Int {
        if (a) {
            if (!b) {
                return FRONTAL
            }
        } else if (b) {
            return POSTERIOR
        }
        return 0
    }

    private fun sortNumber(a: Int, b: Int, asc: Boolean): Int {
        val order = if (a < b) {
            FRONTAL
        } else {
            POSTERIOR
        }
        return if (asc) order else -order
    }

    private fun sortNumber(a: Long, b: Long, asc: Boolean): Int {
        val order = if (a < b) {
            FRONTAL
        } else {
            POSTERIOR
        }
        return if (asc) order else -order
    }
}
