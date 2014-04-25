package org.weibocontentlib.service;

import org.weibocontentlib.entity.TransferingUser;
import org.weibocontentlib.service.exception.ServiceException;

public interface TransferingUserService {

	TransferingUser getTransferingUser(int categoryId, int typeId)
			throws ServiceException;

	void updateTransferingUser(int categoryId, int typeId,
			TransferingUser transferingUser) throws ServiceException;

}
