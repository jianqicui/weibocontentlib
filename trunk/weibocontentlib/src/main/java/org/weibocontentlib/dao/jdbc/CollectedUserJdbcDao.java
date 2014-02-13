package org.weibocontentlib.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.weibocontentlib.dao.CollectedUserDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.CollectedUser;

public class CollectedUserJdbcDao implements CollectedUserDao {

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private RowMapper<CollectedUser> rowMapper = new CollectedUserRowMapper();

	private class CollectedUserRowMapper implements RowMapper<CollectedUser> {

		@Override
		public CollectedUser mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			CollectedUser collectedUser = new CollectedUser();

			try {
				collectedUser.setId(rs.getInt("id"));
				collectedUser.setUserId(rs.getString("user_id"));
				collectedUser.setPageSize(rs.getInt("page_size"));
				collectedUser.setPageNo(rs.getInt("page_no"));
			} catch (SQLException e) {
				throw e;
			}

			return collectedUser;
		}

	}

	private String getTableName(int categoryId, int typeId) {
		String tableName = "category" + categoryId + "_type" + typeId
				+ "_user_collected";

		return tableName;
	}

	@Override
	public List<CollectedUser> getCollectedUserList(int categoryId, int typeId)
			throws DaoException {
		String sql = "select id, user_id, page_size, page_no from "
				+ getTableName(categoryId, typeId);

		try {
			return jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public void updateCollectedUser(int categoryId, int typeId,
			CollectedUser collectedUser) throws DaoException {
		String sql = "update " + getTableName(categoryId, typeId)
				+ " set user_id = ?, page_size = ?, page_no = ? where id = ?";

		try {
			jdbcTemplate.update(sql, collectedUser.getUserId(),
					collectedUser.getPageSize(), collectedUser.getPageNo(),
					collectedUser.getId());
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

}
