package org.weibocontentlib.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.weibocontentlib.api.exception.ApiException;
import org.weibocontentlib.entity.Category;
import org.weibocontentlib.service.CategoryService;
import org.weibocontentlib.service.exception.ServiceException;

@Controller
public class CategoryApiResource extends BaseApiResource {

	private static final Logger logger = LoggerFactory
			.getLogger(CategoryApiResource.class);

	@Autowired
	private CategoryService categoryService;

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@RequestMapping(value = "/categories", method = RequestMethod.GET)
	public ResponseEntity<List<Category>> getCategories() {
		List<Category> categoryList;

		try {
			categoryList = categoryService.getCategoryList();
		} catch (ServiceException e) {
			logger.error("Exception", e);

			throw new ApiException(e, HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return new ResponseEntity<List<Category>>(categoryList, HttpStatus.OK);
	}

}
