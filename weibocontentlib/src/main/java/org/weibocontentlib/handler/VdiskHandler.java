package org.weibocontentlib.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.weibocontentlib.handler.exception.HandlerException;

public class VdiskHandler {

	private String ajaxRequestUploadUrl(HttpClient httpClient, String destFile)
			throws HandlerException {
		String result;

		HttpPost post = new HttpPost(
				"http://vdisk.weibo.com/file/ajaxRequestUploadUrl");

		post.addHeader(HttpHeaders.REFERER, "http://vdisk.weibo.com/");

		List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
		nameValuePairList.add(new BasicNameValuePair("path", destFile));
		nameValuePairList.add(new BasicNameValuePair("overwrite", "true"));
		nameValuePairList.add(new BasicNameValuePair("http_response_code",
				"200"));

		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairList));

			HttpResponse response = httpClient.execute(post);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
			} else {
				throw new HandlerException(String.valueOf(statusCode));
			}
		} catch (UnsupportedEncodingException e) {
			throw new HandlerException(e);
		} catch (ClientProtocolException e) {
			throw new HandlerException(e);
		} catch (IOException e) {
			throw new HandlerException(e);
		} finally {
			post.releaseConnection();
		}

		int beginIndex = result.indexOf("\"");
		int endIndex = result.lastIndexOf("\"");

		result = result.substring(beginIndex + 1, endIndex);
		result = result.replaceAll("\\\\", "");

		return result;
	}

	private String getAjaxRequestUploadUrl(HttpClient httpClient,
			String destFile) throws HandlerException {
		String ajaxRequestUploadUrl = ajaxRequestUploadUrl(httpClient, destFile);

		ajaxRequestUploadUrl = ajaxRequestUploadUrl.replace("files",
				"files_put");

		return ajaxRequestUploadUrl;
	}

	private void uploadEntity(HttpClient httpClient, HttpEntity httpEntity,
			String ajaxRequestUploadUrl) throws HandlerException {
		HttpPut put = new HttpPut(ajaxRequestUploadUrl);

		put.addHeader(HttpHeaders.CONTENT_TYPE, "multipart/form-data");

		put.setEntity(httpEntity);

		try {
			HttpResponse response = httpClient.execute(put);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
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

	public void addBytes(HttpClient httpClient, byte[] bytes, String destFile)
			throws HandlerException {
		String ajaxRequestUploadUrl = getAjaxRequestUploadUrl(httpClient,
				destFile);

		HttpEntity httpEntity = new ByteArrayEntity(bytes);

		uploadEntity(httpClient, httpEntity, ajaxRequestUploadUrl);
	}

}
