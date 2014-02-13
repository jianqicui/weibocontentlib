package org.weibocontentlib.dao;

import java.util.List;

import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.Category;

public interface CategoryDao {

	List<Category> getCategoryList() throws DaoException;

}
