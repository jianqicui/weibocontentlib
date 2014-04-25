package org.weibocontentlib.dao.jdbc;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.weibocontentlib.dao.QueryingUserDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.QueryingUser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class QueryingUserJdbcDaoTest {

	@Autowired
	private QueryingUserDao querying;

	@Test
	public void testGetQueryingUser() throws DaoException {
		QueryingUser queryingUser = querying.getQueryingUser();

		Assert.assertNotNull(queryingUser);
	}

}
