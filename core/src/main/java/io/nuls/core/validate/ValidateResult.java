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
package io.nuls.core.validate;

import io.nuls.core.chain.entity.Result;
import io.nuls.core.constant.ErrorCode;
import io.nuls.core.constant.SeverityLevelEnum;
import io.nuls.core.utils.log.Log;

/**
 * @author Niels
 * @date 2017/11/16
 */
public class ValidateResult<T> extends Result<T> {

    private SeverityLevelEnum level;

    public static ValidateResult getFailedResult(String msg) {
        return getFailedResult(SeverityLevelEnum.WRONG, msg);
    }

    public static ValidateResult getFailedResult(SeverityLevelEnum level, String msg) {
        ValidateResult result = new ValidateResult();
        result.setSuccess(false);
        result.setErrorCode(ErrorCode.VERIFICATION_FAILD);
        result.setMessage(msg);
        result.setLevel(level);
        return result;
    }

    public static ValidateResult getSuccessResult() {
        ValidateResult result = new ValidateResult();
        result.setSuccess(true);
        result.setMessage("");
        return result;
    }

    public static ValidateResult getFailedResult(ErrorCode msg) {
        return getFailedResult(SeverityLevelEnum.WRONG, msg);
    }

    public static ValidateResult getFailedResult(SeverityLevelEnum level, ErrorCode errorCode) {
        ValidateResult result = new ValidateResult();
        result.setSuccess(false);
        result.setLevel(level);
        result.setErrorCode(errorCode);
        return result;
    }

    public SeverityLevelEnum getLevel() {
        return level;
    }

    public void setLevel(SeverityLevelEnum level) {
        this.level = level;
    }
}
