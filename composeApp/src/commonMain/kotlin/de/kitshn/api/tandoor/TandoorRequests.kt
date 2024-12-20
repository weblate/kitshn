package de.kitshn.api.tandoor

import android.graphics.Bitmap
import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import de.kitshn.redactForRelease
import de.kitshn.volley.VolleyInputStreamRequest
import de.kitshn.volley.VolleyMultipartRequest
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TandoorRequestsError(
    val volleyError: VolleyError?,
    private val method: Int,
    val url: String
) : Throwable() {
    override fun getLocalizedMessage(): String {
        val message = volleyError?.networkResponse?.data?.decodeToString()

        return "at $url ($method) / $message / " + volleyError?.localizedMessage + " / TandoorRequestsError / " + volleyError?.stackTraceToString()
    }
}

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.req(
    endpoint: String,
    method: Int,
    data: JSONObject? = null
) = suspendCoroutine { cont ->
    val queue = Volley.newRequestQueue(context)

    val token = this.credentials.token
    val cookie = this.credentials.cookie

    Log.d(
        "TandoorRequests",
        "Method: $method, URL: ${credentials.instanceUrl.redactForRelease()}/api${endpoint}"
    )
    val url = "${credentials.instanceUrl}/api${endpoint}"

    val request = object : StringRequest(
        method,
        url,
        { response: String ->
            cont.resume(response)
        },
        { error: VolleyError? ->
            Log.e("TandoorRequests", "Exception for request \"$endpoint\" (delete)")
            error?.printStackTrace()
            cont.resumeWithException(
                TandoorRequestsError(
                    error,
                    method,
                    "${credentials.instanceUrl.redactForRelease()}/api${endpoint}"
                )
            )
        }
    ) {
        override fun getBody(): ByteArray {
            if(data != null) return data.toString().encodeToByteArray()
            return super.getBody()
        }

        override fun getBodyContentType(): String {
            if(data != null) return "application/json"
            return super.getBodyContentType()
        }

        override fun getHeaders(): MutableMap<String, String> {
            val headers = super.getHeaders().toMutableMap()
            if(token != null) headers["Authorization"] = "Bearer ${token.token}"
            if(cookie != null) {
                val csrfToken = cookie.replace(Regex(".*csrftoken="), "").replace(Regex(";.*"), "")
                headers["X-CSRFTOKEN"] = csrfToken
                headers["Cookie"] = cookie
            }
            headers["Referer"] = credentials.instanceUrl
            return headers
        }
    }

    queue.add(request)
}

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.reqArray(
    endpoint: String,
    method: Int,
    data: JSONArray? = null
) = suspendCoroutine { cont ->
    val queue = Volley.newRequestQueue(context)

    val token = this.credentials.token
    val cookie = this.credentials.cookie

    Log.d(
        "TandoorRequests",
        "Method: $method, URL: ${credentials.instanceUrl.redactForRelease()}/api${endpoint}"
    )
    val url = "${credentials.instanceUrl}/api${endpoint}"

    val jsonArrayRequest = object : JsonArrayRequest(
        method,
        url,
        data,
        { response: JSONArray? ->
            cont.resume(response)
        },
        { error: VolleyError? ->
            Log.e("TandoorRequests", "Exception for request \"$endpoint\" ($method) (jsonArray)")
            error?.printStackTrace()
            cont.resumeWithException(
                TandoorRequestsError(
                    error,
                    method,
                    "${credentials.instanceUrl.redactForRelease()}/api${endpoint}"
                )
            )
        }
    ) {
        override fun getHeaders(): MutableMap<String, String> {
            val headers = super.getHeaders().toMutableMap()
            if(token != null) headers["Authorization"] = "Bearer ${token.token}"
            if(cookie != null) {
                val csrfToken = cookie.replace(Regex(".*csrftoken="), "").replace(Regex(";.*"), "")
                headers["X-CSRFTOKEN"] = csrfToken
                headers["Cookie"] = cookie
            }
            headers["Referer"] = credentials.instanceUrl
            return headers
        }
    }

    queue.add(jsonArrayRequest)
}

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.reqObject(
    endpoint: String,
    method: Int,
    data: JSONObject? = null
) = suspendCoroutine { cont ->
    val queue = Volley.newRequestQueue(context)

    val token = this.credentials.token
    val cookie = this.credentials.cookie

    Log.d(
        "TandoorRequests",
        "Method: $method, URL: ${credentials.instanceUrl.redactForRelease()}/api${endpoint}"
    )
    val url = if(endpoint.startsWith("@")) {
        "${credentials.instanceUrl}/${endpoint.substring(1)}"
    } else {
        "${credentials.instanceUrl}/api${endpoint}"
    }

    val jsonObjectRequest = object : JsonObjectRequest(
        method,
        url,
        data,
        { response: JSONObject? ->
            cont.resume(response)
        },
        { error: VolleyError? ->
            Log.e("TandoorRequests", "Exception for request \"$endpoint\" ($method) (jsonObject)")
            error?.printStackTrace()
            cont.resumeWithException(
                TandoorRequestsError(
                    error,
                    method,
                    "${credentials.instanceUrl.redactForRelease()}/api${endpoint}"
                )
            )
        }
    ) {
        override fun getHeaders(): MutableMap<String, String> {
            val headers = super.getHeaders().toMutableMap()
            if(token != null) headers["Authorization"] = "Bearer ${token.token}"
            if(cookie != null) {
                val csrfToken = cookie.replace(Regex(".*csrftoken="), "").replace(Regex(";.*"), "")
                headers["X-CSRFTOKEN"] = csrfToken
                headers["Cookie"] = cookie
            }
            headers["Referer"] = credentials.instanceUrl
            return headers
        }
    }

    queue.add(jsonObjectRequest)
}

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.reqBitmap(
    endpoint: String,
    method: Int,
    bitmap: Bitmap
) = suspendCoroutine { cont ->
    val queue = Volley.newRequestQueue(context)

    val token = this.credentials.token
    val cookie = this.credentials.cookie

    Log.d(
        "TandoorRequests",
        "Method: $method, URL: ${credentials.instanceUrl.redactForRelease()}/api${endpoint}"
    )
    val url = "${credentials.instanceUrl}/api${endpoint}"

    val request = object : VolleyMultipartRequest(
        method,
        url,
        { response: NetworkResponse? ->
            cont.resume(response)
        },
        { error: VolleyError? ->
            Log.e("TandoorRequests", "Exception for request \"$endpoint\" ($method) (jsonObject)")
            error?.printStackTrace()
            cont.resumeWithException(
                TandoorRequestsError(
                    error,
                    method,
                    "${credentials.instanceUrl.redactForRelease()}/api${endpoint}"
                )
            )
        }
    ) {
        override fun getHeaders(): MutableMap<String, String> {
            val headers = super.getHeaders().toMutableMap()
            if(token != null) headers["Authorization"] = "Bearer ${token.token}"
            if(cookie != null) {
                val csrfToken = cookie.replace(Regex(".*csrftoken="), "").replace(Regex(";.*"), "")
                headers["X-CSRFTOKEN"] = csrfToken
                headers["Cookie"] = cookie
            }
            headers["Referer"] = credentials.instanceUrl
            return headers
        }

        override fun getByteData(): MutableMap<String, DataPart> {
            val image = ByteArrayOutputStream().run {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, this)
                toByteArray()
            }

            val params = mutableMapOf<String, DataPart>()
            params["image"] = DataPart("${System.currentTimeMillis()}.png", image)
            return params
        }
    }

    queue.add(request)
}

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.reqMultipart(
    endpoint: String,
    method: Int,
    data: MutableMap<String, String>
) = suspendCoroutine { cont ->
    val queue = Volley.newRequestQueue(context)

    val token = this.credentials.token
    val cookie = this.credentials.cookie

    Log.d(
        "TandoorRequests",
        "Method: $method, URL: ${credentials.instanceUrl.redactForRelease()}/api${endpoint}"
    )
    val url = "${credentials.instanceUrl}/api${endpoint}"

    val request = object : VolleyMultipartRequest(
        method,
        url,
        { response: NetworkResponse? ->
            cont.resume(response)
        },
        { error: VolleyError? ->
            Log.e("TandoorRequests", "Exception for request \"$endpoint\" ($method) (jsonObject)")
            error?.printStackTrace()
            cont.resumeWithException(
                TandoorRequestsError(
                    error,
                    method,
                    "${credentials.instanceUrl.redactForRelease()}/api${endpoint}"
                )
            )
        }
    ) {
        override fun getHeaders(): MutableMap<String, String> {
            val headers = super.getHeaders().toMutableMap()
            if(token != null) headers["Authorization"] = "Bearer ${token.token}"
            if(cookie != null) {
                val csrfToken = cookie.replace(Regex(".*csrftoken="), "").replace(Regex(";.*"), "")
                headers["X-CSRFTOKEN"] = csrfToken
                headers["Cookie"] = cookie
            }
            headers["Referer"] = credentials.instanceUrl
            return headers
        }

        override fun getParams(): MutableMap<String, String> {
            return data
        }
    }

    queue.add(request)
}

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.reqInputStream(
    endpoint: String,
    method: Int,
    params: HashMap<String, String> = hashMapOf()
) = suspendCoroutine { cont ->
    val queue = Volley.newRequestQueue(context)

    val token = this.credentials.token
    val cookie = this.credentials.cookie

    Log.d(
        "TandoorRequests",
        "Method: $method, URL: ${credentials.instanceUrl.redactForRelease()}/api${endpoint}"
    )
    val url = "${credentials.instanceUrl}/api${endpoint}"

    val request = object : VolleyInputStreamRequest(
        method,
        url,
        { response ->
            cont.resume(response)
        },
        { error: VolleyError? ->
            Log.e("TandoorRequests", "Exception for request \"$endpoint\" ($method) (jsonObject)")
            error?.printStackTrace()
            cont.resumeWithException(
                TandoorRequestsError(
                    error,
                    method,
                    "${credentials.instanceUrl.redactForRelease()}/api${endpoint}"
                )
            )
        },
        params
    ) {
        override fun getHeaders(): MutableMap<String, String> {
            val headers = super.getHeaders().toMutableMap()
            if(token != null) headers["Authorization"] = "Bearer ${token.token}"
            if(cookie != null) {
                val csrfToken = cookie.replace(Regex(".*csrftoken="), "").replace(Regex(";.*"), "")
                headers["X-CSRFTOKEN"] = csrfToken
                headers["Cookie"] = cookie
            }
            headers["Referer"] = credentials.instanceUrl
            return headers
        }
    }

    queue.add(request)
}

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.getObject(endpoint: String) = reqObject(endpoint, Request.Method.GET)

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.getArray(endpoint: String) = reqArray(endpoint, Request.Method.GET)

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.getInputStream(endpoint: String): ByteArray =
    reqInputStream(endpoint, Request.Method.GET)

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.postObject(endpoint: String, data: JSONObject) =
    reqObject(endpoint, Request.Method.POST, data)

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.postArray(endpoint: String, data: JSONArray) =
    reqArray(endpoint, Request.Method.POST, data)

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.postMultipart(endpoint: String, data: MutableMap<String, String>) =
    reqMultipart(endpoint, Request.Method.POST, data)

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.put(endpoint: String, data: JSONObject) =
    req(endpoint, Request.Method.PUT, data)

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.putObject(endpoint: String, data: JSONObject) =
    reqObject(endpoint, Request.Method.PUT, data)

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.putArray(endpoint: String, data: JSONArray) =
    reqArray(endpoint, Request.Method.PUT, data)

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.putBitmap(endpoint: String, bitmap: Bitmap) =
    reqBitmap(endpoint, Request.Method.PUT, bitmap)

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.putMultipart(endpoint: String, data: MutableMap<String, String>) =
    reqMultipart(endpoint, Request.Method.PUT, data)

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.patchObject(endpoint: String, data: JSONObject) =
    reqObject(endpoint, Request.Method.PATCH, data)

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.patchArray(endpoint: String, data: JSONArray) =
    reqArray(endpoint, Request.Method.PATCH, data)

@Throws(TandoorRequestsError::class)
suspend fun TandoorClient.delete(endpoint: String) =
    req(endpoint, Request.Method.DELETE)