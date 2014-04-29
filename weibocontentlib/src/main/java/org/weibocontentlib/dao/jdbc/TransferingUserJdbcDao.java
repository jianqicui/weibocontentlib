package org.weibocontentlib.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.weibocontentlib.dao.TransferingUserDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.TransferingUser;

public class TransferingUserJdbcDao implements TransferingUserDao {

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private RowMapper<TransferingUser> rowMapper = new TransferingUserRowMapper();

	private class TransferingUserRowMapper implements
			RowMapper<TransferingUser> {

		@Override
		public TransferingUser mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			TransferingUser transferingUser = new TransferingUser();

			try {
				transferingUser.setId(rs.getInt("id"));
				transferingUser.setCookies(rs.getBytes("cookies"));
				transferingUser.setTransferingIndex(rs
						.getInt("transfering_index"));
			} catch (SQLException e) {
				throw e;
			}

			return transferingUser;
		}

	}

	private String getTableName(int categoryId, int typeId) {
		return "category" + categoryId + "_type" + typeId + "_user_transfering";
	}

	@Override
	public TransferingUser getTransferingUser(int categoryId, int typeId)
			throws DaoException {
		String sql = "select id, cookies, transfering_index from "
				+ getTableName(categoryId, typeId) + " order by id";

		try {
			return jdbcTemplate.queryForObject(sql, rowMapper);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public void updateTransferingUser(int categoryId, int typeId,
			TransferingUser transferingUser) throws DaoException {
		String sql = "update "
				+ getTableName(categoryId, typeId)
				+ " set cookies = ?, transfering_index = ?, created_timestamp = ?";

		try {
			jdbcTemplate.update(sql, transferingUser.getCookies(),
					transferingUser.getTransferingIndex(), new Date());
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

}
