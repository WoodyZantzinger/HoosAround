package com.hoos.around;

import android.os.Handler;

import com.loopj.android.http.*;

public class RestClient {
	  private static final String BASE_URL = "http://plato.cs.virginia.edu/~wz2ae/spinach/cakephp/";
	  
	  private static AsyncHttpClient client = new AsyncHttpClient();

	  public static void get(String url, RequestParams params, Handler handler, AsyncHttpResponseHandler responseHandler) {
	      client.get(getAbsoluteUrl(url), params, responseHandler);
	  }

	  public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	      client.post(getAbsoluteUrl(url), params, responseHandler);
	  }

	  public static String getAbsoluteUrl(String relativeUrl) {
	      return BASE_URL + relativeUrl;
	  }

}
