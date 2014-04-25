package org.weibocontentlib.dao;

import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.TransferingUser;

public interface TransferingUserDao {

	TransferingUser getTransferingUser(int categoryId, int typeId)
			throws DaoException;

	void updateTransferingUser(int categoryId, int typeId,
			TransferingUser transferingUser) throws DaoException;

}
