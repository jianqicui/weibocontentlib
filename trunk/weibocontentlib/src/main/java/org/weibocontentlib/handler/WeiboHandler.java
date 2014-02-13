package org.weibocontentlib.handler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.weibocontentlib.entity.Status;
import org.weibocontentlib.handler.exception.HandlerException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class WeiboHandler {

	private static final Pattern pageSizePattern = Pattern
			.compile("countPage=(\\d+)");

	private ObjectMapper objectMapper;

	public void initialize() {
		objectMapper = new ObjectMapper();
	}

	private JsonNode getJsonNode(String result) throws HandlerException {
		int beginIndex = result.indexOf("(");
		int endIndex = result.lastIndexOf(")");

		JsonNode jsonNode;

		try {
			jsonNode = objectMapper.readTree(result.substring(beginIndex + 1,
					endIndex));
		} catch (JsonProcessingException e) {
			throw new HandlerException(e);
		} catch (IOException e) {
			throw new HandlerException(e);
		}

		return jsonNode;
	}

	private String get(HttpClient httpClient, String url)
			throws HandlerException {
		String result;

		HttpGet get = new HttpGet(url);

		try {
			HttpResponse response = httpClient.execute(get);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
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

		return result;
	}

	private String crossDomain(HttpClient httpClient) throws HandlerException {
		StringBuilder url = new StringBuilder();

		url.append("http://login.sina.com.cn/sso/crossdomain.php");
		url.append("?");
		url.append("scriptId");
		url.append("=");
		url.append("ssoCrossDomainScriptId");
		url.append("&");
		url.append("callback");
		url.append("=");
		url.append("sinaSSOController.crossDomainCallBack");
		url.append("&");
		url.append("action");
		url.append("=");
		url.append("login");
		url.append("&");
		url.append("domain");
		url.append("=");
		url.append("sina.com.cn");
		url.append("&");
		url.append("sr");
		url.append("=");
		url.append("1440*900");
		url.append("&");
		url.append("client");
		url.append("=");
		url.append("ssologin.js(v1.4.13)");

		String result = get(httpClient, url.toString());

		return result;
	}

	private String loginWeibo(HttpClient httpClient, String query)
			throws HandlerException {
		StringBuilder url = new StringBuilder();

		url.append("http://www.weibo.com/sso/login.php");
		url.append("?");
		url.append(query);
		url.append("&");
		url.append("callback");
		url.append("=");
		url.append("sinaSSOController.doCrossDomainCallBack");
		url.append("&");
		url.append("scriptId");
		url.append("=");
		url.append("ssoscript0");
		url.append("&");
		url.append("client");
		url.append("=");
		url.append("ssologin.js(v1.4.13)");

		String result = get(httpClient, url.toString());

		return result;
	}

	public void refresh(HttpClient httpClient) throws HandlerException {
		// crossDomain
		String result = crossDomain(httpClient);

		JsonNode jsonNode = getJsonNode(result);

		String arrUrl = ((ArrayNode) jsonNode.get("arrURL")).get(0).asText();

		String query;

		try {
			URI uri = new URI(arrUrl);

			query = uri.getQuery();
		} catch (URISyntaxException e) {
			throw new HandlerException(e);
		}

		// loginWeibo
		loginWeibo(httpClient, query);
	}

	public int getPageSize(HttpClient httpClient, String userId)
			throws HandlerException {
		String url = "http://www.weibo.com/p/aj/mblog/mbloglist?id=100505"
				+ userId + "&pre_page=1&page=1&pagebar=1";

		String content = get(httpClient, url);

		int pageSize;

		String html;

		try {
			JsonNode jsonNode = objectMapper.readTree(content);

			JsonNode htmlJsonNode = jsonNode.get("data");

			html = htmlJsonNode.asText();
		} catch (JsonProcessingException e) {
			throw new HandlerException(e);
		} catch (IOException e) {
			throw new HandlerException(e);
		}

		Matcher matcher = pageSizePattern.matcher(html);

		if (matcher.find()) {
			pageSize = Integer.parseInt(matcher.group(1));
		} else {
			throw new HandlerException("GetPageSize failed");
		}

		return pageSize;
	}

	private List<Status> parseDetail(String html) {
		Document doc = Jsoup.parse(html);
		Elements divs = doc.select(".WB_detail");

		List<Status> statusList = new ArrayList<Status>();

		for (Element div : divs) {
			Elements textDocs = div.select(".WB_text");
			Elements imgDocs = div.select("img.bigcursor");

			if (textDocs.size() > 1) {
				continue;
			}

			if (imgDocs.size() < 1) {
				continue;
			}

			String statusText = textDocs.get(0).text();

			String statusPictureFile = imgDocs.get(0).attr("src");
			statusPictureFile = statusPictureFile.replace("thumbnail", "large");

			Status status = new Status();
			status.setStatusText(statusText);
			status.setStatusPictureFile(statusPictureFile);

			statusList.add(status);
		}

		return statusList;
	}

	private List<Status> parseHtmlElement(String content)
			throws HandlerException {
		List<Status> statusList;

		Document doc = Jsoup.parse(content);

		Elements scripts = doc.getElementsByTag("script");

		if (!scripts.isEmpty()) {
			String scriptHtml = null;

			for (Element script : scripts) {
				if (script.html().contains("WB_detail")) {
					scriptHtml = script.html();

					break;
				}
			}

			if (scriptHtml != null) {
				JsonNode jsonNode = getJsonNode(scriptHtml);

				JsonNode htmlJsonNode = jsonNode.get("html");

				if (htmlJsonNode != null) {
					String html = htmlJsonNode.asText();

					statusList = parseDetail(html);
				} else {
					throw new HandlerException("ParseHtmlElement failed");
				}
			} else {
				throw new HandlerException("ParseHtmlElement failed");
			}
		} else {
			throw new HandlerException("ParseHtmlElement failed");
		}

		return statusList;
	}

	private List<Status> parseHtml(HttpClient httpClient, String url)
			throws HandlerException {
		List<Status> statusList;

		String content = get(httpClient, url);

		statusList = parseHtmlElement(content);

		return statusList;
	}

	private List<Status> parseJsonElement(String content)
			throws HandlerException {
		List<Status> statusList;

		try {
			JsonNode jsonNode = objectMapper.readTree(content);

			JsonNode htmlJsonNode = jsonNode.get("data");

			if (htmlJsonNode != null) {
				String html = htmlJsonNode.asText();

				statusList = parseDetail(html);
			} else {
				throw new HandlerException("ParseJsonElement failed");
			}
		} catch (JsonProcessingException e) {
			throw new HandlerException(e);
		} catch (IOException e) {
			throw new HandlerException(e);
		}

		return statusList;
	}

	private List<Status> parseJson(HttpClient httpClient, String url)
			throws HandlerException {
		List<Status> statusList;

		String content = get(httpClient, url);

		statusList = parseJsonElement(content);

		return statusList;
	}

	public List<Status> getStatusListByPageNo(HttpClient httpClient,
			String userId, int pageNo) throws HandlerException {
		List<Status> statusList = new ArrayList<Status>();

		String url;

		List<Status> statuses;

		// page
		url = "http://www.weibo.com/p/100505" + userId + "/weibo?page="
				+ pageNo;

		statuses = parseHtml(httpClient, url);

		statusList.addAll(statuses);

		// pagebar=0
		url = "http://www.weibo.com/p/aj/mblog/mbloglist?id=100505" + userId
				+ "&pre_page=" + pageNo + "&page=" + pageNo + "&pagebar=0";

		statuses = parseJson(httpClient, url);

		statusList.addAll(statuses);

		// pagebar=1
		url = "http://www.weibo.com/p/aj/mblog/mbloglist?id=100505" + userId
				+ "&pre_page=" + pageNo + "&page=" + pageNo + "&pagebar=1";

		statuses = parseJson(httpClient, url);

		statusList.addAll(statuses);

		return statusList;
	}

}