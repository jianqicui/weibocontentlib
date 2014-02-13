package org.weibocontentlib.dao;

import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.Operator;

public interface OperatorDao {

	Operator getOperatorByName(String name) throws DaoException;

}
