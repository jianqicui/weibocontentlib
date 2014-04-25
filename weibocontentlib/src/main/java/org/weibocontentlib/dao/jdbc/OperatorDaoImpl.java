package org.weibocontentlib.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.weibocontentlib.dao.OperatorDao;
import org.weibocontentlib.dao.exception.DaoException;
import org.weibocontentlib.entity.Operator;

public class OperatorDaoImpl implements OperatorDao {

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private RowMapper<Operator> rowMapper = new OperatorRowMapper();

	private class OperatorRowMapper implements RowMapper<Operator> {

		@Override
		public Operator mapRow(ResultSet rs, int rowNum) throws SQLException {
			Operator operator = new Operator();

			try {
				operator.setName(rs.getString("name"));
				operator.setPassword(rs.getString("password"));
				operator.setRole(rs.getString("role"));
			} catch (SQLException e) {
				throw e;
			}

			return operator;
		}

	}

	@Override
	public Operator getOperatorByName(String name) throws DaoException {
		String sql = "select name, password, role from operator where name = ? order by id";

		try {
			return jdbcTemplate.queryForObject(sql, rowMapper, name);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

}
