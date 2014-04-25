package org.weibocontentlib.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.weibocontentlib.entity.Status;
import org.weibocontentlib.entity.StatusPhase;
import org.weibocontentlib.service.StatusService;
import org.weibocontentlib.service.exception.ServiceException;

@Controller
public class StatusApiResource {

	private static final Logger logger = LoggerFactory
			.getLogger(StatusApiResource.class);

	@Autowired
	private StatusService statusService;

	public void setStatusService(StatusService statusService) {
		this.statusService = statusService;
	}

	@RequestMapping(value = "/categories/{categoryId}/types/{typeId}/statuses/size", method = RequestMethod.GET)
	public ResponseEntity<?> getStatusSize(
			@PathVariable("categoryId") int categoryId,
			@PathVariable("typeId") int typeId,
			@RequestParam("statusPhase") StatusPhase statusPhase) {
		int statusSize;

		try {
			statusSize = statusService.getStatusSize(categoryId, typeId,
					statusPhase);
		} catch (ServiceException e) {
			logger.error("Exception", e);

			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<String>(String.valueOf(statusSize),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/categories/{categoryId}/types/{typeId}/statuses", method = RequestMethod.GET)
	public ResponseEntity<?> getStatuses(
			@PathVariable("categoryId") int categoryId,
			@PathVariable("typeId") int typeId,
			@RequestParam("statusPhase") StatusPhase statusPhase,
			@RequestParam("pageNo") int pageNo,
			@RequestParam("pageSize") int pageSize) {
		List<Status> statusList;

		int index = pageNo * pageSize;
		int size = pageSize;

		try {
			statusList = statusService.getStatusList(categoryId, typeId,
					statusPhase, index, size);
		} catch (ServiceException e) {
			logger.error("Exception", e);

			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<Status>>(statusList, HttpStatus.OK);
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/categories/{categoryId}/types/{typeId}/statuses/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateStatus(
			@PathVariable("categoryId") int categoryId,
			@PathVariable("typeId") int typeId, @PathVariable("id") int id,
			@RequestParam("fromStatusPhase") StatusPhase fromStatusPhase,
			@RequestParam("toStatusPhase") StatusPhase toStatusPhase,
			@RequestBody Status status) {
		status.setId(id);
		
		try {
			statusService.moveStatus(categoryId, typeId, fromStatusPhase,
					toStatusPhase, status);
		} catch (ServiceException e) {
			logger.error("Exception", e);

			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/categories/{categoryId}/types/{typeId}/statuses/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteStatus(
			@PathVariable("categoryId") int categoryId,
			@PathVariable("typeId") int typeId, @PathVariable("id") int id,
			@RequestParam("statusPhase") StatusPhase statusPhase) {
		try {
			statusService.deleteStatus(categoryId, typeId, statusPhase, id);
		} catch (ServiceException e) {
			logger.error("Exception", e);

			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

}
