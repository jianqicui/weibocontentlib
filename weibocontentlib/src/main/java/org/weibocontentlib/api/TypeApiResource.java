package org.weibocontentlib.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.weibocontentlib.entity.Type;
import org.weibocontentlib.service.TypeService;
import org.weibocontentlib.service.exception.ServiceException;

@Controller
public class TypeApiResource {

	private static final Logger logger = LoggerFactory
			.getLogger(TypeApiResource.class);

	@Autowired
	private TypeService typeService;

	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}

	@RequestMapping(value = "/categories/{categoryId}/types", method = RequestMethod.GET)
	public ResponseEntity<?> getTypes(@PathVariable("categoryId") int categoryId) {
		List<Type> typeList;

		try {
			typeList = typeService.getTypeList(categoryId);
		} catch (ServiceException e) {
			logger.error("Exception", e);

			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<Type>>(typeList, HttpStatus.OK);
	}

}
