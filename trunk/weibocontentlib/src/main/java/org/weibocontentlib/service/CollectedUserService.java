package org.weibocontentlib.service;

import java.util.List;

import org.weibocontentlib.entity.CollectedUser;
import org.weibocontentlib.service.exception.ServiceException;

public interface CollectedUserService {

	List<CollectedUser> getCollectedUserList(int categoryId, int typeId)
			throws ServiceException;

	void updateCollectedUser(int categoryId, int typeId,
			CollectedUser collectedUser) throws ServiceException;

}
