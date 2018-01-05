package io.nuls.db.dao;

import io.nuls.core.chain.entity.Result;
import io.nuls.db.entity.AccountPo;

/**
 *
 * @author Niels
 * @date 2017/11/15
 */
public interface AccountDao extends BaseDao<String, AccountPo> {

    AccountPo loadByAddress(String address);

}
