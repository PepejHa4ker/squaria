package com.pepej.squaria.utils

import java.util.function.ToDoubleFunction

class Vec3d @JvmOverloads constructor(val x: Double = 0.0, val y: Double = 0.0, val z: Double = 0.0) {
    constructor(mcVec: net.minecraft.util.math.Vec3d) : this(mcVec.x, mcVec.y, mcVec.z)

    fun setX(x: Double): Vec3d {
        return Vec3d(x, y, z)
    }

    fun setY(y: Double): Vec3d {
        return Vec3d(x, y, z)
    }

    fun setZ(z: Double): Vec3d {
        return Vec3d(x, y, z)
    }

    fun add(`val`: Double): Vec3d {
        return Vec3d(x + `val`, y + `val`, z + `val`)
    }

    fun add(vec: Vec3d): Vec3d {
        return Vec3d(x + vec.x, y + vec.y, z + vec.z)
    }

    fun add(x: Double, y: Double, z: Double): Vec3d {
        return Vec3d(this.x + x, this.y + y, this.z + z)
    }

    fun sub(`val`: Double): Vec3d {
        return Vec3d(x - `val`, y - `val`, z - `val`)
    }

    fun sub(vec: Vec3d): Vec3d {
        return Vec3d(x - vec.x, y - vec.y, z - vec.z)
    }

    fun sub(x: Double, y: Double, z: Double): Vec3d {
        return Vec3d(this.x - x, this.y - y, this.z - z)
    }

    fun multiply(`val`: Double): Vec3d {
        return Vec3d(x * `val`, y * `val`, z * `val`)
    }

    fun multiply(vec: Vec3d): Vec3d {
        return Vec3d(x * vec.x, y * vec.y, z * vec.z)
    }

    fun multiply(x: Double, y: Double, z: Double): Vec3d {
        return Vec3d(this.x * x, this.y * y, this.z * z)
    }

    fun divide(`val`: Double): Vec3d {
        return Vec3d(x / `val`, y / `val`, z / `val`)
    }

    fun divide(vec: Vec3d): Vec3d {
        return Vec3d(x / vec.x, y / vec.y, z / vec.z)
    }

    fun divide(x: Double, y: Double, z: Double): Vec3d {
        return Vec3d(this.x / x, this.y / y, this.z / z)
    }

    fun cross(other: Vec3d): Vec3d {
        return Vec3d(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
    }

    fun invert(): Vec3d {
        return Vec3d(-x, -y, -z)
    }

    fun normalize(): Vec3d {
        return this.divide(length())
    }

    fun abs(): Vec3d {
        return Vec3d(Math.abs(x), Math.abs(y), Math.abs(z))
    }

    fun dot(other: Vec3d): Double {
        return x * other.x + y * other.y + z * other.z
    }

    fun length(): Double {
        return Math.sqrt(x * x + y * y + z * z)
    }

    fun lengthSq(): Double {
        return x * x + y * y + z * z
    }

    override fun toString(): String {
        return "Vec3d[$x,$y,$z]"
    }

    override fun hashCode(): Int {
        var temp = java.lang.Double.doubleToLongBits(x)
        var result = (temp xor temp ushr 32).toInt()
        temp = java.lang.Double.doubleToLongBits(y)
        result = 31 * result + (temp xor temp ushr 32).toInt()
        temp = java.lang.Double.doubleToLongBits(z)
        result = 31 * result + (temp xor temp ushr 32).toInt()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        return if (obj !is Vec3d) {
            false
        } else {
            val o = obj
            o.x == x && o.y == y && o.z == z
        }
    }

    companion object {
        val GET_X = ToDoubleFunction { v: Vec3d -> v.x }
        val GET_Y = ToDoubleFunction { v: Vec3d -> v.y }
        val GET_Z = ToDoubleFunction { v: Vec3d -> v.z }
    }
}