/**
 * 
 */
package com.outsourcing.industry.restaurant.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.outsourcing.industry.restaurant.util.GlobalParam;

import android.os.Handler;
import android.util.Log;

/**
 * @author Tianhua, Pan
 * 
 */
public class HttpRequest implements Runnable {
	private Handler handler;
	private String requestUrl;
	private HttpPost request_post;
	private HttpGet request_get;
	private List<NameValuePair> list_params;

	public HttpRequest(Handler handler, String requestUrl) {
		this.handler = handler;
		this.requestUrl = requestUrl;
		this.list_params = new ArrayList<NameValuePair>();
	}

	public void addParam(String tag, String value) {
		NameValuePair param_pair = new BasicNameValuePair(tag, value);
		list_params.add(param_pair);
	}

	public void asyncPostRequest() throws IOException {
		request_post = new HttpPost(requestUrl);
		HttpEntity postEntity = new UrlEncodedFormEntity(list_params, "utf-8");
		request_post.setEntity(postEntity);
	}

	public void asyncGetRequest() {
		request_get = new HttpGet(requestUrl);
	}

	@Override
	public void run() {
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = null;
		try {
			if (null != request_post) {
				response = httpClient.execute(request_post);
			} else if (null != request_get) {
				response = httpClient.execute(request_get);
			} else {
				Log.e(GlobalParam.TAG,
						"Can't request directly from thread start.");
				return;
			}

			if (response != null
					&& response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = response.getEntity();
			}
		} catch (IOException e) {
			Log.e(GlobalParam.TAG, "IOException", e);
		}

	}
}
