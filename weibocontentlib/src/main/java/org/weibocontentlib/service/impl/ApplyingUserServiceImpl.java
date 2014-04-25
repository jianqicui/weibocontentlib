package org.weibocontentlib.service.impl;

import java.util.List;

import org.weibocontentlib.dao.ApplyingUserDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.ApplyingUser;
import org.weibocontentlib.service.ApplyingUserService;
import org.weibocontentlib.service.exception.ServiceException;

public class ApplyingUserServiceImpl implements ApplyingUserService {

	private ApplyingUserDao applyingUserDao;

	public void setApplyingUserDao(ApplyingUserDao applyingUserDao) {
		this.applyingUserDao = applyingUserDao;
	}

	@Override
	public List<ApplyingUser> getApplyingUserList(int categoryId, int typeId)
			throws ServiceException {
		try {
			return applyingUserDao.getApplyingUserList(categoryId, typeId);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void updateApplyingUserList(int categoryId, int typeId,
			List<ApplyingUser> applyingUserList) throws ServiceException {
		try {
			applyingUserDao.updateApplyingUserList(categoryId, typeId,
					applyingUserList);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

}
