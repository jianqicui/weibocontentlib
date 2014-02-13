package org.weibocontentlib.service;

import java.util.List;

import org.weibocontentlib.entity.Category;
import org.weibocontentlib.service.exception.ServiceException;

public interface CategoryService {

	List<Category> getCategoryList() throws ServiceException;

}
