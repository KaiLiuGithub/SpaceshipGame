package com.kailiu.spaceship.cloud

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResourceCallAdapter: CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {

        if (getRawType(returnType) != LiveData::class.java) {
            return null
        }
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType = getRawType(observableType)
        if (rawObservableType != Resource::class.java) {
            return null
        }
        val bodyType = getParameterUpperBound(0, observableType as ParameterizedType)

        return object : CallAdapter<Any, LiveData<Resource<Any>>> {
            override fun adapt(call: Call<Any>): LiveData<Resource<Any>> {

                val liveResource = MutableLiveData<Resource<Any>>()
                liveResource.postValue(Resource.loading(null))

                call.enqueue(object : Callback<Any> {
                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        liveResource.postValue(Resource.error(t.message, null))
                    }

                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        if (response.isSuccessful) {
                            liveResource.postValue(Resource.success(response.body()))
                        } else {
                            liveResource.postValue(Resource.error(response.errorBody()?.string(), response.body(), response.code()))
                        }
                    }
                })
                return liveResource
            }

            override fun responseType(): Type {
                return bodyType
            }
        }
    }
}