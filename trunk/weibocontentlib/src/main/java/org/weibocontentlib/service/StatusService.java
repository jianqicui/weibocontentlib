package org.weibocontentlib.service;

import java.util.List;

import org.weibocontentlib.entity.Status;
import org.weibocontentlib.entity.StatusPhase;
import org.weibocontentlib.service.exception.ServiceException;

public interface StatusService {

	void addStatus(int categoryId, int typeId, StatusPhase statusPhase,
			Status status) throws ServiceException;

	int getStatusSize(int categoryId, int typeId, StatusPhase statusPhase)
			throws ServiceException;

	List<Status> getStatusList(int categoryId, int typeId,
			StatusPhase statusPhase, int index, int size)
			throws ServiceException;

	boolean isSimilarStatusExisting(int categoryId, int typeId,
			StatusPhase statusPhase, Status status) throws ServiceException;

	void deleteStatus(int categoryId, int typeId, StatusPhase statusPhase,
			int id) throws ServiceException;

	void moveStatus(int categoryId, int typeId, StatusPhase fromStatusPhase,
			StatusPhase toStatusPhase, Status status) throws ServiceException;

	List<Status> getRandomStatusList(int categoryId, int typeId,
			StatusPhase statusPhase, int index, int size)
			throws ServiceException;

}
