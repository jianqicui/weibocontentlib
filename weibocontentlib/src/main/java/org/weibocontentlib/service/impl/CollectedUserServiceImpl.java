package org.weibocontentlib.service.impl;

import java.util.List;

import org.weibocontentlib.dao.CollectedUserDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.CollectedUser;
import org.weibocontentlib.service.CollectedUserService;
import org.weibocontentlib.service.exception.ServiceException;

public class CollectedUserServiceImpl implements CollectedUserService {

	private CollectedUserDao collectedUserDao;

	public void setCollectedUserDao(CollectedUserDao collectedUserDao) {
		this.collectedUserDao = collectedUserDao;
	}

	@Override
	public List<CollectedUser> getCollectedUserList(int categoryId, int typeId)
			throws ServiceException {
		try {
			return collectedUserDao.getCollectedUserList(categoryId, typeId);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void updateCollectedUser(int categoryId, int typeId,
			CollectedUser collectedUser) throws ServiceException {
		try {
			collectedUserDao.updateCollectedUser(categoryId, typeId,
					collectedUser);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

}
