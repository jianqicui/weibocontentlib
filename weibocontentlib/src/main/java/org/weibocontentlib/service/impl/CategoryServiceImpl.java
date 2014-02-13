package org.weibocontentlib.service.impl;

import java.util.List;

import org.weibocontentlib.dao.CategoryDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.Category;
import org.weibocontentlib.service.CategoryService;
import org.weibocontentlib.service.exception.ServiceException;

public class CategoryServiceImpl implements CategoryService {

	private CategoryDao categoryDao;

	public void setCategoryDao(CategoryDao categoryDao) {
		this.categoryDao = categoryDao;
	}

	@Override
	public List<Category> getCategoryList() throws ServiceException {
		try {
			return categoryDao.getCategoryList();
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

}
