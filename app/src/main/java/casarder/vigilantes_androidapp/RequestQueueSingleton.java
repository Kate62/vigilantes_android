package casarder.vigilantes_androidapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by ritaf_000 on 28/12/2017.
 */

public class RequestQueueSingleton {

    private static RequestQueueSingleton instance;

    private RequestQueue queue;

    private static Context context;

    private RequestQueueSingleton(Context nContext) {
        context = nContext;
        queue = getRequestQueue();
    }

    public static synchronized RequestQueueSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new RequestQueueSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return queue;
    }

    public <T> void addToQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

}
