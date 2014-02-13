package org.weibocontentlib.dao;

import java.util.List;

import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.Status;
import org.weibocontentlib.entity.StatusPhase;

public interface StatusDao {

	void addStatus(int categoryId, int typeId, StatusPhase statusPhase,
			Status status) throws DaoException;

	int getStatusSize(int categoryId, int typeId, StatusPhase statusPhase)
			throws DaoException;

	List<Status> getStatusList(int categoryId, int typeId,
			StatusPhase statusPhase, int index, int size) throws DaoException;

	boolean isSimilarStatusExisting(int categoryId, int typeId,
			StatusPhase statusPhase, Status status) throws DaoException;

	void deleteStatus(int categoryId, int typeId, StatusPhase statusPhase,
			int id) throws DaoException;

	List<Status> getRandomStatusList(int categoryId, int typeId,
			StatusPhase statusPhase, int index, int size) throws DaoException;

}
