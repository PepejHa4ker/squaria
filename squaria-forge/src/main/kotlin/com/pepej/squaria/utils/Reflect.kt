package com.pepej.squaria.utils

import com.pepej.squaria.Squaria.Companion.LOG
import scala.annotation.meta.field
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object Reflect {
    val cache: MutableMap<String?, ClassData<*>?> = ConcurrentHashMap()

    inline fun <reified T : Any> construct(clazz: Class<T>, vararg args: Any?): T? {
        return try {
            getClass<T>(clazz)?.construct(*args)
        } catch (var3: Exception) {
//            error(var3, "Constructor error")
            null
        }
    }


    inline fun <reified T : Any> isConstructorExist(clazz: Class<T>, vararg args: Class<*>?): Boolean {
        return findConstructor(clazz, *args) != null
    }

    inline fun <reified T : Any> isMethodExist(clazz: Class<T>, method: String, vararg args: Class<*>?): Boolean {
        return findMethod(clazz, method, *args) != null
    }

    inline fun <reified T : Any> isFieldExist(clazz: Class<T>,field: String): Boolean {
        return findField(clazz, field) != null
    }

    inline fun <reified T : Any> findConstructor(clazz: Class<T>,vararg args: Class<*>?): Constructor<T>? {
        return try {
            getClass(clazz)?.findConstructor0(*args)
        } catch (var3: Exception) {
            null
        }
    }

    inline fun <reified T : Any> findMethod(clazz: Class<T>,method: String, vararg args: Class<*>?): Method? {
        return try {
            getClass(clazz)?.findMethod0(method, *args)
        } catch (var4: Exception) {
            null
        }
    }

    inline fun <reified T: Any> findField(clazz: Class<T>, field: String): Field? {
        return try {
            getClass(clazz)?.findField(field)
        } catch (var3: Exception) {
            null
        }
    }

    inline fun <reified T : Any> findFinalField(clazz: Class<T>, field: String): Field? {
        return try {
            getClass(clazz)?.findFinalField(field)
        } catch (var3: Exception) {
            null
        }
    }

    fun findClass(name: String?): Class<*>? {
        return try {
            Class.forName(name)
        } catch (var2: ClassNotFoundException) {
            null
        }
    }


    inline fun <reified T : Any> getClass(clazz: Class<T>): ClassData<T>? {
        var data: ClassData<T>? = cache[clazz.name] as ClassData<T>?
        if (data == null) {
            cache[clazz.name] = ClassData(clazz).also { data = it }
        }
        return data
    }

    private fun error(e: Throwable, message: String) {
       LOG.error(message, e)
    }

    private fun classesToString(classes: Array<Class<*>?>?): String {
        val iMax = classes!!.size - 1
        if (iMax == -1) {
            return "()"
        } else {
            val b = StringBuilder()
            b.append('(')
            var i = 0
            while (true) {
                b.append(classes[i]!!.name)
                if (i == iMax) {
                    return b.append(')').toString()
                }
                b.append(',')
                ++i
            }
        }
    }

    private class UnableToFindConstructorException(clazz: Class<*>, types: Array<out Class<*>>) :
        UnableToFindMethodException(clazz, null as String?, types) {
        override fun toString(): String {
           return ""
//            return "Unable to find constructor '" + super.className + ".<init>" + classesToString(super.types) + "'"
        }
    }

    private open class UnableToFindMethodException(
        clazz: Class<*>,
        protected var methodName: String?,
        types: Array<out Class<*>>,
    ) :
        RuntimeException() {
        protected var className: String
        protected var types: Array<out Class<*>>
        override val message: String
            get() = this.toString()

        override fun toString(): String {
            return ""
//            return "Unable to find method '" + className + "." + methodName + classesToString(types) + "'"
        }

        init {
            className = clazz.name
            this.types = types
        }
    }

    private class UnableToFindFieldException(clazz: Class<*>, private val fieldName: String) :
        RuntimeException() {
        private val className: String
        override val message: String
            get() = this.toString()

        override fun toString(): String {
            return "Unable to find field '" + fieldName + "' in class '" + className + "'"
        }

        init {
            className = clazz.name
        }
    }

    internal class AggressiveMethodMapKey(var name: String, var types: Array<Class<*>?>?) {
        override fun hashCode(): Int {
            var hash = name.hashCode()
            hash = 31 * hash + Arrays.hashCode(types)
            return hash
        }

        override fun equals(obj: Any?): Boolean {
            return if (obj !is AggressiveMethodMapKey) {
                false
            } else {
                val other = obj
                if (types!!.size == other.types!!.size && other.name == name) {
                    for (i in types!!.indices) {
                        if (types!![i] != other.types!![i]) {
                            return false
                        }
                    }
                    true
                } else {
                    false
                }
            }
        }
    }

    internal class MethodMapKey(var name: String, var args: Int) {
        override fun hashCode(): Int {
            return name.hashCode() + args
        }

        override fun equals(obj: Any?): Boolean {
            return if (obj !is MethodMapKey) {
                false
            } else {
                val other = obj
                other.args == args && other.name == name
            }
        }
    }

    internal class ConstructorMapKey(var types: Array<Class<*>>) {
        override fun hashCode(): Int {
            return Arrays.hashCode(types)
        }

        override fun equals(obj: Any?): Boolean {
            return if (obj !is AggressiveMethodMapKey) {
                false
            } else {
                val other = obj
                if (types.size != other.types!!.size) {
                    false
                } else {
                    for (i in types.indices) {
                        if (types[i] != other.types!![i]) {
                            return false
                        }
                    }
                    true
                }
            }
        }
    }

    class ClassData<K>(private val clazz: Class<K>) {
        private val fields: MutableMap<String?, Field?> = HashMap()
        val methods: MutableMap<Any?, Method?> = HashMap()
        private val constructors: MutableMap<Any?, Constructor<K>?> = HashMap()
        var aggressiveOverloading = false

        @Throws(java.lang.Exception::class)
        operator fun set(instance: Any?, field: String, value: Any?) {
            this.findField(field)[instance] = value
        }

        @Throws(java.lang.Exception::class)
        fun setFinal(instance: Any?, field: String, value: Any?) {
            this.findFinalField(field)[instance] = value
        }

        @Throws(java.lang.Exception::class)
        operator fun get(instance: Any?, field: String): Any {
            return this.findField(field)[instance]
        }

        @Throws(Throwable::class)
        operator fun invoke(instance: Any?, method: String, vararg args: Any?): Any {
            return this.findMethod(method, *args).invoke(instance, *args)
        }

        @Throws(java.lang.Exception::class)
        fun construct(vararg args: Any?): K {
            return this.findConstructor(*args).newInstance(*args)
        }

        fun findConstructor(vararg args: Any?): Constructor<K> {
            return findConstructor0(*toTypes(args as Array<Any?>))
        }

        fun findConstructor0(vararg types: Class<*>?): Constructor<K> {
            val mapped: Any = ConstructorMapKey(types as Array<Class<*>>)
            var con: Constructor<K>? = constructors[mapped]
            if (con == null) {
                val var4 = clazz.declaredConstructors
                val var5 = var4.size
                label37@ for (var6 in 0 until var5) {
                    val c = var4[var6]
                    val ptypes = c.parameterTypes
                    if (ptypes.size == types.size) {
                        for (i in ptypes.indices) {
                            if (types[i] != null && ptypes[i] != types[i] && !ptypes[i].isAssignableFrom(types[i])) {
                                continue@label37
                            }
                        }
                        con = c as Constructor<K>?
                        c.isAccessible = true
                        constructors[mapped] = c
                        break
                    }
                }
                if (con == null) {
                    throw UnableToFindConstructorException(clazz, types as Array<out Class<*>>)
                }
            }
            return con
        }

        fun findMethod(name: String, vararg args: Any?): Method {
            var types: Array<out Class<*>?>? = null
            val mapped: Any
            if (aggressiveOverloading) {
                types = toTypes(args as Array<Any?>)
                mapped = AggressiveMethodMapKey(name, types as Array<Class<*>?>?)
            } else {
                mapped = MethodMapKey(name, args.size)
            }
            var method = methods[mapped]
            if (method == null) {
                if (types == null) {
                    types = toTypes(args as Array<Any?>)
                }
                method = fastFindMethod(name, *types as Array<out Class<*>>)
                if (method == null) {
                    throw UnableToFindMethodException(clazz, name, types)
                }
                methods[mapped] = method
            }
            return method
        }

        fun findMethod0(name: String, vararg types: Class<*>?): Method {
            val mapped: Any
            mapped = if (aggressiveOverloading) {
                AggressiveMethodMapKey(name, types as Array<Class<*>?>?)
            } else {
                MethodMapKey(name, types.size)
            }
            var method = methods[mapped]
            if (method == null) {
                method = fastFindMethod(name, *types as Array<out Class<*>>)
                if (method == null) {
                    throw UnableToFindMethodException(clazz, name, types)
                }
                methods[mapped] = method
            }
            return method
        }

        private fun fastFindMethod(name: String, vararg types: Class<*>): Method? {
            var name = name
            var method: Method? = null
            name = name.intern()
            var clazz0: Class<*>? = clazz
            do {
                val var5 = clazz0!!.declaredMethods
                val var6 = var5.size
                label41@ for (var7 in 0 until var6) {
                    val m = var5[var7]
                    if (name == m.name) {
                        val ptypes = m.parameterTypes
                        if (ptypes.size == types.size) {
                            for (i in ptypes.indices) {
                                if (ptypes[i] != types[i] && !ptypes[i].isAssignableFrom(types[i])) {
                                    continue@label41
                                }
                            }
                            method = m
                            break
                        }
                    }
                }
                if (method != null) {
                    method.isAccessible = true
                    break
                }
                clazz0 = clazz0.superclass
            } while (clazz0 != null)
            return method
        }

        @Throws(Exception::class)
        fun findFinalField(name: String): Field {
            val field = this.findField(name)
            FIELD_MODIFIERS[field] = field.modifiers and -17
            return field
        }

        fun findField(name: String): Field {
            var field = fields[name]
            if (field == null) {
                var clazz0: Class<*>? = clazz
                while (clazz0 != null) {
                    try {
                        field = clazz0.getDeclaredField(name)
                        field.isAccessible = true
                        fields[name] = field
                        break
                    } catch (var5: java.lang.Exception) {
                        clazz0 = clazz0.superclass
                    }
                }
                if (field == null) {
                    throw UnableToFindFieldException(clazz, name)
                }
            }
            return field
        }

        private fun toTypes(objects: Array<Any?>): Array<out Class<*>?> {
            return if (objects.isEmpty()) {
                arrayOfNulls(0)
            } else {
                val types = arrayOfNulls<Class<*>?>(objects.size)
                for (i in objects.indices) {
                    if (objects[i] == null) {
                        types[i] = null
                    } else {
                        var type: Class<*> = objects[i]!!.javaClass
                        when (type) {
                            Int::class.java -> {
                                type = Integer.TYPE
                            }
                            Double::class.java -> {
                                type = java.lang.Double.TYPE
                            }
                            Boolean::class.java -> {
                                type = java.lang.Boolean.TYPE
                            }
                            Float::class.java -> {
                                type = java.lang.Float.TYPE
                            }
                            Long::class.java -> {
                                type = java.lang.Long.TYPE
                            }
                            Char::class.java -> {
                                type = Character.TYPE
                            }
                            Byte::class.java -> {
                                type = java.lang.Byte.TYPE
                            }
                            Short::class.java -> {
                                type = java.lang.Short.TYPE
                            }
                        }
                        types[i] = type
                    }
                }
                types
            }
        }

        companion object {
            private val FIELD_MODIFIERS: Field

            init {
                FIELD_MODIFIERS = Field::class.java.getDeclaredField("modifiers")
                FIELD_MODIFIERS.isAccessible = true
            }
        }
    }


}