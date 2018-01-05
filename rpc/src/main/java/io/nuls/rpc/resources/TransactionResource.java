package io.nuls.rpc.resources;

import io.nuls.rpc.entity.RpcResult;
import io.nuls.rpc.resources.form.TxForm;

/**
 *
 * @author Niels
 * @date 2017/9/27
 */
public interface TransactionResource {

    RpcResult load(String hash);

    RpcResult list(String accountAddress,String type);

    RpcResult create(TxForm form);

}
