package org.weibocontentlib.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.weibocontentlib.entity.Status;
import org.weibocontentlib.handler.exception.HandlerException;

public class SaeAppBatchhelperHandler {

	public void authorize(HttpClient httpClient) throws HandlerException {
		String url = "https://api.weibo.com/oauth2/authorize?client_id=3144078080&redirect_uri=http%3A%2F%2Fbatchhelper.sinaapp.com%2F&response_type=code";

		HttpGet get = new HttpGet(url);

		try {
			httpClient.execute(get);
		} catch (ClientProtocolException e) {
			throw new HandlerException(e);
		} catch (IOException e) {
			throw new HandlerException(e);
		} finally {
			get.releaseConnection();
		}
	}

	public void publishEntity(HttpClient httpClient, HttpEntity httpEntity)
			throws HandlerException {
		String url = "http://batchhelper.sinaapp.com/action.php?action=uploadStatus";

		HttpPost post = new HttpPost(url);

		post.setEntity(httpEntity);

		try {
			HttpResponse response = httpClient.execute(post);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				throw new HandlerException(String.valueOf(statusCode));
			}
		} catch (ClientProtocolException e) {
			throw new HandlerException(e);
		} catch (IOException e) {
			throw new HandlerException(e);
		} finally {
			post.releaseConnection();
		}
	}

	public void publishStatus(HttpClient httpClient, Status status)
			throws HandlerException {
		List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
		nameValuePairList.add(new BasicNameValuePair("text", status
				.getStatusText()));
		nameValuePairList.add(new BasicNameValuePair("picturePath", status
				.getStatusPictureFile()));

		HttpEntity httpEntity;

		try {
			httpEntity = new UrlEncodedFormEntity(nameValuePairList, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new HandlerException(e);
		}

		publishEntity(httpClient, httpEntity);
	}

}
