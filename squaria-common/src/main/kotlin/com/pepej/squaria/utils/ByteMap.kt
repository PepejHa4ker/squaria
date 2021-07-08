package com.pepej.squaria.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

private const val TYPE_INT: Int = 1
private const val TYPE_BYTE: Int = 2
private const val TYPE_LONG: Int = 3
private const val TYPE_STRING: Int = 4
private const val TYPE_SHORT: Int = 5
private const val TYPE_FLOAT: Int = 6
private const val TYPE_DOUBLE: Int = 7
private const val TYPE_BOOLEAN: Int = 8
private const val TYPE_MAP: Int = 9
private const val TYPE_BYTE_ARRAY: Int = 10
private const val TYPE_STRING_ARRAY: Int = 11
private const val TYPE_MAP_ARRAY: Int = 12


@Suppress("UNCHECKED_CAST")
/**
 * Represents a class that serializes data from server to client into byte array.
 * For standard types (Byte, Shore, Int, Long, Boolean, Float, Double, String),
 * the first parameter is a byte, which denotes the data type (see constants above)
 * and then data with the corresponding type
 *
 * For non standard type (ByteMap, ByteArray, Array<String>, Array<ByteMap>,
 * the first parameter also is a byte, which denotes the data type (also see constants above),
 * then the second parameter is a Int which denotes a data length (For Arrays, array length)
 * and then a data to serialize
 */
class ByteMap(bytes: ByteArray? = null) : HashMap<String, Any?>() {

