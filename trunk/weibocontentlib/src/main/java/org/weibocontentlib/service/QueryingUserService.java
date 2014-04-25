package org.weibocontentlib.service;

import org.weibocontentlib.entity.QueryingUser;
import org.weibocontentlib.service.exception.ServiceException;

public interface QueryingUserService {

	QueryingUser getQueryingUser() throws ServiceException;

	void updateQueryingUser(QueryingUser queryingUser) throws ServiceException;

}
