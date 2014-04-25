package org.weibocontentlib.service.impl;

import org.weibocontentlib.dao.QueryingUserDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.QueryingUser;
import org.weibocontentlib.service.QueryingUserService;
import org.weibocontentlib.service.exception.ServiceException;

public class QueryingUserServiceImpl implements QueryingUserService {

	private QueryingUserDao queryingUserDao;

	public void setQueryingUserDao(QueryingUserDao queryingUserDao) {
		this.queryingUserDao = queryingUserDao;
	}

	@Override
	public QueryingUser getQueryingUser() throws ServiceException {
		try {
			return queryingUserDao.getQueryingUser();
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void updateQueryingUser(QueryingUser queryingUser)
			throws ServiceException {
		try {
			queryingUserDao.updateQueryingUser(queryingUser);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

}
