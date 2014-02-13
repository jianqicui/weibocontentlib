package org.weibocontentlib.service.impl;

import org.weibocontentlib.dao.OperatorDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.Operator;
import org.weibocontentlib.service.OperatorService;
import org.weibocontentlib.service.exception.ServiceException;

public class OperatorServiceImpl implements OperatorService {

	private OperatorDao operatorDao;

	public void setOperatorDao(OperatorDao operatorDao) {
		this.operatorDao = operatorDao;
	}

	@Override
	public Operator getOperatorByName(String name) throws ServiceException {
		try {
			return operatorDao.getOperatorByName(name);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

}
