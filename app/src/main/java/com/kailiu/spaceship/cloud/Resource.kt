package com.kailiu.spaceship.cloud

class Resource<T>(
    val status: Status,
    val data: T?,
    val message: String?,
    val httpResponseCode: Int? = 0
) {

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> success(msg: String?, data: T?, httpResponseCode: Int? = 0): Resource<T> {
            return Resource(Status.SUCCESS, data, msg, httpResponseCode)
        }


        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }

        fun <T> error(msg: String?, data: T?, httpResponseCode: Int? = 0): Resource<T> {
            return Resource(Status.ERROR, data, msg, httpResponseCode)
        }
    }


    override fun toString(): String {
        return ""
    }

    enum class Status {
        SUCCESS, LOADING, ERROR
    }
}