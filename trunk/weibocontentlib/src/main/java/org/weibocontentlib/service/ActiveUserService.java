package org.weibocontentlib.service;

import java.util.List;

import org.weibocontentlib.entity.ActiveUser;
import org.weibocontentlib.entity.ActiveUserPhase;
import org.weibocontentlib.service.exception.ServiceException;

public interface ActiveUserService {

	ActiveUser getActiveUser(ActiveUserPhase activeUserPhase)
			throws ServiceException;

	void updateActiveUser(ActiveUserPhase activeUserPhase, ActiveUser activeUser)
			throws ServiceException;

	List<ActiveUser> getActiveUserList(int categoryId, int typeId,
			ActiveUserPhase activeUserPhase) throws ServiceException;

	void updateActiveUserList(int categoryId, int typeId,
			ActiveUserPhase activeUserPhase, List<ActiveUser> activeUserList)
			throws ServiceException;

}
