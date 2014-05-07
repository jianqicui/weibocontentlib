package org.weibocontentlib.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weibocontentlib.action.exception.ActionException;
import org.weibocontentlib.entity.ApplyingUser;
import org.weibocontentlib.entity.Category;
import org.weibocontentlib.entity.CollectingUser;
import org.weibocontentlib.entity.QueryingUser;
import org.weibocontentlib.entity.SaeStorage;
import org.weibocontentlib.entity.Status;
import org.weibocontentlib.entity.StatusPhase;
import org.weibocontentlib.entity.TransferingUser;
import org.weibocontentlib.entity.Type;
import org.weibocontentlib.handler.SaeAppBatchhelperHandler;
import org.weibocontentlib.handler.SaeStorageHandler;
import org.weibocontentlib.handler.VdiskHandler;
import org.weibocontentlib.handler.WeiboHandler;
import org.weibocontentlib.handler.exception.HandlerException;
import org.weibocontentlib.service.ApplyingUserService;
import org.weibocontentlib.service.CategoryService;
import org.weibocontentlib.service.CollectingUserService;
import org.weibocontentlib.service.QueryingUserService;
import org.weibocontentlib.service.StatusService;
import org.weibocontentlib.service.TransferingUserService;
import org.weibocontentlib.service.TypeService;
import org.weibocontentlib.service.exception.ServiceException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WeiboContentLibAction {

	private static final Logger logger = LoggerFactory
			.getLogger(WeiboContentLibAction.class);

	private QueryingUserService queryingUserService;

	private CategoryService categoryService;

	private TypeService typeService;

	private CollectingUserService collectingUserService;

	private StatusService statusService;

	private WeiboHandler weiboHandler;

	private int filteringStatusSize;

	private TransferingUserService transferingUserService;

	private SaeAppBatchhelperHandler saeAppBatchhelperHandler;

	private VdiskHandler vdiskHandler;

	private SaeStorageHandler saeStorageHandler;

	private String saeStorageAccessKey;

	private String saeStorageSecretKey;

	private int transferingStatusSize;

	private ApplyingUserService applyingUserService;

	private ObjectMapper objectMapper;

	private DefaultHttpClient collectingDefaultHttpClient;

	private DefaultHttpClient transferingDefaultHttpClient;

	private DefaultHttpClient publishingDefaultHttpClient;

	public void setQueryingUserService(QueryingUserService queryingUserService) {
		this.queryingUserService = queryingUserService;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}

	public void setCollectingUserService(
			CollectingUserService collectingUserService) {
		this.collectingUserService = collectingUserService;
	}

	public void setStatusService(StatusService statusService) {
		this.statusService = statusService;
	}

	public void setWeiboHandler(WeiboHandler weiboHandler) {
		this.weiboHandler = weiboHandler;
	}

	public void setFilteringStatusSize(int filteringStatusSize) {
		this.filteringStatusSize = filteringStatusSize;
	}

	public void setTransferingUserService(
			TransferingUserService transferingUserService) {
		this.transferingUserService = transferingUserService;
	}

	public void setSaeAppBatchhelperHandler(
			SaeAppBatchhelperHandler saeAppBatchhelperHandler) {
		this.saeAppBatchhelperHandler = saeAppBatchhelperHandler;
	}

	public void setVdiskHandler(VdiskHandler vdiskHandler) {
		this.vdiskHandler = vdiskHandler;
	}

	public void setSaeStorageHandler(SaeStorageHandler saeStorageHandler) {
		this.saeStorageHandler = saeStorageHandler;
	}

	public void setSaeStorageAccessKey(String saeStorageAccessKey) {
		this.saeStorageAccessKey = saeStorageAccessKey;
	}

	public void setSaeStorageSecretKey(String saeStorageSecretKey) {
		this.saeStorageSecretKey = saeStorageSecretKey;
	}

	public void setTransferingStatusSize(int transferingStatusSize) {
		this.transferingStatusSize = transferingStatusSize;
	}

	public void setApplyingUserService(ApplyingUserService applyingUserService) {
		this.applyingUserService = applyingUserService;
	}

	public void initialize() {
		objectMapper = new ObjectMapper();

		collectingDefaultHttpClient = getDefaultHttpClient();
		transferingDefaultHttpClient = getDefaultHttpClient();
		publishingDefaultHttpClient = getDefaultHttpClient();
	}

	public void destroy() {
		collectingDefaultHttpClient.getConnectionManager().shutdown();
		transferingDefaultHttpClient.getConnectionManager().shutdown();
		publishingDefaultHttpClient.getConnectionManager().shutdown();
	}

	private DefaultHttpClient getDefaultHttpClient() {
		X509TrustManager tm = new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType)
					throws CertificateException {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType)
					throws CertificateException {
			}
		};

		SSLContext sslContext;

		try {
			sslContext = SSLContext.getInstance("TLS");

			sslContext.init(null, new TrustManager[] { tm }, null);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		} catch (KeyManagementException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		}

		SSLSocketFactory ssf = new SSLSocketFactory(sslContext);
		Scheme scheme = new Scheme("https", 443, ssf);

		SchemeRegistry registry = SchemeRegistryFactory.createDefault();
		registry.register(scheme);

		PoolingClientConnectionManager poolingClientConnectionManager = new PoolingClientConnectionManager(
				registry, 60000, TimeUnit.MILLISECONDS);
		poolingClientConnectionManager.setDefaultMaxPerRoute(10);
		poolingClientConnectionManager.setMaxTotal(100);

		BasicHttpParams basicHttpParams = new BasicHttpParams();

		basicHttpParams.setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.BROWSER_COMPATIBILITY);
		basicHttpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				600000);
		basicHttpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, 600000);

		return new DefaultHttpClient(poolingClientConnectionManager,
				basicHttpParams);
	}

	private void setCookies(DefaultHttpClient defaultHttpClient, byte[] cookies) {
		List<Map<String, Object>> list;

		try {
			list = objectMapper.readValue(cookies,
					new TypeReference<List<Map<String, Object>>>() {
					});
		} catch (JsonParseException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		} catch (JsonMappingException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		} catch (IOException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		}

		BasicCookieStore basicCookieStore = new BasicCookieStore();

		for (Map<String, Object> map : list) {
			String name = (String) map.get("name");
			String value = (String) map.get("value");

			String comment = (String) map.get("comment");
			String domain = (String) map.get("domain");

			Date expiryDate = null;
			if (map.get("expiryDate") != null) {
				expiryDate = new Date((long) map.get("expiryDate"));
			}

			String path = (String) map.get("path");
			boolean secure = (boolean) map.get("secure");
			int version = (int) map.get("version");

			BasicClientCookie basicClientCookie = new BasicClientCookie(name,
					value);

			basicClientCookie.setComment(comment);
			basicClientCookie.setDomain(domain);
			basicClientCookie.setExpiryDate(expiryDate);
			basicClientCookie.setPath(path);
			basicClientCookie.setSecure(secure);
			basicClientCookie.setVersion(version);

			basicCookieStore.addCookie(basicClientCookie);
		}

		defaultHttpClient.setCookieStore(basicCookieStore);
	}

	private byte[] getCookies(DefaultHttpClient defaultHttpClient) {
		byte[] cookies;

		CookieStore cookieStore = defaultHttpClient.getCookieStore();

		List<Cookie> cookieList = cookieStore.getCookies();

		try {
			cookies = objectMapper.writeValueAsBytes(cookieList);
		} catch (JsonProcessingException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		}

		return cookies;
	}

	private int getCurrentPageNo(int pageSize, int pageNo, int currentPageSize) {
		int currentPageNo;

		if (pageSize == currentPageSize) {
			currentPageNo = pageNo;
		} else {
			currentPageNo = pageNo + currentPageSize - pageSize;

			if (currentPageNo > currentPageSize) {
				currentPageNo = currentPageSize;
			}

			if (currentPageNo < 1) {
				currentPageNo = 1;
			}
		}

		return currentPageNo;
	}

	private void collectStatuses() {
		QueryingUser queryingUser;

		try {
			queryingUser = queryingUserService.getQueryingUser();
		} catch (ServiceException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		}

		setCookies(collectingDefaultHttpClient, queryingUser.getCookies());

		try {
			weiboHandler.refresh(collectingDefaultHttpClient);
		} catch (HandlerException e) {
			return;
		}

		queryingUser.setCookies(getCookies(collectingDefaultHttpClient));

		try {
			queryingUserService.updateQueryingUser(queryingUser);
		} catch (ServiceException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		}

		List<Category> categoryList;

		try {
			categoryList = categoryService.getCategoryList();
		} catch (ServiceException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		}

		for (Category category : categoryList) {
			int categoryId = category.getCategoryId();

			List<Type> typeList;

			try {
				typeList = typeService.getTypeList(categoryId);
			} catch (ServiceException e) {
				logger.error("Exception", e);

				throw new ActionException(e);
			}

			for (Type type : typeList) {
				int typeId = type.getTypeId();

				List<Status> statusList = new ArrayList<Status>();

				int statusSize = statusList.size();

				logger.debug(String
						.format("Begin to collect statuses, categoryId = %s, typeId = %s, statusSize = %s",
								categoryId, typeId, statusSize));

				List<CollectingUser> collectingUserList;

				try {
					collectingUserList = collectingUserService
							.getCollectingUserList(categoryId, typeId);
				} catch (ServiceException e) {
					logger.error("Exception", e);

					throw new ActionException(e);
				}

				for (CollectingUser collectingUser : collectingUserList) {
					String userId = collectingUser.getUserId();
					int pageSize = collectingUser.getPageSize();
					int pageNo = collectingUser.getPageNo();

					int currentPageSize;

					try {
						currentPageSize = weiboHandler.getPageSize(
								collectingDefaultHttpClient, userId);
					} catch (HandlerException e) {
						continue;
					}

					int currentPageNo = getCurrentPageNo(pageSize, pageNo,
							currentPageSize);

					if (currentPageNo > 1) {
						List<Status> statuses = new ArrayList<Status>();

						try {
							statuses = weiboHandler.getStatusListByPageNo(
									collectingDefaultHttpClient, userId,
									currentPageNo);
						} catch (HandlerException e) {

						}

						statusList.addAll(statuses);

						currentPageNo--;

						collectingUser.setPageSize(currentPageSize);
						collectingUser.setPageNo(currentPageNo);

						try {
							collectingUserService.updateCollectingUser(
									categoryId, typeId, collectingUser);
						} catch (ServiceException e) {
							logger.error("Exception", e);

							throw new ActionException(e);
						}
					}
				}

				statusSize = 0;

				for (Status status : statusList) {
					try {
						statusService.addStatus(categoryId, typeId,
								StatusPhase.collected, status);

						statusSize++;
					} catch (ServiceException e) {
						logger.error("Exception", e);

						throw new ActionException(e);
					}
				}

				logger.debug(String
						.format("End to collect statuses, categoryId = %s, typeId = %s, statusSize = %s",
								categoryId, typeId, statusSize));
			}
		}
	}

	private boolean isSimilarStatusExisting(int categoryId, int typeId,
			Status status) {
		boolean similarStatusExisting = false;

		try {
			similarStatusExisting = statusService.isSimilarStatusExisting(
					categoryId, typeId, StatusPhase.filtered, status);
		} catch (ServiceException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		}

		if (!similarStatusExisting) {
			try {
				similarStatusExisting = statusService.isSimilarStatusExisting(
						categoryId, typeId, StatusPhase.verified, status);
			} catch (ServiceException e) {
				logger.error("Exception", e);

				throw new ActionException(e);
			}
		}

		return similarStatusExisting;
	}

	private void filterStatuses() {
		List<Category> categoryList;

		try {
			categoryList = categoryService.getCategoryList();
		} catch (ServiceException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		}

		for (Category category : categoryList) {
			int categoryId = category.getCategoryId();

			List<Type> typeList;

			try {
				typeList = typeService.getTypeList(categoryId);
			} catch (ServiceException e) {
				logger.error("Exception", e);

				throw new ActionException(e);
			}

			for (Type type : typeList) {
				int typeId = type.getTypeId();

				List<Status> statusList;

				try {
					statusList = statusService.getStatusList(categoryId,
							typeId, StatusPhase.collected, 0,
							filteringStatusSize);
				} catch (ServiceException e) {
					logger.error("Exception", e);

					throw new ActionException(e);
				}

				int statusSize = statusList.size();

				if (statusSize < filteringStatusSize) {
					return;
				}

				logger.debug(String
						.format("Begin to filter statuses, categoryId = %s, typeId = %s, statusSize = %s",
								categoryId, typeId, statusSize));

				statusSize = 0;

				for (Status status : statusList) {
					boolean similarStatusExisting = isSimilarStatusExisting(
							categoryId, typeId, status);

					if (!similarStatusExisting) {
						try {
							statusService.moveStatus(categoryId, typeId,
									StatusPhase.collected,
									StatusPhase.filtered, status);

							statusSize++;
						} catch (ServiceException e) {
							logger.error("Exception", e);

							throw new ActionException(e);
						}
					} else {
						try {
							statusService.deleteStatus(categoryId, typeId,
									StatusPhase.collected, status.getId());
						} catch (ServiceException e) {
							logger.error("Exception", e);

							throw new ActionException(e);
						}
					}
				}

				logger.debug(String
						.format("End to filter statuses, categoryId = %s, typeId = %s, statusSize = %s",
								categoryId, typeId, statusSize));
			}
		}
	}

	public void collectAndFilterStatuses() {
		collectStatuses();

		filterStatuses();
	}

	private byte[] getTextBytes(String text) {
		byte[] textBytes;

		try {
			textBytes = text.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		}

		return textBytes;
	}

	private byte[] getRemoteFileBytes(String remoteFile) {
		byte[] remoteFileBytes;

		try {
			URL url = new URL(remoteFile);

			remoteFileBytes = IOUtils.toByteArray(url);
		} catch (MalformedURLException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		} catch (IOException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		}

		return remoteFileBytes;
	}

	private void transferStatusToVdisk(HttpClient httpClient, int categoryId,
			int typeId, Status status, String transferedName) {
		String statusText = status.getStatusText();
		String statusPictureFile = status.getStatusPictureFile();

		byte[] statusTextBytes = getTextBytes(statusText);
		byte[] statusPictureFileBytes = getRemoteFileBytes(statusPictureFile);

		String statusPictureFileExt = FilenameUtils
				.getExtension(statusPictureFile);

		if (statusPictureFileExt.equals("")) {
			statusPictureFileExt = "jpg";
		}

		String destStatusPathPrefix = "/weibocontentlib/category" + categoryId
				+ "/type" + typeId + "/" + transferedName + ".";

		String destStatusTextFile = destStatusPathPrefix + "txt";
		String destStatusPictureFile = destStatusPathPrefix
				+ statusPictureFileExt;

		try {
			vdiskHandler.addBytes(httpClient, statusTextBytes,
					destStatusTextFile);
			vdiskHandler.addBytes(httpClient, statusPictureFileBytes,
					destStatusPictureFile);
		} catch (HandlerException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		}
	}

	private void transferStatusToSaeStorage(HttpClient httpClient, int typeId,
			SaeStorage saeStorage, Status status, String transferedName) {
		String statusText = status.getStatusText();
		String statusPictureFile = status.getStatusPictureFile();

		byte[] statusTextBytes = getTextBytes(statusText);
		byte[] statusPictureFileBytes = getRemoteFileBytes(statusPictureFile);

		String statusPictureFileExt = FilenameUtils
				.getExtension(statusPictureFile);

		if (statusPictureFileExt.equals("")) {
			statusPictureFileExt = "jpg";
		}

		String destStatusPathPrefix = "/contentlib/type" + typeId + "/"
				+ transferedName + ".";

		String destStatusTextFile = destStatusPathPrefix + "txt";
		String destStatusPictureFile = destStatusPathPrefix
				+ statusPictureFileExt;

		try {
			saeStorageHandler.addBytes(httpClient, saeStorage, statusTextBytes,
					destStatusTextFile);
			saeStorageHandler.addBytes(httpClient, saeStorage,
					statusPictureFileBytes, destStatusPictureFile);
		} catch (HandlerException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		}
	}

	public void transferStatuses() {
		List<Category> categoryList;

		try {
			categoryList = categoryService.getCategoryList();
		} catch (ServiceException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		}

		for (Category category : categoryList) {
			int categoryId = category.getCategoryId();

			List<Type> typeList;

			try {
				typeList = typeService.getTypeList(categoryId);
			} catch (ServiceException e) {
				logger.error("Exception", e);

				throw new ActionException(e);
			}

			for (Type type : typeList) {
				int typeId = type.getTypeId();

				TransferingUser transferingUser;

				try {
					transferingUser = transferingUserService
							.getTransferingUser(categoryId, typeId);
				} catch (ServiceException e) {
					logger.error("Exception", e);

					throw new ActionException(e);
				}

				List<Status> statusList;

				int transferingIndex = transferingUser.getTransferingIndex();

				try {
					statusList = statusService.getStatusList(categoryId,
							typeId, StatusPhase.verified, transferingIndex,
							transferingStatusSize);
				} catch (ServiceException e) {
					logger.error("Exception", e);

					throw new ActionException(e);
				}

				int statusSize = statusList.size();

				if (statusSize < transferingStatusSize) {
					return;
				}

				transferingIndex = transferingIndex + transferingStatusSize;

				transferingUser.setTransferingIndex(transferingIndex);

				setCookies(transferingDefaultHttpClient,
						transferingUser.getCookies());

				try {
					weiboHandler.refresh(transferingDefaultHttpClient);
				} catch (HandlerException e) {
					continue;
				}

				transferingUser
						.setCookies(getCookies(transferingDefaultHttpClient));

				SaeStorage saeStorage;

				try {
					saeStorage = saeStorageHandler.login(
							transferingDefaultHttpClient, saeStorageAccessKey,
							saeStorageSecretKey);
				} catch (HandlerException e) {
					logger.error("Exception", e);

					throw new ActionException(e);
				}

				logger.debug(String
						.format("Begin to transfer statuses, categoryId = %s, typeId = %s, statusSize = %s",
								categoryId, typeId, statusSize));

				statusSize = 0;

				for (Status status : statusList) {
					String transferedName = UUID.randomUUID().toString();

					transferStatusToVdisk(transferingDefaultHttpClient,
							categoryId, typeId, status, transferedName);

					if (categoryId == 2) {
						transferStatusToSaeStorage(
								transferingDefaultHttpClient, typeId,
								saeStorage, status, transferedName);
					}

					statusSize++;
				}

				logger.debug(String
						.format("End to transfer statuses, categoryId = %s, typeId = %s, statusSize = %s",
								categoryId, typeId, statusSize));

				try {
					transferingUserService.updateTransferingUser(categoryId,
							typeId, transferingUser);
				} catch (ServiceException e) {
					logger.error("Exception", e);

					throw new ActionException(e);
				}
			}
		}
	}

	public void publishStatuses() {
		List<Category> categoryList;

		try {
			categoryList = categoryService.getCategoryList();
		} catch (ServiceException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		}

		for (Category category : categoryList) {
			int categoryId = category.getCategoryId();

			List<Type> typeList;

			try {
				typeList = typeService.getTypeList(categoryId);
			} catch (ServiceException e) {
				logger.error("Exception", e);

				throw new ActionException(e);
			}

			for (Type type : typeList) {
				int typeId = type.getTypeId();

				List<Status> statusList;

				int publishingStatusSize = 1;

				try {
					statusList = statusService.getRandomStatusList(categoryId,
							typeId, StatusPhase.verified, 0,
							publishingStatusSize);
				} catch (ServiceException e) {
					logger.error("Exception", e);

					throw new ActionException(e);
				}

				int statusSize = statusList.size();

				if (statusSize < publishingStatusSize) {
					return;
				}

				List<ApplyingUser> applyingUserList;

				try {
					applyingUserList = applyingUserService.getApplyingUserList(
							categoryId, typeId);
				} catch (ServiceException e) {
					logger.error("Exception", e);

					throw new ActionException(e);
				}

				for (ApplyingUser applyingUser : applyingUserList) {
					setCookies(publishingDefaultHttpClient,
							applyingUser.getCookies());

					try {
						weiboHandler.refresh(publishingDefaultHttpClient);
					} catch (HandlerException e) {
						continue;
					}

					applyingUser
							.setCookies(getCookies(publishingDefaultHttpClient));

					try {
						saeAppBatchhelperHandler
								.authorize(publishingDefaultHttpClient);
					} catch (HandlerException e) {
						logger.error("Exception", e);

						throw new ActionException(e);
					}

					logger.debug(String
							.format("Begin to publish statuses, categoryId = %s, typeId = %s, statusSize = %s",
									categoryId, typeId, statusSize));

					statusSize = 0;

					for (Status status : statusList) {
						try {
							saeAppBatchhelperHandler.publishStatus(
									publishingDefaultHttpClient, status);

							statusSize++;
						} catch (HandlerException e) {
							logger.error("Exception", e);

							throw new ActionException(e);
						}
					}

					logger.debug(String
							.format("End to publish statuses, categoryId = %s, typeId = %s, statusSize = %s",
									categoryId, typeId, statusSize));
				}

				try {
					applyingUserService.updateApplyingUserList(categoryId,
							typeId, applyingUserList);
				} catch (ServiceException e) {
					logger.error("Exception", e);

					throw new ActionException(e);
				}
			}
		}
	}

}
