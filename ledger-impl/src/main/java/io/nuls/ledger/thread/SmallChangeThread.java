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
package io.nuls.ledger.thread;

import io.nuls.account.service.intf.AccountService;
import io.nuls.core.context.NulsContext;
import io.nuls.core.utils.log.Log;
import io.nuls.ledger.service.intf.LedgerService;

/**
 * @author Niels
 * @date 2017/11/13
 */
public class SmallChangeThread implements Runnable {
    private LedgerService ledgerService = NulsContext.getInstance().getService(LedgerService.class);
    private AccountService accountService = NulsContext.getInstance().getService(AccountService.class);
    private static final SmallChangeThread INSTANCE = new SmallChangeThread();

    private SmallChangeThread( ) {
    }

    public static SmallChangeThread getInstance() {
        return INSTANCE;
    }

    private boolean stop;

    @Override
    public void run() {
        stop = false;
        while (!stop) {
            try {
                smallChange();
            } catch (Exception e) {
                Log.error(e);
            }
            try {
                Thread.sleep(3600000L);
            } catch (InterruptedException e) {
                Log.error(e);
            }
        }
    }

    private void smallChange() {
        //todo Statistical non spending
//        List<TransactionOutput> list = ledgerService.queryNotSpent(this.accountService.getLocalAccount().getAddress().toString());
//        int count = list.size();
//        if (count >= LedgerConstant.SMALL_CHANGE_COUNT) {
//            this.ledgerService.smallChange(list.subList(0, LedgerConstant.SMALL_CHANGE_COUNT));
//        }
    }

    public void stop() {
        this.stop = true;
    }


}
