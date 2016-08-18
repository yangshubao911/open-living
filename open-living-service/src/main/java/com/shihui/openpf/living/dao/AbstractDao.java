/**
 *
 */
package com.shihui.openpf.living.dao;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.shihui.commons.ApiLogger;

/**
 * 基于jpa注解简单实现几个常用方法
 * 支持的注解: Id Entity(定义表名，可选) Table(定义表名，可选) Column(定义字段名，可选) Transient
 * 不加注解默认将驼峰转为下划线作为表名或者字段名
 * 依赖spring JdbcTemplate
 * @author zhouqisheng
 *
 * @version 1.0 Created at: 2015年12月14日 上午11:13:05
 */
public abstract class AbstractDao<T> {
    @Resource(name = "livingJdbcTemplate")
	protected JdbcTemplate jdbcTemplate;

	/** 具体操作的实体类对象 */
	protected Class<T> entityClass;

	protected String tableName;

	protected Map<String, String> fieldNameMap;

	protected Map<String, String> idsFieldNameMap;
	
	protected Map<String, Method> readMethodMap;

	protected RowMapper<T> rowMapper;
	

	/**
	 * 构造方法，获取运行时的具体实体对象
	 */
	@SuppressWarnings("unchecked")
	public AbstractDao() {
		Type superclass = getClass().getGenericSuperclass();
		ParameterizedType type = (ParameterizedType) superclass;
		entityClass = (Class<T>) type.getActualTypeArguments()[0];

		Entity entity = entityClass.getAnnotation(Entity.class);
		if (entity != null) {
			tableName = entity.name();
		} else {
			Table table = entityClass.getAnnotation(Table.class);
			if(table != null){
				tableName = table.name();
			}else{
				//把类名转为下划线分割作为表名
				String[] temp = StringUtils.splitByCharacterTypeCamelCase(entityClass.getSimpleName());
				StringBuilder nameBuilder = new StringBuilder();
				for(String s : temp){
					nameBuilder.append(s.toLowerCase()).append("_");
				}
				tableName = nameBuilder.deleteCharAt(nameBuilder.length() - 1).toString();
			}
		}
		
		try {
			this.initialize();
		}  catch (Exception e) {
			throw new RuntimeException(e);
		}

		rowMapper = new BeanPropertyRowMapper<T>(entityClass);
	}

