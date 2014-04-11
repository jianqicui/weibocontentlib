package org.weibocontentlib.service;

import java.util.List;

import org.weibocontentlib.entity.CollectingUser;
import org.weibocontentlib.service.exception.ServiceException;

public interface CollectingUserService {

	List<CollectingUser> getCollectingUserList(int categoryId, int typeId)
			throws ServiceException;

	void updateCollectingUser(int categoryId, int typeId,
			CollectingUser collectingUser) throws ServiceException;

}
