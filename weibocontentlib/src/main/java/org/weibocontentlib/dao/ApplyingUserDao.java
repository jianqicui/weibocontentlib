package org.weibocontentlib.dao;

import java.util.List;

import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.ApplyingUser;

public interface ApplyingUserDao {

	List<ApplyingUser> getApplyingUserList(int categoryId, int typeId)
			throws DaoException;

	void updateApplyingUserList(int categoryId, int typeId,
			List<ApplyingUser> applyingUserList) throws DaoException;

}
