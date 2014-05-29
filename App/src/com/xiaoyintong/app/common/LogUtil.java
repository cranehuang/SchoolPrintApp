package com.xiaoyintong.app.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import org.apache.http.Header;

import android.util.Log;

public class LogUtil {
	
	 public static void debugHeaders(String TAG, Header[] headers) {
	        if (headers != null) {
	            Log.d(TAG, "Return Headers:");
	            StringBuilder builder = new StringBuilder();
	            for (Header h : headers) {
	                String _h = String.format(Locale.US, "%s : %s", h.getName(), h.getValue());
	                Log.d(TAG, _h);
	                builder.append(_h);
	                builder.append("\n");
	            }
	            Log.d(TAG, builder.toString());
	        }
	    }

	    public static String throwableToString(Throwable t) {
	        if (t == null)
	            return null;

	        StringWriter sw = new StringWriter();
	        t.printStackTrace(new PrintWriter(sw));
	        return sw.toString();
	    }

	    public static void debugThrowable(String TAG, Throwable t) {
	        if (t != null) {
	            Log.e(TAG, "AsyncHttpClient returned error", t);
	        }
	    }

	    public static void debugResponse(String TAG, String response) {
	        if (response != null) {
	            Log.d(TAG, "Response data:");
	            Log.d(TAG, response);
	        }
	    }

	    public static void debugStatusCode(String TAG, int statusCode) {
	        String msg = String.format(Locale.US, "Return Status Code: %d", statusCode);
	        Log.d(TAG, msg);
	    }
}
