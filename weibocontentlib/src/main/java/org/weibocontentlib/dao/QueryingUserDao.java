package org.weibocontentlib.dao;

import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.QueryingUser;

public interface QueryingUserDao {

	QueryingUser getQueryingUser() throws DaoException;

	void updateQueryingUser(QueryingUser queryingUser) throws DaoException;

}
