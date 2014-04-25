package org.weibocontentlib.dao.jdbc;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.weibocontentlib.dao.TransferingUserDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.TransferingUser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class TransferingUserJdbcDaoTest {

	@Autowired
	private TransferingUserDao transferingUserDao;

	@Test
	public void testGetTransferingUser() throws DaoException {
		int categoryId = 1;
		int typeId = 1;

		TransferingUser transferingUser = transferingUserDao
				.getTransferingUser(categoryId, typeId);

		Assert.assertNotNull(transferingUser);
	}

}
