package org.weibocontentlib.dao;

import java.util.List;

import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.CollectedUser;

public interface CollectedUserDao {

	List<CollectedUser> getCollectedUserList(int categoryId, int typeId)
			throws DaoException;

	void updateCollectedUser(int categoryId, int typeId,
			CollectedUser collectedUser) throws DaoException;

}
