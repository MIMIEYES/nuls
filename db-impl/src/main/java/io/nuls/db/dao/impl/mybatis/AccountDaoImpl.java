/**
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.db.dao.impl.mybatis;

import io.nuls.core.constant.ErrorCode;
import io.nuls.core.exception.NulsRuntimeException;
import io.nuls.db.dao.AccountDataService;
import io.nuls.db.dao.impl.mybatis.mapper.AccountMapper;
import io.nuls.db.dao.impl.mybatis.params.AccountSearchParams;
import io.nuls.db.dao.impl.mybatis.util.Searchable;
import io.nuls.db.entity.AccountPo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Niels
 * @date 2017/11/21
 */
public class AccountDaoImpl extends BaseDaoImpl<AccountMapper, String, AccountPo> implements AccountDataService {

    public AccountDaoImpl() {
        super(AccountMapper.class);
    }

    @Override
    public AccountPo loadByAddress(String address) {
        Map<String, Object> params = new HashMap<>();
        params.put(AccountSearchParams.SEARCH_FIELD_ADDRESS, address);
        List<AccountPo> list = this.getList(params);
        if (list == null || list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new NulsRuntimeException(ErrorCode.DB_DATA_ERROR, "data error");
        }
        return list.get(0);
    }

    @Override
    public int updateAlias(AccountPo po) {
        return getMapper().updateAlias(po);
    }

    @Override
    protected Searchable getSearchable(Map<String, Object> params) {
        return new AccountSearchParams(params);
    }
}
