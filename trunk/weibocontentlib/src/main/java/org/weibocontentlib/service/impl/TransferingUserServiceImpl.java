package org.weibocontentlib.service.impl;

import org.weibocontentlib.dao.TransferingUserDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.TransferingUser;
import org.weibocontentlib.service.TransferingUserService;
import org.weibocontentlib.service.exception.ServiceException;

public class TransferingUserServiceImpl implements TransferingUserService {

	private TransferingUserDao transferingUserDao;

	public void setTransferingUserDao(TransferingUserDao transferingUserDao) {
		this.transferingUserDao = transferingUserDao;
	}

	@Override
	public TransferingUser getTransferingUser(int categoryId, int typeId)
			throws ServiceException {
		try {
			return transferingUserDao.getTransferingUser(categoryId, typeId);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void updateTransferingUser(int categoryId, int typeId,
			TransferingUser transferingUser) throws ServiceException {
		try {
			transferingUserDao.updateTransferingUser(categoryId, typeId,
					transferingUser);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

}
