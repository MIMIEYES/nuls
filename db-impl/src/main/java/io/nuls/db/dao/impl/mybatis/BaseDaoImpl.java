package io.nuls.db.dao.impl.mybatis;

import io.nuls.db.dao.BaseDataService;
import io.nuls.db.dao.impl.mybatis.common.BaseMapper;
import io.nuls.db.transactional.annotation.TransactionalAnnotation;
import io.nuls.db.dao.impl.mybatis.session.SessionManager;
import io.nuls.db.dao.impl.mybatis.util.Searchable;
import org.apache.ibatis.session.SqlSession;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author NielsWang
 * @date 2017/10/24
 * T : the type of Mapper interface
 * K : the type of primary key of Object
 * V : the type of Object
 */
public abstract class BaseDaoImpl<T extends BaseMapper<K, V>, K extends Serializable, V> implements BaseDataService<K, V> {
    private Class<T> mapperClass;

    public BaseDaoImpl(Class<T> mapperClass) {
        this.mapperClass = mapperClass;
    }

    private SqlSession getSession() {
        return SessionManager.getSession();
    }

    protected T getMapper() {
        return getSession().getMapper(mapperClass);
    }

    @Override
    @TransactionalAnnotation
    public int save(V o) {
        return getMapper().insert(o);
    }

    @Override
    @TransactionalAnnotation
    public int save(List<V> list) {
        return getMapper().batchInsert(list);
    }

    @Override
    @TransactionalAnnotation
    public int update(V o) {
        return this.getMapper().updateByPrimaryKey(o);
    }

    @Override
    @TransactionalAnnotation
    public int update(List<V> list) {
        int result = 0;
        for (int i = 0; i < list.size(); i++) {
            result += update(list.get(i));
        }
        return result;
    }

    @Override
    public V get(K key) {
        return this.getMapper().selectByPrimaryKey(key);
    }

    @Override
    @TransactionalAnnotation
    public int delete(K key) {
        return this.getMapper().deleteByPrimaryKey(key);
    }

    @Override
    public List<V> getList() {
        return this.getMapper().selectList(null);
    }

    @Override
    public final List<V> getList(Map<String, Object> params) {
        if (null == params || params.isEmpty()) {
            return getList();
        }
        return this.getMapper().selectList(getSearchable(params));
    }

    /**
     * change params to searchable object
     *
     * @param params
     * @return
     */
    protected abstract Searchable getSearchable(Map<String, Object> params);

    @Override
    public Long getCount() {
        return this.getMapper().countAll();
    }
}