	/**
	 * 初始化bean信息
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	protected void initialize() throws NoSuchFieldException, SecurityException {
		fieldNameMap = new HashMap<>();
		idsFieldNameMap = new HashMap<>();
		readMethodMap = new HashMap<>();
		PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(entityClass);
		for (PropertyDescriptor pd : pds) {
			if (pd.getWriteMethod() != null && pd.getReadMethod() != null) {
				Field field = null;
				field = entityClass.getDeclaredField(pd.getName());
				if(field != null){
					Transient transientAno = field.getAnnotation(Transient.class);
					if (transientAno != null) {
						continue;
					}
					//获得字段名称
					Column columnAno = field.getAnnotation(Column.class);
					String fieldName;
					if (columnAno != null){
						fieldName = columnAno.name();
					} else{
						//驼峰转下划线
						String[] temp = StringUtils.splitByCharacterTypeCamelCase(field.getName());
						StringBuilder nameBuilder = new StringBuilder();
						for(String s : temp){
							nameBuilder.append(s.toLowerCase()).append("_");
						}
						fieldName = nameBuilder.deleteCharAt(nameBuilder.length() - 1).toString();
					}
						

					//查找id
					Id idAno = field.getAnnotation(Id.class);
					if(idAno != null){
						idsFieldNameMap.put(pd.getName(), fieldName);
					} else {
						fieldNameMap.put(pd.getName(), fieldName);
					}
					
					readMethodMap.put(fieldName, pd.getReadMethod());
				}
			}
		}
	}

	/**
	 *保存记录
	 * @param t
	 * @return 返回插入条数
	 */
	public int save(T t) {
		Entity entity = t.getClass().getAnnotation(Entity.class);
		if (entity == null) {
			throw new RuntimeException("unsurpport entity, please check class:" + t.getClass());
		}
		String tableName = entity.name();
		StringBuilder sql = new StringBuilder("insert into `");
		StringBuilder valueStr = new StringBuilder();
		sql.append(tableName).append("`(");
		Field[] fields = t.getClass().getDeclaredFields();
		ArrayList<Object> valus = new ArrayList<Object>();
		try {
			for (Field field : fields) {
				String fieldName = this.fieldNameMap.get(field.getName());
				if(fieldName == null){
					fieldName = this.idsFieldNameMap.get(field.getName());
				}
				if (fieldName == null) {
					continue;
				}
				
				Method method = readMethodMap.get(fieldName);
				Object value = method.invoke(t);
				if (value != null) {
					sql.append("`").append(fieldName).append("`,");
					valueStr.append("?,");
					valus.add(value);
				}
			}

			sql.deleteCharAt(sql.length()-1).append(") values(").append(valueStr.deleteCharAt(valueStr.length()-1))
					.append(")");
ApiLogger.info(" SQL : " + sql.toString());
ApiLogger.info(" SQL : " + valus.toString());
			return this.jdbcTemplate.update(sql.toString(), valus.toArray());
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * 插入一条记录
	 * @param t
	 * @return 返回自增主键值
	 */
	public long insert(T t) {
		final StringBuilder sql = new StringBuilder("insert into `");
		StringBuilder valueStr = new StringBuilder();
		sql.append(tableName).append("`(");
		Field[] fields = t.getClass().getDeclaredFields();
		final ArrayList<Object> valus = new ArrayList<Object>();
		final ArrayList<String> ids = new ArrayList<String>();
		try {
			for (Field field : fields) {
				String fieldName = this.fieldNameMap.get(field.getName());
				if(fieldName == null){
					fieldName = this.idsFieldNameMap.get(field.getName());
				}
				if (fieldName == null) {
					continue;
				}

				Method method = readMethodMap.get(fieldName);
				Object value = method.invoke(t);
				if (value != null) {
					sql.append("`").append(fieldName).append("`,");
					valueStr.append("?,");
					valus.add(value);
				}
			}

			sql.deleteCharAt(sql.length()-1).append(") values(").append(valueStr.deleteCharAt(valueStr.length()-1))
					.append(")");

			KeyHolder keyHolder = new GeneratedKeyHolder();
			ids.addAll(this.idsFieldNameMap.values());
			final String[] id_field = ids.toArray(new String[ids.size()]);
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(),
							id_field);
					int index = 0;
					for (Object param : valus) {
						index++;
						ps.setObject(index, param);
					}
					return ps;
				}
			}, keyHolder);
			return keyHolder.getKey().longValue();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据Id注解字段更新应用
	 * 仅更新不为空字段
	 * @param t
	 * @return
	 */
	public int update(T t) {
		StringBuilder sql = new StringBuilder("update `");
		StringBuilder idStr = new StringBuilder();
		sql.append(tableName).append("` set ");
		Field[] fields = t.getClass().getDeclaredFields();
		ArrayList<Object> valus = new ArrayList<Object>();
		ArrayList<Object> ids = new ArrayList<Object>();
		try {
			for (Field field : fields) {
				boolean isId = false;
				String fieldName = this.fieldNameMap.get(field.getName());
				if(fieldName == null){
					fieldName = this.idsFieldNameMap.get(field.getName());
					isId = true;
				}
				if (fieldName == null) {
					continue;
				}
				
				Method method = readMethodMap.get(fieldName);
				Object value = method.invoke(t);
				if (value != null) {
					if(isId){
						idStr.append("`").append(fieldName).append("`=? and ");
						ids.add(value);
					}else{
						sql.append("`").append(fieldName).append("`=?,");
						valus.add(value);
					}
				}
			}

			sql.deleteCharAt(sql.length()-1).append(" where ").append(idStr.delete(idStr.length() - 5, idStr.length()));
			valus.addAll(ids);
			return this.jdbcTemplate.update(sql.toString(), valus.toArray());
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据bean里设置Id注解删除数据
	 * @param t
	 * @return
	 */
	public int delete(T t) {
		StringBuilder sql = new StringBuilder("delete from `");
		sql.append(tableName).append("` where ");
		Field[] fields = t.getClass().getDeclaredFields();
		ArrayList<Object> valus = new ArrayList<Object>();
		try {
			for (Field field : fields) {
				String fieldName = this.idsFieldNameMap.get(field.getName());
				if (fieldName == null) {
					continue;
				}
				
				Method method = readMethodMap.get(fieldName);
				Object value = method.invoke(t);
				if (value != null) {
					sql.append("`").append(fieldName).append("`=? and ");
					valus.add(value);
				}
			}

			sql.delete(sql.length() - 5, sql.length());
			return this.jdbcTemplate.update(sql.toString(), valus.toArray());
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 查询所有记录
	 * @return
	 */
	public List<T> findAll(){
		return this.jdbcTemplate.query("select * from " + tableName, rowMapper);
	}
	
	/**
	 * 根据bean中Id注解字段查询
	 * @param t
	 * @return
	 */
	public T findById(T t){
		StringBuilder sql = new StringBuilder("select * from `");
		sql.append(this.tableName).append("` where 1=1");
		
		Field[] fields = t.getClass().getDeclaredFields();
		ArrayList<Object> valus = new ArrayList<Object>();
		
		try {
			for (Field field : fields) {
				String fieldName = this.idsFieldNameMap.get(field.getName());
				if (fieldName == null) {
					continue;
				}
				
				Method method = readMethodMap.get(fieldName);
				Object value = method.invoke(t);
				if (value != null) {
					sql.append(" and `").append(fieldName).append("`=?");
					valus.add(value);
				}
			}

			List<T> result =  this.jdbcTemplate.query(sql.toString(), valus.toArray(), rowMapper);
			if(result != null){
				if( result.size() > 1)
					throw new RuntimeException("the result is not unique,plase check the condition!");
				if(result.size() == 1)
					return result.get(0);
			}
			return null;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 将bean实例非空字段转为查询条件查询
	 * @param t
	 * @return
	 */
	public List<T> findByCondition(T t){
		StringBuilder sql = new StringBuilder("select * from `");
		sql.append(this.tableName).append("` where 1=1");
		
		Field[] fields = t.getClass().getDeclaredFields();
		ArrayList<Object> valus = new ArrayList<Object>();
		
		try {
			for (Field field : fields) {
				String fieldName = this.fieldNameMap.get(field.getName());
				if(fieldName == null){
					fieldName = this.idsFieldNameMap.get(field.getName());
				}
				if (fieldName == null) {
					continue;
				}
				
				Method method = readMethodMap.get(fieldName);
				Object value = method.invoke(t);
				if (value != null) {
					sql.append(" and `").append(fieldName).append("`=?");
					valus.add(value);
				}
			}

			return this.jdbcTemplate.query(sql.toString(), valus.toArray(), rowMapper);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 查询
	 * @param sql
	 * @param args
	 * @return
	 */
	public List<T> queryForList(String sql, Object... args){
		return this.jdbcTemplate.query(sql, args, rowMapper);
	}

	/**
	 * 查询
	 * @param sql
	 * @param args
	 * @return
	 */
	public T queryForObject(String sql, Object... args){
		T result;
		try {
			result = this.jdbcTemplate.queryForObject(sql, args, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
		return result;
	}
	
	/**
	 * 统计查询
	 * @param sql
	 * @param args
	 * @return
	 */
	public Integer queryCount(String sql, Object... args){
		int count  = this.jdbcTemplate.query(sql, args, new ResultSetExtractor<Integer>(){

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				if(rs.next())
					return rs.getInt(1);
				return 0;
			}
			
		});
		return count;
	}
	public long queryLongValue(String sql, Object... args){
		long count  = this.jdbcTemplate.query(sql, args, new ResultSetExtractor<Long>(){

			@Override
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				if(rs.next())
					return rs.getLong(1);
				return -1L;
			}
			
		});
		return count;
	}
}
