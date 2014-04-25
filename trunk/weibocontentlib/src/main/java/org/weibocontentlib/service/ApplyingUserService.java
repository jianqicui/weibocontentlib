package org.weibocontentlib.service;

import java.util.List;

import org.weibocontentlib.entity.ApplyingUser;
import org.weibocontentlib.service.exception.ServiceException;

public interface ApplyingUserService {

	List<ApplyingUser> getApplyingUserList(int categoryId, int typeId)
			throws ServiceException;

	void updateApplyingUserList(int categoryId, int typeId,
			List<ApplyingUser> applyingUserList) throws ServiceException;

}
