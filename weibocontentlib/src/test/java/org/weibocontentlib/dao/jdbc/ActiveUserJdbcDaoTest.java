package org.weibocontentlib.dao.jdbc;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.weibocontentlib.dao.ActiveUserDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.ActiveUser;
import org.weibocontentlib.entity.ActiveUserPhase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class ActiveUserJdbcDaoTest {

	@Autowired
	private ActiveUserDao activeUserDao;

	@Test
	public void testGetCookies() throws DaoException {
		ActiveUser activeUser = activeUserDao
				.getActiveUser(ActiveUserPhase.collecting);

		Assert.assertNotNull(activeUser);
	}

}
