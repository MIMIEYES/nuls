package io.nuls.db.dao.impl.mybatis;

import io.nuls.core.chain.entity.Result;
import io.nuls.core.constant.ErrorCode;
import io.nuls.core.context.NulsContext;
import io.nuls.core.exception.NulsRuntimeException;
import io.nuls.db.dao.*;
import io.nuls.db.dao.impl.mybatis.session.SessionAnnotation;
import io.nuls.db.entity.AccountPo;
import io.nuls.db.entity.AliasPo;
import io.nuls.db.entity.TransactionLocalPo;

import java.util.List;

/**
 * @author vivi
 * @date 2017/12/22.
 */
public class AccountTxDaoImpl implements AccountTxDao {

    private AccountDao accountDao = NulsContext.getInstance().getService(AccountDao.class);

    private AliasDao aliasDao = NulsContext.getInstance().getService(AliasDao.class);

    private TransactionLocalDao txDao = NulsContext.getInstance().getService(TransactionLocalDao.class);

    @SessionAnnotation
    @Override
    public Result setAlias(String address, String alias) {
        try {
            AliasPo aliasPo = new AliasPo();
            aliasPo.setAlias(alias);
            aliasPo.setAddress(address);
            aliasDao.save(aliasPo);

            AccountPo po = new AccountPo();
            po.setId(address);
            po.setAlias(alias);
            accountDao.updateSelective(po);
        } catch (Exception e) {
            throw new NulsRuntimeException(ErrorCode.DB_SAVE_ERROR);
        }
        return new Result(true, "OK");
    }

    @SessionAnnotation
    @Override
    public void importAccount(List<AccountPo> accountPoList) {
        for (AccountPo account : accountPoList) {
            accountDao.save(account);
            for (int i = 0; i < account.getMyTxs().size(); i++) {
                TransactionLocalPo tx = account.getMyTxs().get(i);
                txDao.save(tx);
            }
        }
    }
}
