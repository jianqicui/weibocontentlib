package org.weibocontentlib.dao;

import java.util.List;

import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.Type;

public interface TypeDao {

	List<Type> getTypeList(int categoryId) throws DaoException;

}
