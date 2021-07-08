package com.pepej.squaria.elements

import com.pepej.papi.text.Text.colorize
import com.pepej.squaria.utils.ByteMap
import java.util.*

class Table(id: String) : Element<Table>(id, "Table") {
    var columns = LinkedList<Column>()
    var rows = LinkedList<Array<String>>()
    var titleColor = -769226
    var title: String? = null
    var headingColor = -5317
    var drawBack = false
    var maxRows = -1
    var scrollbarColor = -5317
    var rowHoverColor = 1140946643
    fun addColumn(column: Column): Table {
        columns.add(column)
        return this
    }

    fun addRow(row: Array<String>): Table {
        for (i in row.indices) {
            row[i] = colorize(row[i])
        }
        rows.add(row)
        return this
    }

    fun setTitle(title: String?): Table {
        this.title = colorize(title)
        return this
    }

    fun setTitleColor(color: Int): Table {
        titleColor = color
        return this
    }

    fun setHeadingColor(color: Int): Table {
        headingColor = color
        return this
    }

    fun setDrawBack(drawBack: Boolean): Table {
        this.drawBack = drawBack
        return this
    }

    fun setMaxRows(maxRows: Int): Table {
        this.maxRows = maxRows
        return this
    }

    fun setScrollbarColor(color: Int): Table {
        scrollbarColor = color
        return this
    }

    fun setRowHoverColor(color: Int): Table {
        rowHoverColor = color
        return this
    }

    override fun write(map: ByteMap) {
        val cols = ByteMap()
        cols["size"] = columns.size
        var id = 0
        for (column in columns) {
            cols["$id.n"] = column.name
            cols["$id.w"] = column.width
            if (column.center) {
                cols["$id.c"] = true
            }
            if (column.color != -1) {
                cols["$id.t"] = column.color
            }
            ++id
        }
        map["cols"] = cols
        val rows = ByteMap()
        id = 0
        for (row in rows) {
            rows[id++.toString() + ""] = row
        }
        map["rows"] = rows
        if (title != null) {
            map["title"] = title
        }
        if (titleColor != -5317) {
            map["title.c"] = titleColor
        }
        if (headingColor != -5317) {
            map["heading.c"] = headingColor
        }
        if (drawBack) {
            map["drawBack"] = true
        }
        if (maxRows != -1) {
            map["maxRows"] = maxRows
        }
        if (scrollbarColor != -5317) {
            map["sb.c"] = scrollbarColor
        }
        if (rowHoverColor != 1140946643) {
            map["rh.c"] = rowHoverColor
        }
        super.write(map)
    }

    class Column(name: String, val width: Int) {
        var name: String
        var center = false
        var color = -1
        fun setCenter(flag: Boolean): Column {
            center = flag
            return this
        }

        fun setColor(color: Int): Column {
            this.color = color
            return this
        }

        init {
            this.name = colorize(name)
        }
    }
}