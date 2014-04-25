$(document).ready(function() {
	showCategories();
});

function showCategories() {
	$('#categoriesUl').html('');
	
	$.ajax({
		async: true, 
		type: 'GET', 
		dataType: 'json', 
		url: 'api/categories', 
		success: function(data, textStatus) {
			for (var i = 0; i < data.length; i++) {
				var category = data[i];
				
				var categoryId = category['categoryId'];
				var categoryName = category['categoryName'];
				
				var categoryHtml = 
					'<li>' + 
						'<a href="javascript:void(0);" onclick="showTypes(' + categoryId + ')">' + categoryName + '</a>' + 
					'</li>';
				
				$('#categoriesUl').append(categoryHtml);
			}
			
			showTypes(1);
		}
	});
}

function setCategoryActive(categoryId) {
	var categoryLis = $('#categoriesUl > li');
	
	for (i = 0; i < categoryLis.size(); i++) {
		var categoryLi = categoryLis.eq(i);
		
		if (i + 1 == categoryId) {
			categoryLi.attr('class', 'active');
		} else {
			categoryLi.removeAttr('class');
		}
	}
}

function showTypes(categoryId) {
	setCategoryActive(categoryId);
	
	$('#typesUl').html('');
	
	$.ajax({
		async: true, 
		type: 'GET', 
		dataType: 'json', 
		url: 'api/categories/' + categoryId + '/types', 
		success: function(data, textStatus) {
			for (var i = 0; i < data.length; i++) {
				var type = data[i];
				
				var typeId = type['typeId'];
				var typeName = type['typeName'];
				
				var typeHtml = 
					'<li>' + 
						'<a href="javascript:void(0);" onclick="showStatuses(' + categoryId + ', ' + typeId + ')">' + 
							'<div style="text-align: center;">' + typeName + '</div>' + 
						'</a>' + 
					'</li>';
				
				$('#typesUl').append(typeHtml);
			}
			
			showStatuses(categoryId, 1);
		}
	});
}

function setTypeActive(typeId) {
	var typeLis = $('#typesUl > li');
	
	for (i = 0; i < typeLis.size(); i++) {
		var typeLi = typeLis.eq(i);
		
		if (i + 1 == typeId) {
			typeLi.attr('class', 'active');
		} else {
			typeLi.removeAttr('class');
		}
	}
}

function getStatusDivId(id) {
	return 'statusDiv' + id;
}

function getStatusSize(categoryId, typeId, statusPhase) {
	var statusSize = 0;
	
	$.ajax({
		async: false, 
		type: 'GET', 
		dataType: 'text', 
		url: 'api/categories/' + categoryId + '/types/' + typeId + '/statuses/size?statusPhase=' + statusPhase, 
		success: function(data, textStatus) {
			statusSize = data;
		}
	});
	
	return statusSize;
}

function showStatuses(categoryId, typeId) {
	setTypeActive(typeId);
	
	$('#statusesDiv').html('');
	
	var statusPhase = 'filtered';
	var pageNo = 0;
	var pageSize = 6;
	
	var statusSize = getStatusSize(categoryId, typeId, statusPhase);
	
	var statusSizeHtml = 
		'<div class="row" style="margin-bottom: 10px;">' + 
			'<div class="col-md-12" style="text-align: center; color: red; font-size: 30px;">' + 
				statusSize
			'</div>' + 
		'</div>';
	
	$('#statusesDiv').append(statusSizeHtml);
	
	$.ajax({
		async: true, 
		type: 'GET', 
		dataType: 'json', 
		url: 'api/categories/' + categoryId + '/types/' + typeId + '/statuses?statusPhase=' + statusPhase + '&pageNo=' + pageNo + '&pageSize=' + pageSize, 
		success: function(data, textStatus) {
			for (var i = 0; i < data.length; i++) {
				var status = data[i];
				
				var id = status['id'];
				var statusText = status['statusText'];
				var statusPictureFile = status['statusPictureFile'];
				
				var statusDivId = getStatusDivId(id);
				
				var statusHtml = 
					'<div id="' + statusDivId + '" class="row" style="margin-bottom: 10px;">' + 
						'<div class="col-md-10">' + 
							'<textarea style="width: 100%; height: 80px;">' + statusText + '</textarea>' + 
						'</div>' + 
						'<div class="col-md-1" style="text-align: center;">' + 
							'<input type="hidden" value="' + statusPictureFile + '" />' + 
							'<a href="javascript:void(0);" onclick="showStatusPictureDiv(\'' + statusPictureFile + '\', ' + id + ')">' + 
								'图片' + 
							'</a>' + 
						'</div>' + 
						'<div class="col-md-1">' + 
							'<button type="button" class="btn btn-primary btn-block disabled" onclick="updateStatus(' + categoryId + ', ' + typeId + ', ' + id + ')">更新</button>' + 
							'<button type="button" class="btn btn-default btn-block disabled" onclick="deleteStatus(' + categoryId + ', ' + typeId + ', ' + id + ')">删除</button>' + 
						'</div>' + 
					'</div>';
				
				$('#statusesDiv').append(statusHtml);
			}
		}
	});
}

function showStatusPictureDiv(statusPictureFile, id) {
	//Show picture
	var statusPictureImg = $('#statusPictureDiv > div > img').eq(0);
	statusPictureImg.removeAttr('src');
	statusPictureImg.attr('src', statusPictureFile);
	
	$('#statusPictureDiv').modal('show');
	
	//Enable buttons
	var statusDivId = getStatusDivId(id);
	
	var buttons = $('#' + statusDivId + ' > div:eq(2) > button');
	
	for (i = 0; i < buttons.size(); i++) {
		var button = buttons.eq(i);
		
		button.removeClass('disabled');
	}
}

function setStatusSize() {
	var statusSize = $('#statusesDiv > div:eq(0) > div').text();
	statusSize--;
	$('#statusesDiv > div:eq(0) > div').text(statusSize);
}

function updateStatus(categoryId, typeId, id) {
	var statusDivId = getStatusDivId(id);
	
	var statusText = $('#' + statusDivId + ' > div:eq(0) > textarea').val();
	statusText = statusText.replace(/"/g, '\\"');
	
	var statusPictureFile = $('#' + statusDivId + ' > div:eq(1) > input').val();
	
	$.ajax({
		async: true, 
		type: 'PUT', 
		contentType: 'application/json', 
		url: 'api/categories/' + categoryId + '/types/' + typeId + '/statuses/' + id + '?fromStatusPhase=filtered&toStatusPhase=verified', 
		data: '{"statusText": "' + statusText + '", "statusPictureFile": "' + statusPictureFile + '"}', 
		success: function(data, textStatus) {
			setStatusSize();
			
			$('#' + statusDivId).remove();
		}
	});
}

function deleteStatus(categoryId, typeId, id) {
	var statusDivId = getStatusDivId(id);
	
	$.ajax({
		async: true, 
		type: 'DELETE', 
		url: 'api/categories/' + categoryId + '/types/' + typeId + '/statuses/' + id + '?statusPhase=filtered', 
		success: function(data, textStatus) {
			setStatusSize();
			
			$('#' + statusDivId).remove();
		}
	});
}