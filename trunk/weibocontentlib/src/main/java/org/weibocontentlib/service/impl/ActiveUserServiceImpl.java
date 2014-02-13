package org.weibocontentlib.service.impl;

import java.util.List;

import org.weibocontentlib.dao.ActiveUserDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.ActiveUser;
import org.weibocontentlib.entity.ActiveUserPhase;
import org.weibocontentlib.service.ActiveUserService;
import org.weibocontentlib.service.exception.ServiceException;

public class ActiveUserServiceImpl implements ActiveUserService {

	private ActiveUserDao activeUserDao;

	public void setActiveUserDao(ActiveUserDao activeUserDao) {
		this.activeUserDao = activeUserDao;
	}

	@Override
	public ActiveUser getActiveUser(ActiveUserPhase activeUserPhase)
			throws ServiceException {
		try {
			return activeUserDao.getActiveUser(activeUserPhase);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void updateActiveUser(ActiveUserPhase activeUserPhase,
			ActiveUser activeUser) throws ServiceException {
		try {
			activeUserDao.updateActiveUser(activeUserPhase, activeUser);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public List<ActiveUser> getActiveUserList(int categoryId, int typeId,
			ActiveUserPhase activeUserPhase) throws ServiceException {
		try {
			return activeUserDao.getActiveUserList(categoryId, typeId,
					activeUserPhase);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void updateActiveUserList(int categoryId, int typeId,
			ActiveUserPhase activeUserPhase, List<ActiveUser> activeUserList)
			throws ServiceException {
		try {
			activeUserDao.updateActiveUserList(categoryId, typeId,
					activeUserPhase, activeUserList);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

}
