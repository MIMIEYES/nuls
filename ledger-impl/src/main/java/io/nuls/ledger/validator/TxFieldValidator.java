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
package io.nuls.ledger.validator;

import io.nuls.core.chain.entity.Transaction;
import io.nuls.core.constant.ErrorCode;
import io.nuls.core.constant.SeverityLevelEnum;
import io.nuls.core.validate.NulsDataValidator;
import io.nuls.core.validate.ValidateResult;

/**
 * @author Niels
 * @date 2017/12/19
 */
public class TxFieldValidator implements NulsDataValidator<Transaction> {
    private static final TxFieldValidator INSTANCE = new TxFieldValidator();

    private TxFieldValidator() {
    }

    public static TxFieldValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public ValidateResult validate(Transaction data) {
        boolean result = true;
        do {
            if (data == null) {
                result = false;
                break;
            }
            if (data.getHash() == null || data.getHash().size() == 0) {
                result = false;
                break;
            }
            if (data.getSign() == null || data.getSign().getSignLength() == 0) {
                result = false;
                break;
            }
            if (data.getType() == 0) {
                result = false;
                break;
            }
            if (data.getTime() == 0) {
                result = false;
                break;
            }
        } while (false);
        if (!result) {
            return ValidateResult.getFailedResult( ErrorCode.DATA_FIELD_CHECK_ERROR);
        }
        return ValidateResult.getSuccessResult();
    }
}
