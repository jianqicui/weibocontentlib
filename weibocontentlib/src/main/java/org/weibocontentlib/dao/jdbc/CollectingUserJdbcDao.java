package org.weibocontentlib.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.weibocontentlib.dao.CollectingUserDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.CollectingUser;

public class CollectingUserJdbcDao implements CollectingUserDao {

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private RowMapper<CollectingUser> rowMapper = new CollectingUserRowMapper();

	private class CollectingUserRowMapper implements RowMapper<CollectingUser> {

		@Override
		public CollectingUser mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			CollectingUser collectingUser = new CollectingUser();

			try {
				collectingUser.setId(rs.getInt("id"));
				collectingUser.setUserId(rs.getString("user_id"));
				collectingUser.setPageSize(rs.getInt("page_size"));
				collectingUser.setPageNo(rs.getInt("page_no"));
			} catch (SQLException e) {
				throw e;
			}

			return collectingUser;
		}

	}

	private String getTableName(int categoryId, int typeId) {
		String tableName = "category" + categoryId + "_type" + typeId
				+ "_user_collecting";

		return tableName;
	}

	@Override
	public List<CollectingUser> getCollectingUserList(int categoryId, int typeId)
			throws DaoException {
		String sql = "select id, user_id, page_size, page_no from "
				+ getTableName(categoryId, typeId) + " order by id";

		try {
			return jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public void updateCollectingUser(int categoryId, int typeId,
			CollectingUser collectingUser) throws DaoException {
		String sql = "update " + getTableName(categoryId, typeId)
				+ " set user_id = ?, page_size = ?, page_no = ? where id = ?";

		try {
			jdbcTemplate.update(sql, collectingUser.getUserId(),
					collectingUser.getPageSize(), collectingUser.getPageNo(),
					collectingUser.getId());
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

}
