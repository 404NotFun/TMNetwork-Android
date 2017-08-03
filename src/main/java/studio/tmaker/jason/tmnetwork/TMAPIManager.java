package studio.tmaker.jason.tmnetwork;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jasontsai on 2017/7/26.
 */

public class TMAPIManager {
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private static TMAPIManager instance = null;
    public String SERVER_DOMAIN = "http://10.0.2.2:8080/";
    public Map<String, String> HEADERS = null;
    public Map<String, String> params = new HashMap<>();
    public TMAPIManager() {

    }

    public String getSERVER_DOMAIN() {
        return SERVER_DOMAIN;
    }

    public void setSERVER_DOMAIN(String SERVER_DOMAIN) {
        this.SERVER_DOMAIN = SERVER_DOMAIN;
    }

    public Map<String, String> getHEADERS() {
        return HEADERS;
    }

    public void setHEADERS(Map<String, String> HEADERS) {
        this.HEADERS = HEADERS;
    }

    public void setHEADERS(String k, String v) {
        this.HEADERS.put(k,v);
    }

    public void clearHEADERS(String k) {
        this.HEADERS.remove(k);
    }

    public static TMAPIManager getInstance() {
        if (instance == null) {
            instance = new TMAPIManager();
        }
        if (instance.HEADERS == null) {
            instance.HEADERS = new HashMap<>();
            instance.HEADERS.put("HashKey", "2287c6b8641dd2d21ab050eb9ff795f3");
            instance.HEADERS.put("Authorization", "ApiFoodieMA");
        }

        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void Get(String url, final TMNetwork.ApiResponseListener resp) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                        resp.completion(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        resp.failure(error);
                    }
                }
        );
        mRequestQueue.add(stringRequest);
    }

    public void Post(String url, final String jsonString, final Map<String, String> headers, final TMNetwork.ApiResponseListener responseListener){
//        try {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                responseListener.completion(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.failure(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return jsonString == null ? null : jsonString.getBytes("utf-8");

                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", jsonString, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // TODO Auto-generated method stub
                String str = null;
                try {
                    str = new String(response.data,"utf-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError){
                if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    volleyError = error;
                }

                return volleyError;
            }
        };

        mRequestQueue.add(stringRequest);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public interface ApiResponseListener {
        void completion(String resp);
        void failure(VolleyError error);
    }


}
