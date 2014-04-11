package org.weibocontentlib.dao;

import java.util.List;

import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.CollectingUser;

public interface CollectingUserDao {

	List<CollectingUser> getCollectingUserList(int categoryId, int typeId)
			throws DaoException;

	void updateCollectingUser(int categoryId, int typeId,
			CollectingUser collectingUser) throws DaoException;

}
