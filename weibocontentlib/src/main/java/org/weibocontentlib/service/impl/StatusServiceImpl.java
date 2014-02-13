package org.weibocontentlib.service.impl;

import java.util.List;

import org.weibocontentlib.dao.StatusDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.Status;
import org.weibocontentlib.entity.StatusPhase;
import org.weibocontentlib.service.StatusService;
import org.weibocontentlib.service.exception.ServiceException;

public class StatusServiceImpl implements StatusService {

	private StatusDao statusDao;

	public void setStatusDao(StatusDao statusDao) {
		this.statusDao = statusDao;
	}

	@Override
	public void addStatus(int categoryId, int typeId, StatusPhase statusPhase,
			Status status) throws ServiceException {
		try {
			statusDao.addStatus(categoryId, typeId, statusPhase, status);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}

	}

	@Override
	public int getStatusSize(int categoryId, int typeId, StatusPhase statusPhase)
			throws ServiceException {
		try {
			return statusDao.getStatusSize(categoryId, typeId, statusPhase);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public List<Status> getStatusList(int categoryId, int typeId,
			StatusPhase statusPhase, int index, int size)
			throws ServiceException {
		try {
			return statusDao.getStatusList(categoryId, typeId, statusPhase,
					index, size);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean isSimilarStatusExisting(int categoryId, int typeId,
			StatusPhase statusPhase, Status status) throws ServiceException {
		try {
			return statusDao.isSimilarStatusExisting(categoryId, typeId,
					statusPhase, status);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void deleteStatus(int categoryId, int typeId,
			StatusPhase statusPhase, int id) throws ServiceException {
		try {
			statusDao.deleteStatus(categoryId, typeId, statusPhase, id);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void moveStatus(int categoryId, int typeId,
			StatusPhase fromStatusPhase, StatusPhase toStatusPhase,
			Status status) throws ServiceException {
		try {
			statusDao.addStatus(categoryId, typeId, toStatusPhase, status);

			statusDao.deleteStatus(categoryId, typeId, fromStatusPhase,
					status.getId());
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public List<Status> getRandomStatusList(int categoryId, int typeId,
			StatusPhase statusPhase, int index, int size)
			throws ServiceException {
		try {
			return statusDao.getRandomStatusList(categoryId, typeId,
					statusPhase, index, size);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

}
