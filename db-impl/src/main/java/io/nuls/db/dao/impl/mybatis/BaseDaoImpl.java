package io.nuls.db.dao.impl.mybatis;

import io.nuls.db.dao.BaseDao;
import io.nuls.db.dao.impl.mybatis.common.BaseMapper;
import io.nuls.db.dao.impl.mybatis.session.SessionAnnotation;
import io.nuls.db.dao.impl.mybatis.session.SessionManager;
import org.apache.ibatis.session.SqlSession;

import java.io.Serializable;
import java.util.List;

/**
 * @author NielsWang
 * @date 2017/10/24
 * T : the type of Mapper interface
 * K : the type of primary key of Object
 * V : the type of Object
 */
public class BaseDaoImpl<T extends BaseMapper<K, V>, K extends Serializable, V> implements BaseDao<K, V> {
    private Class<T> mapperClass;

    public BaseDaoImpl(Class<T> mapperClass) {
        this.mapperClass = mapperClass;
    }

    private SqlSession getSession() {
        return SessionManager.getSession();
    }

    @SessionAnnotation
    protected T getMapper() {
        return getSession().getMapper(mapperClass);
    }

    @Override
    @SessionAnnotation
    public int save(V o) {
        return getMapper().insert(o);
    }

    @Override
    @SessionAnnotation
    public int saveBatch(List<V> list) {
        return getMapper().batchInsert(list);
    }

    @Override
    @SessionAnnotation
    public int update(V o) {
        return this.getMapper().updateByPrimaryKey(o);
    }

    @Override
    @SessionAnnotation
    public int updateSelective(V o) {
        return this.getMapper().updateByPrimaryKeySelective(o);
    }

    @Override
    public V getByKey(K key) {
        return this.getMapper().selectByPrimaryKey(key);
    }

    @Override
    @SessionAnnotation
    public int deleteByKey(K key) {
        return this.getMapper().deleteByPrimaryKey(key);
    }

    @Override
    public List<V> listAll() {
        return this.getMapper().selectAll();
    }

    @Override
    public Long count() {
        return this.getMapper().countAll();
    }
}
