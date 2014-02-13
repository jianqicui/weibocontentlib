package org.weibocontentlib.handler;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.weibocontentlib.entity.SaeStorage;
import org.weibocontentlib.handler.exception.HandlerException;

public class SaeStorageHandler {

	public SaeStorage login(HttpClient httpClient, String accessKey,
			String secretKey) throws HandlerException {
		String xStorageUrl;
		String xAuthToken;

		HttpGet get = new HttpGet("https://auth.sinas3.com/v1.0");

		get.addHeader("X-Auth-User", accessKey);
		get.addHeader("X-Auth-Key", secretKey);

		try {
			HttpResponse response = httpClient.execute(get);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {
				xStorageUrl = response.getLastHeader("X-Storage-Url")
						.getValue();
				xAuthToken = response.getLastHeader("X-Auth-Token").getValue();
			} else {
				throw new HandlerException(String.valueOf(statusCode));
			}
		} catch (ClientProtocolException e) {
			throw new HandlerException(e);
		} catch (IOException e) {
			throw new HandlerException(e);
		} finally {
			get.releaseConnection();
		}

		SaeStorage saeStorage = new SaeStorage();
		saeStorage.setxStorageUrl(xStorageUrl);
		saeStorage.setxAuthToken(xAuthToken);

		return saeStorage;
	}

	private void uploadEntity(HttpClient httpClient, SaeStorage saeStorage,
			HttpEntity httpEntity, String destFile) throws HandlerException {
		String xStorageUrl = saeStorage.getxStorageUrl();
		String xAuthToken = saeStorage.getxAuthToken();

		HttpPut put = new HttpPut(xStorageUrl + destFile);

		put.addHeader("X-Auth-Token", xAuthToken);

		put.setEntity(httpEntity);

		try {
			HttpResponse response = httpClient.execute(put);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_CREATED) {
				throw new HandlerException(String.valueOf(statusCode));
			}
		} catch (ClientProtocolException e) {
			throw new HandlerException(e);
		} catch (IOException e) {
			throw new HandlerException(e);
		} finally {
			put.releaseConnection();
		}
	}

	public void addBytes(HttpClient httpClient, SaeStorage saeStorage,
			byte[] bytes, String destFile) throws HandlerException {
		HttpEntity httpEntity = new ByteArrayEntity(bytes);

		uploadEntity(httpClient, saeStorage, httpEntity, destFile);
	}

}
