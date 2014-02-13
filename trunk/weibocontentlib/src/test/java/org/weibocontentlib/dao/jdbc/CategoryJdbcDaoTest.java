package org.weibocontentlib.dao.jdbc;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.weibocontentlib.dao.CategoryDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.Category;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class CategoryJdbcDaoTest {

	@Autowired
	private CategoryDao categoryDao;

	@Test
	public void testGetCategoryList() throws DaoException {
		List<Category> categoryList = categoryDao.getCategoryList();

		Assert.assertNotNull(categoryList);
	}

}
