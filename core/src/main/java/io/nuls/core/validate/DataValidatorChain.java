/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.core.validate;

import io.nuls.core.chain.entity.BaseNulsData;
import io.nuls.core.utils.log.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Niels
 * @date 2017/11/16
 */
public class DataValidatorChain {

    private List<NulsDataValidator<BaseNulsData>> list = new ArrayList<>();
    private Set<Class> classSet = new HashSet<>();
    private ThreadLocal<Integer> index = new ThreadLocal<>();

    public ValidateResult startDoValidator(BaseNulsData data) {
        if (list.isEmpty()) {
            return ValidateResult.getSuccessResult();
        }
        index.set(-1);
        ValidateResult result = doValidate(data);
        boolean b = index.get() == list.size();
        index.remove();
        if (b) {
            return ValidateResult.getSuccessResult();
        } else if (!b && result.isSuccess()) {
            return ValidateResult.getFailedResult("The Validators not fully executed`");
        }
        return result;
    }

    private ValidateResult doValidate(BaseNulsData data) {
        index.set(1 + index.get());
        if (index.get() == list.size()) {
            return ValidateResult.getSuccessResult();
        }
        NulsDataValidator<BaseNulsData> validator = list.get(index.get());
        ValidateResult result = validator.validate(data);
        if (null == result) {
            Log.error(validator.getClass() + " has null result!");
        }
        if (!result.isSuccess()) {
            return result;
        }
        return this.doValidate(data);
    }

    public void addValidator(NulsDataValidator validator) {
        if (null == validator) {
            return;
        }
        if (classSet.add(validator.getClass())) {
            list.add(validator);
        }
    }
}
