package org.weibocontentlib.service;

import java.util.List;

import org.weibocontentlib.entity.Type;
import org.weibocontentlib.service.exception.ServiceException;

public interface TypeService {

	List<Type> getTypeList(int categoryId) throws ServiceException;

}