    init {

        if (bytes != null) {
            try {
                val data = DataInputStream(ByteArrayInputStream(bytes))
                while (data.available() > 0) {
                    val key = data.readUTF()
                    val arr: ByteArray
                    when (data.readByte().toInt()) {
                        TYPE_INT -> this[key] = data.readInt()
                        TYPE_BYTE -> this[key] = data.readByte()
                        TYPE_LONG -> this[key] = data.readLong()
                        TYPE_STRING -> this[key] = data.readUTF()
                        TYPE_SHORT -> this[key] = data.readShort()
                        TYPE_FLOAT -> this[key] = data.readFloat()
                        TYPE_DOUBLE -> this[key] = data.readDouble()
                        TYPE_BOOLEAN -> this[key] = data.readBoolean()
                        TYPE_MAP -> {
                            arr = ByteArray(data.readInt())
                            data.read(arr, 0, arr.size)
                            this[key] = ByteMap(arr)
                        }
                        TYPE_BYTE_ARRAY -> {
                            arr = ByteArray(data.readInt())
                            data.read(arr, 0, arr.size)
                            this[key] = arr
                        }
                        TYPE_STRING_ARRAY -> {
                            val array = arrayOfNulls<String>(data.readInt())
                            for (i in array.indices) {
                                array[i] = data.readUTF()
                            }
                            this[key] = array
                        }
                        TYPE_MAP_ARRAY -> {
                            val array = arrayOfNulls<ByteMap>(data.readInt())

                            for (i in array.indices) {
                                val mapBytes = ByteArray(data.readInt())
                                data.read(mapBytes, 0, mapBytes.size)
                                array[i] = ByteMap(mapBytes)
                            }
                            this[key] = array
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun toByteArray(): ByteArray {
        val baos = ByteArrayOutputStream(128)

        try {
            val out = DataOutputStream(baos)
            for ((key, value) in entries) {
                out.writeUTF(key)
                when (value) {
                    is Int -> {
                        out.writeByte(TYPE_INT)
                        out.writeInt(value)
                    }
                    is Float -> {
                        out.writeByte(TYPE_FLOAT)
                        out.writeFloat(value)
                    }
                    is Byte -> {
                        out.writeByte(TYPE_BYTE)
                        out.writeByte(value.toInt())
                    }
                    is Short -> {
                        out.writeByte(TYPE_SHORT)
                        out.writeShort(value.toInt())
                    }
                    is Long -> {

                        out.writeByte(TYPE_LONG)
                        out.writeLong(value)

                    }
                    is String -> {
                        out.writeByte(TYPE_STRING)
                        out.writeUTF(value)
                    }
                    is Double -> {
                        out.writeByte(TYPE_DOUBLE)
                        out.writeDouble(value)
                    }
                    is Boolean -> {
                        out.writeByte(TYPE_BOOLEAN)
                        out.writeBoolean(value)
                    }

                    is ByteMap -> {
                        out.writeByte(TYPE_MAP)
                        val bytes = value.toByteArray()
                        out.writeInt(bytes.size)
                        out.write(bytes)
                    }
                    is ByteArray -> {
                        out.writeByte(TYPE_BYTE_ARRAY)
                        out.writeInt(value.size)
                        out.write(value)
                    }
                    else -> {
                        when (value?.javaClass) {
                            Array<String>::class.java -> {  // :(
                                out.writeByte(TYPE_STRING_ARRAY)
                                val array = value as Array<String>
                                out.writeInt(array.size)
                                for (i in array.indices) {
                                    out.writeUTF(array[i])
                                }
                            }
                            Array<ByteMap>::class.java -> { // :(
                                out.writeByte(TYPE_MAP_ARRAY)
                                val array = value as Array<ByteMap>
                                out.writeInt(array.size)
                                for (i in array.indices) {
                                    val map = array[i]
                                    val serialized = map.toByteArray()
                                    out.writeInt(serialized.size)
                                    out.write(serialized)
                                }
                            } else -> {
                                throw IllegalStateException("Unknown value type ${value?.javaClass} for key '$key'")
                            }
                        }
                    }

                }
            }
            out.flush()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return baos.toByteArray()

    }


    fun getString(key: String): String = this[key] as String
    fun getString(key: String?, default: String): String {
        val value = this[key ?: return default] as String?
        return value ?: default
    }

    fun getByte(key: String): Byte = this[key] as Byte
    fun getByte(key: String?, default: Byte): Byte {
        val value = this[key ?: return default] as Byte?
        return value ?: default
    }

    fun getShort(key: String): Short = this[key] as Short
    fun getShort(key: String?, default: Short): Short {
        val value = this[key ?: return default] as Short?
        return value ?: default
    }

    fun getFloat(key: String): Float = this[key] as Float
    fun getFloat(key: String?, default: Float): Float {
        val value = this[key ?: return default] as Float?
        return value ?: default
    }

    fun getDouble(key: String): Double = this[key] as Double
    fun getDouble(key: String?, default: Double): Double {
        val value = this[key ?: return default] as Double?
        return value ?: default
    }

    fun getInt(key: String): Int = this[key] as Int
    fun getInt(key: String?, default: Int): Int {
        val value = this[key ?: return default] as Int?
        return value ?: default
    }

    fun getLong(key: String): Long = this[key] as Long
    fun getLong(key: String?, default: Long): Long {
        val value = this[key ?: return default] as Long?
        return value ?: default
    }

    fun getBoolean(key: String): Boolean = this[key] as Boolean
    fun getBoolean(key: String?, default: Boolean): Boolean {
        val value = this[key ?: return default] as Boolean?
        return value ?: default
    }

    fun getMap(key: String): ByteMap? = this[key] as ByteMap?
    fun getMap(key: String?, default: ByteMap): ByteMap {
        val value = this[key ?: return default] as ByteMap?
        return value ?: default
    }

    fun getByteArray(key: String): ByteArray = this[key] as ByteArray
    fun getByteArray(key: String?, default: ByteArray): ByteArray {
        val value = this[key ?: return default] as ByteArray?
        return value ?: default
    }

    fun getStringArray(key: String): Array<String> = this[key] as Array<String>
    fun getStringArray(key: String?, default: Array<String>): Array<String> {
        val value = this[key ?: return default] as Array<String>?
        return value ?: default
    }

    fun getMapArray(key: String): Array<ByteMap> = this[key] as Array<ByteMap>
    fun getMapArray(key: String?, default: Array<ByteMap>): Array<ByteMap> {
        val value = this[key ?: return default] as Array<ByteMap>?
        return value ?: default
    }


    override fun toString(): String {
        val i: Iterator<Map.Entry<String, Any?>> = entries.iterator()
        if (!i.hasNext()) {
            return "{}"
        } else {
            val sb = StringBuilder()
            sb.append('{')
            while (true) {
                val (key, value) = i.next()
                sb.append(key)
                sb.append('=')
                val `val`: String = when {
                    value === this -> {
                        "(this Map)"
                    }
                    value is ByteArray -> {
                        value.contentToString()
                    }
                    value is Array<*> -> {
                        (value as Array<Any>).contentToString()
                    }
                    else -> {
                        value.toString()
                    }
                }
                sb.append(`val`)
                if (!i.hasNext()) {
                    return sb.append('}').toString()
                }
                sb.append(',').append(' ')
            }
        }
    }
}