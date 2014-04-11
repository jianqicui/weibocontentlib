package org.weibocontentlib.service.impl;

import java.util.List;

import org.weibocontentlib.dao.CollectingUserDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.CollectingUser;
import org.weibocontentlib.service.CollectingUserService;
import org.weibocontentlib.service.exception.ServiceException;

public class CollectingUserServiceImpl implements CollectingUserService {

	private CollectingUserDao collectingUserDao;

	public void setCollectingUserDao(CollectingUserDao collectingUserDao) {
		this.collectingUserDao = collectingUserDao;
	}

	@Override
	public List<CollectingUser> getCollectingUserList(int categoryId, int typeId)
			throws ServiceException {
		try {
			return collectingUserDao.getCollectingUserList(categoryId, typeId);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void updateCollectingUser(int categoryId, int typeId,
			CollectingUser collectingUser) throws ServiceException {
		try {
			collectingUserDao.updateCollectingUser(categoryId, typeId,
					collectingUser);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

}
