package org.weibocontentlib.service.impl;

import java.util.List;

import org.weibocontentlib.dao.TypeDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.Type;
import org.weibocontentlib.service.TypeService;
import org.weibocontentlib.service.exception.ServiceException;

public class TypeServiceImpl implements TypeService {

	private TypeDao typeDao;

	public void setTypeDao(TypeDao typeDao) {
		this.typeDao = typeDao;
	}

	@Override
	public List<Type> getTypeList(int categoryId) throws ServiceException {
		try {
			return typeDao.getTypeList(categoryId);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

}
