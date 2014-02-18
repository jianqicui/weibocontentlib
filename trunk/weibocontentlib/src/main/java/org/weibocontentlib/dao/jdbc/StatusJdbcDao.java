package org.weibocontentlib.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.weibocontentlib.dao.StatusDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.Status;
import org.weibocontentlib.entity.StatusPhase;

public class StatusJdbcDao implements StatusDao {

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private String getTableName(int categoryId, int typeId,
			StatusPhase statusPhase) {
		String tableName = "category" + categoryId + "_type" + typeId
				+ "_status_" + statusPhase;

		return tableName;
	}

	private RowMapper<Status> rowMapper = new StatusRowMapper();

	private class StatusRowMapper implements RowMapper<Status> {

		@Override
		public Status mapRow(ResultSet rs, int rowNum) throws SQLException {
			Status status = new Status();

			try {
				status.setId(rs.getInt("id"));
				status.setStatusText(rs.getString("status_text"));
				status.setStatusPictureFile(rs.getString("status_picture_file"));
			} catch (SQLException e) {
				throw e;
			}

			return status;
		}

	}

	@Override
	public void addStatus(int categoryId, int typeId, StatusPhase statusPhase,
			Status status) throws DaoException {
		String sql = "insert into "
				+ getTableName(categoryId, typeId, statusPhase)
				+ " (status_text, status_picture_file) values (?, ?)";

		try {
			jdbcTemplate.update(sql, status.getStatusText(),
					status.getStatusPictureFile());
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public int getStatusSize(int categoryId, int typeId, StatusPhase statusPhase)
			throws DaoException {
		String sql = "select count(*) from "
				+ getTableName(categoryId, typeId, statusPhase);

		try {
			return jdbcTemplate.queryForObject(sql, Integer.class);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public List<Status> getStatusList(int categoryId, int typeId,
			StatusPhase statusPhase, int index, int size) throws DaoException {
		String sql = "select id, status_text, status_picture_file from "
				+ getTableName(categoryId, typeId, statusPhase) + " limit "
				+ index + ", " + size;

		try {
			return jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public boolean isSimilarStatusExisting(int categoryId, int typeId,
			StatusPhase statusPhase, Status status) throws DaoException {
		String sql = "select count(*) from "
				+ getTableName(categoryId, typeId, statusPhase)
				+ " where levenshtein_ratio(?, status_text) > 50";

		int size;

		try {
			size = jdbcTemplate.queryForObject(sql,
					new String[] { status.getStatusText() }, Integer.class);
		} catch (Exception e) {
			throw new DaoException(e);
		}

		return size > 0;
	}

	@Override
	public void deleteStatus(int categoryId, int typeId,
			StatusPhase statusPhase, int id) throws DaoException {
		String sql = "delete from "
				+ getTableName(categoryId, typeId, statusPhase)
				+ " where id = ?";

		try {
			jdbcTemplate.update(sql, id);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public List<Status> getRandomStatusList(int categoryId, int typeId,
			StatusPhase statusPhase, int index, int size) throws DaoException {
		String sql = "select id, status_text, status_picture_file from "
				+ getTableName(categoryId, typeId, statusPhase)
				+ " order by rand() limit " + index + ", " + size;

		try {
			return jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

}
