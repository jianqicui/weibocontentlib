package org.weibocontentlib.dao.jdbc;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.weibocontentlib.dao.TypeDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.Type;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class TypeJdbcDaoTest {

	@Autowired
	private TypeDao typeDao;

	@Test
	public void testGetTypeList() throws DaoException {
		int categoryId = 1;

		List<Type> typeList = typeDao.getTypeList(categoryId);

		Assert.assertNotNull(typeList);
	}

}
