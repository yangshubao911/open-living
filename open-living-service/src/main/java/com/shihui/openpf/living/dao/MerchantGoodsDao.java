package com.shihui.openpf.living.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.shihui.openpf.living.entity.MerchantGoods;

/**
 * Created by zhoutc on 2016/1/27.
 */
@Repository
public class MerchantGoodsDao extends AbstractDao<MerchantGoods> {
	
	/**
	 * 批量保存
	 * @param list
	 * @return
	 * @throws SQLException
	 */
	public int batchSave(List<MerchantGoods> list) throws SQLException {
		String sql = "insert into merchant_goods(merchant_id, category_id, status, service_id, goods_id, settlement) values(?,?,?,?,?,?)";
		DataSource dataSource = this.jdbcTemplate.getDataSource();
		int result = 0;
		try (Connection conn = dataSource.getConnection()) {
			conn.setAutoCommit(false);
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				for (MerchantGoods mg : list) {
                    ps.setInt(1, mg.getMerchantId());
                    ps.setInt(2, mg.getCategoryId());
                    ps.setInt(3, mg.getStatus());
                    ps.setInt(4, mg.getServiceId());
                    ps.setLong(5, mg.getGoodsId());
                    ps.setString(6, mg.getSettlement());
                    
					ps.addBatch();
				}
				int[] resultarr = ps.executeBatch();

				for (int i = 0; i < resultarr.length; i++) {
					result += resultarr[i];
				}
				conn.commit();
			} catch(SQLException e){
				 conn.rollback();
				 throw e;
			 } finally {
				conn.setAutoCommit(true);
			}
			return result;
		} catch (SQLException e) {
			throw e;
		}
	}
	
	/**
	 * 批量更新
	 * @param list
	 * @return
	 * @throws SQLException
	 */
	public int batchUpdate(List<MerchantGoods> list) throws SQLException {
		String sql = "update merchant_goods set status=?,settlement=? where merchant_id=? and service_id=? and goods_id=?";
		DataSource dataSource = this.jdbcTemplate.getDataSource();
		int result = 0;
		try (Connection conn = dataSource.getConnection()) {
			conn.setAutoCommit(false);
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				for (MerchantGoods mg : list) {
                    ps.setInt(1, mg.getStatus());
                    ps.setString(2, mg.getSettlement());
                    ps.setInt(3, mg.getMerchantId());
                    ps.setInt(4, mg.getServiceId());
                    ps.setLong(5, mg.getGoodsId());
                    
					ps.addBatch();
				}
				int[] resultarr = ps.executeBatch();

				for (int i = 0; i < resultarr.length; i++) {
					result += resultarr[i];
				}
				conn.commit();
			} catch(SQLException e){
				 conn.rollback();
				 throw e;
			 }finally {
				conn.setAutoCommit(true);
			}
			return result;
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * 查询商品可提供的商户
	 * @param goodsId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getAvailableMerchant(int goodsId){

		String sql = "select merchant_id from merchant_goods where goods_id = ? and status = 1";
		final List<Integer> result= new ArrayList<>();
		return jdbcTemplate.query(sql, new Object[]{goodsId},new RowMapper<Integer>() {
			@Override
			public Integer mapRow(ResultSet rs, int i) throws SQLException, DataAccessException {
				int merchantId = rs.getInt("merchant_id");
				result.add(merchantId);
				return merchantId;
			}
		});
	}

	public MerchantGoods findbyServiceId(int serviceId) {
		return this.queryForObject("SELECT * FROM merchant_goods WHERE service_id = ?", new Object[]{serviceId});
	}

	public Integer queryMerchantId(int serviceId, int categoryId, long goodsId) {
		return this.queryCount("SELECT merchant_id FROM merchant_goods WHERE service_id = ? AND category_id = ? AND goods_id = ? ",
				new Object[]{serviceId, categoryId, goodsId});
	}
}
