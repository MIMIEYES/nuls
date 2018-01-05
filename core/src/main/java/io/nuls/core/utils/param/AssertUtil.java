package io.nuls.core.utils.param;


import io.nuls.core.constant.ErrorCode;
import io.nuls.core.exception.NulsRuntimeException;
import io.nuls.core.utils.str.StringUtils;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Niels
 * @date 2017/9/29
 */
public final class AssertUtil {

    public static void isEquals(Object val1, Object val2, String msg) {
        if (val1 == val2 || (val1 != null && val1.equals(val2))) {
            return;
        }
        throw new NulsRuntimeException(ErrorCode.DATA_ERROR, msg);
    }

    public static void canNotEmpty(Object val, ErrorCode ec) {
        canNotEmpty(val, ec.getMsg());
    }

    public static void canNotEmpty(Object val, String msg) {
        boolean b = false;
        do {
            if (null == val) {
                b = true;
                break;
            }
            if (val instanceof String) {
                b = StringUtils.isBlank(val + "");
                break;
            }
            if (val instanceof List) {
                b = ((List) val).isEmpty();
                break;
            }
            if (val instanceof Map) {
                b = ((Map) val).isEmpty();
                break;
            }
            if (val instanceof String[]) {
                b = ((String[]) val).length == 0;
                break;
            }
            if (val instanceof byte[]) {
                b = ((byte[]) val).length == 0;
                break;
            }
        } while (false);
        if (b) {
            throw new NulsRuntimeException(ErrorCode.NULL_PARAMETER, msg);
        }
    }
}
