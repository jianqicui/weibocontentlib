package org.weibocontentlib.dao.jdbc;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.weibocontentlib.dao.StatusDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.Status;
import org.weibocontentlib.entity.StatusPhase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class StatusJdbcDaoTest {

	@Autowired
	private StatusDao statusDao;

	@Test
	public void testGetStatusList() throws DaoException {
		int categoryId = 1;
		int typeId = 1;
		StatusPhase statusPhase = StatusPhase.collected;
		int index = 0;
		int size = 10;

		List<Status> statusList = statusDao.getStatusList(categoryId, typeId,
				statusPhase, index, size);

		Assert.assertNotNull(statusList);
	}

}
