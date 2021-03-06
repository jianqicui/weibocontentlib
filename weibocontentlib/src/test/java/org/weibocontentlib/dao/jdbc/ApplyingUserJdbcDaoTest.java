package org.weibocontentlib.dao.jdbc;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.weibocontentlib.dao.ApplyingUserDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.ApplyingUser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class ApplyingUserJdbcDaoTest {

	@Autowired
	private ApplyingUserDao applyingUserDao;

	@Test
	public void testGetApplyingUserList() throws DaoException {
		int categoryId = 1;
		int typeId = 1;

		List<ApplyingUser> applyingUserList = applyingUserDao
				.getApplyingUserList(categoryId, typeId);

		Assert.assertNotNull(applyingUserList);
	}

}
