package org.weibocontentlib.service;

import org.weibocontentlib.entity.Operator;
import org.weibocontentlib.service.exception.ServiceException;

public interface OperatorService {

	Operator getOperatorByName(String name) throws ServiceException;

}
