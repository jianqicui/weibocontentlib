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
import org.weibocontentlib.entity.ActiveUser;
import org.weibocontentlib.entity.ActiveUserPhase;
import org.weibocontentlib.entity.Category;
import org.weibocontentlib.entity.CollectedUser;
import org.weibocontentlib.entity.SaeStorage;
import org.weibocontentlib.entity.Status;
import org.weibocontentlib.entity.StatusPhase;
import org.weibocontentlib.entity.Type;
import org.weibocontentlib.handler.SaeAppBatchhelperHandler;
import org.weibocontentlib.handler.SaeStorageHandler;
import org.weibocontentlib.handler.VdiskHandler;
import org.weibocontentlib.handler.WeiboHandler;
import org.weibocontentlib.handler.exception.HandlerException;
import org.weibocontentlib.service.ActiveUserService;
import org.weibocontentlib.service.CategoryService;
import org.weibocontentlib.service.CollectedUserService;
import org.weibocontentlib.service.StatusService;
import org.weibocontentlib.service.TypeService;
import org.weibocontentlib.service.exception.ServiceException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WeiboContentLibAction {

	private static final Logger logger = LoggerFactory
			.getLogger(WeiboContentLibAction.class);

	private ActiveUserService activeUserService;

	private CategoryService categoryService;

	private TypeService typeService;

	private CollectedUserService collectedUserService;

	private StatusService statusService;

	private WeiboHandler weiboHandler;

	private int filteredStatusSize;

	private int transferedStatusSize;

	private VdiskHandler vdiskHandler;

	private SaeStorageHandler saeStorageHandler;

	private String saeStorageAccessKey;

	private String saeStorageSecretKey;

	private SaeAppBatchhelperHandler saeAppBatchhelperHandler;

	private ObjectMapper objectMapper;

	public void setActiveUserService(ActiveUserService activeUserService) {
		this.activeUserService = activeUserService;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}

	public void setCollectedUserService(
			CollectedUserService collectedUserService) {
		this.collectedUserService = collectedUserService;
	}

	public void setStatusService(StatusService statusService) {
		this.statusService = statusService;
	}

	public void setWeiboHandler(WeiboHandler weiboHandler) {
		this.weiboHandler = weiboHandler;
	}

	public void setFilteredStatusSize(int filteredStatusSize) {
		this.filteredStatusSize = filteredStatusSize;
	}

	public void setTransferedStatusSize(int transferedStatusSize) {
		this.transferedStatusSize = transferedStatusSize;
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

	public void setSaeAppBatchhelperHandler(
			SaeAppBatchhelperHandler saeAppBatchhelperHandler) {
		this.saeAppBatchhelperHandler = saeAppBatchhelperHandler;
	}

	public void initialize() {
		objectMapper = new ObjectMapper();
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

		basicHttpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				600000);
		basicHttpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, 600000);

		return new DefaultHttpClient(poolingClientConnectionManager,
				basicHttpParams);
	}

	@SuppressWarnings("unchecked")
	private void setCookies(DefaultHttpClient defaultHttpClient, byte[] cookies) {
		List<Map<String, Object>> list;

		try {
			list = objectMapper.readValue(cookies, List.class);
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
			String path = (String) map.get("path");
			String comment = (String) map.get("comment");
			int version = (int) map.get("version");
			String domain = (String) map.get("domain");
			boolean secure = (boolean) map.get("secure");

			Date expiryDate = null;

			if (map.get("expiryDate") != null) {
				expiryDate = new Date((long) map.get("expiryDate"));
			}

			BasicClientCookie basicClientCookie = new BasicClientCookie(name,
					value);
			basicClientCookie.setPath(path);
			basicClientCookie.setComment(comment);
			basicClientCookie.setVersion(version);
			basicClientCookie.setDomain(domain);
			basicClientCookie.setSecure(secure);
			basicClientCookie.setExpiryDate(expiryDate);

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

	public void collectStatuses() {
		DefaultHttpClient defaultHttpClient = getDefaultHttpClient();

		ActiveUser activeUser;

		ActiveUserPhase activeUserPhase = ActiveUserPhase.collecting;

		try {
			activeUser = activeUserService.getActiveUser(activeUserPhase);
		} catch (ServiceException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		}

		setCookies(defaultHttpClient, activeUser.getCookies());

		boolean successful = false;

		for (int i = 0; i < 10; i++) {
			try {
				weiboHandler.refresh(defaultHttpClient);

				successful = true;

				break;
			} catch (HandlerException e) {
				continue;
			}
		}

		if (!successful) {
			return;
		}

		activeUser.setCookies(getCookies(defaultHttpClient));

		try {
			activeUserService.updateActiveUser(activeUserPhase, activeUser);
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

				int statusSize = 0;

				logger.debug(String
						.format("Begin to collect statuses, categoryId = %s, typeId = %s, statusSize = %s",
								categoryId, typeId, statusSize));

				List<CollectedUser> collectedUserList;

				try {
					collectedUserList = collectedUserService
							.getCollectedUserList(categoryId, typeId);
				} catch (ServiceException e) {
					logger.error("Exception", e);

					throw new ActionException(e);
				}

				for (CollectedUser collectedUser : collectedUserList) {
					String userId = collectedUser.getUserId();
					int pageSize = collectedUser.getPageSize();
					int pageNo = collectedUser.getPageNo();

					int currentPageSize = 0;

					for (int i = 0; i < 10; i++) {
						try {
							currentPageSize = weiboHandler.getPageSize(
									defaultHttpClient, userId);

							break;
						} catch (HandlerException e) {
							continue;
						}
					}

					if (currentPageSize == 0) {
						continue;
					}

					int currentPageNo = getCurrentPageNo(pageSize, pageNo,
							currentPageSize);

					if (currentPageNo > 1) {
						List<Status> statuses = new ArrayList<Status>();

						statusSize = 0;

						logger.debug(String
								.format("Begin to collect statuses, categoryId = %s, typeId = %s, userId = %s, pageSize = %s, pageNo = %s, statusSize = %s",
										categoryId, typeId, userId,
										currentPageSize, currentPageNo,
										statusSize));

						for (int i = 0; i < 10; i++) {
							try {
								statuses = weiboHandler.getStatusListByPageNo(
										defaultHttpClient, userId,
										currentPageNo);

								break;
							} catch (HandlerException e) {
								continue;
							}
						}

						statusSize = statuses.size();

						logger.debug(String
								.format("End to collect statuses, categoryId = %s, typeId = %s, userId = %s, pageSize = %s, pageNo = %s, statusSize = %s",
										categoryId, typeId, userId,
										currentPageSize, currentPageNo,
										statusSize));

						statusList.addAll(statuses);

						currentPageNo--;

						collectedUser.setPageSize(currentPageSize);
						collectedUser.setPageNo(currentPageNo);

						try {
							collectedUserService.updateCollectedUser(
									categoryId, typeId, collectedUser);
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

		defaultHttpClient.getConnectionManager().shutdown();
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

			if (!similarStatusExisting) {
				try {
					similarStatusExisting = statusService
							.isSimilarStatusExisting(categoryId, typeId,
									StatusPhase.transfered, status);
				} catch (ServiceException e) {
					logger.error("Exception", e);

					throw new ActionException(e);
				}
			}
		}

		return similarStatusExisting;
	}

	public void filterStatuses() {
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
							filteredStatusSize);
				} catch (ServiceException e) {
					logger.error("Exception", e);

					throw new ActionException(e);
				}

				int statusSize = statusList.size();

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

		for (int i = 0; i < 10; i++) {
			try {
				vdiskHandler.addBytes(httpClient, statusTextBytes,
						destStatusTextFile);
				vdiskHandler.addBytes(httpClient, statusPictureFileBytes,
						destStatusPictureFile);

				break;
			} catch (HandlerException e) {
				continue;
			}
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

		for (int i = 0; i < 10; i++) {
			try {
				saeStorageHandler.addBytes(httpClient, saeStorage,
						statusTextBytes, destStatusTextFile);
				saeStorageHandler.addBytes(httpClient, saeStorage,
						statusPictureFileBytes, destStatusPictureFile);

				break;
			} catch (HandlerException e) {
				continue;
			}
		}
	}

	public void transferStatuses() {
		DefaultHttpClient defaultHttpClient = getDefaultHttpClient();

		ActiveUser activeUser;

		ActiveUserPhase activeUserPhase = ActiveUserPhase.transfering;

		try {
			activeUser = activeUserService.getActiveUser(activeUserPhase);
		} catch (ServiceException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		}

		setCookies(defaultHttpClient, activeUser.getCookies());

		boolean successful = false;

		for (int i = 0; i < 10; i++) {
			try {
				weiboHandler.refresh(defaultHttpClient);

				successful = true;

				break;
			} catch (HandlerException e) {
				continue;
			}
		}

		if (!successful) {
			return;
		}

		activeUser.setCookies(getCookies(defaultHttpClient));

		try {
			activeUserService.updateActiveUser(activeUserPhase, activeUser);
		} catch (ServiceException e) {
			logger.error("Exception", e);

			throw new ActionException(e);
		}

		SaeStorage saeStorage = null;

		for (int i = 0; i < 10; i++) {
			try {
				saeStorage = saeStorageHandler.login(defaultHttpClient,
						saeStorageAccessKey, saeStorageSecretKey);

				break;
			} catch (HandlerException e) {
				continue;
			}
		}

		if (saeStorage == null) {
			return;
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

				List<Status> statusList;

				try {
					statusList = statusService.getStatusList(categoryId,
							typeId, StatusPhase.verified, 0,
							transferedStatusSize);
				} catch (ServiceException e) {
					logger.error("Exception", e);

					throw new ActionException(e);
				}

				int statusSize = statusList.size();

				logger.debug(String
						.format("Begin to transfer statuses, categoryId = %s, typeId = %s, statusSize = %s",
								categoryId, typeId, statusSize));

				statusSize = 0;

				for (Status status : statusList) {
					String transferedName = UUID.randomUUID().toString();

					transferStatusToVdisk(defaultHttpClient, categoryId,
							typeId, status, transferedName);

					if (categoryId == 2) {
						transferStatusToSaeStorage(defaultHttpClient, typeId,
								saeStorage, status, transferedName);
					}

					try {
						statusService.moveStatus(categoryId, typeId,
								StatusPhase.verified, StatusPhase.transfered,
								status);

						statusSize++;
					} catch (ServiceException e) {
						logger.error("Exception", e);

						throw new ActionException(e);
					}
				}

				logger.debug(String
						.format("End to transfer statuses, categoryId = %s, typeId = %s, statusSize = %s",
								categoryId, typeId, statusSize));
			}
		}

		defaultHttpClient.getConnectionManager().shutdown();
	}

	public void publishStatuses() {
		DefaultHttpClient defaultHttpClient = getDefaultHttpClient();

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

				ActiveUserPhase activeUserPhase = ActiveUserPhase.applying;

				List<ActiveUser> activeUserList;

				try {
					activeUserList = activeUserService.getActiveUserList(
							categoryId, typeId, activeUserPhase);
				} catch (ServiceException e) {
					logger.error("Exception", e);

					throw new ActionException(e);
				}

				for (ActiveUser activeUser : activeUserList) {
					setCookies(defaultHttpClient, activeUser.getCookies());

					boolean successful = false;

					for (int i = 0; i < 10; i++) {
						try {
							weiboHandler.refresh(defaultHttpClient);

							successful = true;

							break;
						} catch (HandlerException e) {
							continue;
						}
					}

					if (!successful) {
						continue;
					}

					activeUser.setCookies(getCookies(defaultHttpClient));

					try {
						saeAppBatchhelperHandler.authorize(defaultHttpClient);
					} catch (HandlerException e) {
						logger.error("Exception", e);

						throw new ActionException(e);
					}

					List<Status> statusList;

					try {
						statusList = statusService.getRandomStatusList(
								categoryId, typeId, StatusPhase.transfered, 0,
								1);
					} catch (ServiceException e) {
						logger.error("Exception", e);

						throw new ActionException(e);
					}

					int statusSize = statusList.size();

					logger.debug(String
							.format("Begin to public statuses, categoryId = %s, typeId = %s, statusSize = %s",
									categoryId, typeId, statusSize));

					statusSize = 0;

					for (Status status : statusList) {
						try {
							saeAppBatchhelperHandler.publishStatus(
									defaultHttpClient, status);

							statusSize++;
						} catch (HandlerException e) {
							logger.error("Exception", e);

							throw new ActionException(e);
						}
					}

					logger.debug(String
							.format("End to public statuses, categoryId = %s, typeId = %s, statusSize = %s",
									categoryId, typeId, statusSize));
				}

				try {
					activeUserService.updateActiveUserList(categoryId, typeId,
							activeUserPhase, activeUserList);
				} catch (ServiceException e) {
					logger.error("Exception", e);

					throw new ActionException(e);
				}
			}
		}

		defaultHttpClient.getConnectionManager().shutdown();
	}

}
