package com.pepej.squaria.world

import java.util.*

class Element3DList {
    private val map: MutableMap<String?, Element3D?> = HashMap()
    private val set: MutableSet<Element3D> = LinkedHashSet()
    var cachedSortedVisible = arrayOfNulls<Element3D>(0)
        private set
    private var changed = false
    private var lastDistanceValidation: Long = 0
    fun add(elem: Element3D) {
        if (map.put(elem.id, elem) != null) {
            this.remove(elem.id)
        }
        set.add(elem)
        changed = true
    }

    fun remove0(elem: Element3D) {
        map.remove(elem.id, elem)
        set.remove(elem)
        changed = true
    }

    fun remove(id: String?) {
        for (elem in set) {
            if (elem.id == id) {
                elem.remove()
            }
        }
    }

    fun removeStartsWith(str: String) {
        for (elem in set) {
            if (elem.id.startsWith(str)) {
                elem.remove()
            }
        }
    }

    fun clear() {
        for (elem in set) {
            elem.remove()
        }
    }

    fun clearNow() {
        for (elem in set) {
            elem.dispose()
        }
        set.clear()
        map.clear()
        cachedSortedVisible = arrayOfNulls(0)
    }

    operator fun get(id: String?): Element3D? {
        return map[id]
    }

    val isEmpty = set.isEmpty()

    fun getSortedVisible(time: Long): Array<Element3D?> {
        if (!changed && time - lastDistanceValidation <= 1000L) {
            for (element in cachedSortedVisible) {
                element?.calcDistanceSquaredToPlayer()
            }
        } else {
            changed = false
            lastDistanceValidation = time
            val visible: MutableList<Element3D> = ArrayList(10.coerceAtLeast(cachedSortedVisible.size + 5))
            val removed: MutableList<Element3D> = ArrayList()
            for (elem in set) {
                elem.calcDistanceSquaredToPlayer()
                if (elem.distanceSquaredToPlayer < elem.renderDistanceSquared.coerceAtLeast(10000.0f)) {
                    visible.add(elem)
                } else if (elem.finishTime < time) {
                    removed.add(elem)
                }
            }
            for (elem in removed) {
                elem.dispose()
                remove0(elem)
            }
            cachedSortedVisible = visible.toTypedArray()
        }
        cachedSortedVisible.sortedBy { it?.distanceSquaredToPlayer }
        return cachedSortedVisible
    }

    val all: Collection<Element3D?>
        get() = set
}