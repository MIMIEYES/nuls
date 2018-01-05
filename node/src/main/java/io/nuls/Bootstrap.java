package io.nuls;


import io.nuls.core.constant.ErrorCode;
import io.nuls.core.constant.NulsConstant;
import io.nuls.core.context.NulsContext;
import io.nuls.core.exception.NulsException;
import io.nuls.core.exception.NulsRuntimeException;
import io.nuls.core.i18n.I18nUtils;
import io.nuls.core.module.manager.ModuleManager;
import io.nuls.core.module.service.ModuleService;
import io.nuls.core.utils.cfg.ConfigLoader;
import io.nuls.core.utils.log.Log;
import io.nuls.jettyserver.JettyServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * System start class
 *
 * @author Niels
 */
public class Bootstrap {
    private static final ModuleService moduleService = ModuleService.getInstance();

    public static void main(String[] args) {
        Thread.currentThread().setName("Nuls");
        try {
            sysStart();
//            webStart();
        } catch (Exception e) {
            Log.error(e);
            System.exit(1);
        }
    }

    private static void sysStart() {
        do {
            //load nuls.ini
            try {
                ConfigLoader.loadIni(NulsConstant.USER_CONFIG_FILE);
            } catch (IOException e) {
                Log.error("Client start faild", e);
                throw new NulsRuntimeException(ErrorCode.FAILED, "Client start faild");
            }
            //set system language
            try {
                NulsContext.DEFAULT_ENCODING = ConfigLoader.getCfgValue(NulsConstant.CFG_SYSTEM_SECTION, NulsConstant.CFG_SYSTEM_DEFAULT_ENCODING);
                String language = ConfigLoader.getCfgValue(NulsConstant.CFG_SYSTEM_SECTION, NulsConstant.CFG_SYSTEM_LANGUAGE);
                I18nUtils.setLanguage(language);
            } catch (NulsException e) {
                Log.error(e);
            }
            try {
                Properties bootstrapClasses = ConfigLoader.loadProperties(NulsConstant.SYSTEM_CONFIG_FILE);
                initModules(bootstrapClasses);
            } catch (IOException e) {
                Log.error(e);
                throw new NulsRuntimeException(ErrorCode.FAILED, "Client start faild");
            }
        } while (false);
        while (true) {
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                Log.error(e);
            }
            Log.info(ModuleManager.getInstance().getInfo());
            Log.info("--------------------------------------------");
        }
    }

    private static void webStart() {
        JettyServer.init();
    }

    private static void initModules(Properties bootstrapClasses) {
        List<String> keyList = new ArrayList<>(bootstrapClasses.stringPropertyNames());
        for (String key : keyList) {
            try {
                moduleService.startModule(key, bootstrapClasses.getProperty(key));
            } catch (Exception e) {
                throw new NulsRuntimeException(e);
            }
        }
    }


}
