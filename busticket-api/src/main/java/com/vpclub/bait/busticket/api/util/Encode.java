package com.vpclub.bait.busticket.api.util;

import org.apache.commons.lang.StringUtils;

import java.io.InputStream;


/**
 * 从不伟大，亦不渺小
 * <p/>
 * Created by Byrdkm17@gmail.com on 2014-07-31
 */
public class Encode extends AbstractEncrypt {

    public Encode(String privateKey) throws Exception {
        InputStream private_data = null;
        int mod = 0;

        if (StringUtils.isNotBlank(privateKey)) {
            if (privateKey.endsWith(".dat")) {
                mod = 1;
            }
            if (privateKey.endsWith(".pem")) {
                mod = 2;
            }
            private_data = Encode.class.getResourceAsStream(privateKey);
        }

        if (mod == 1) {
            loadKey(null, private_data);
        }
        if (mod == 2) {
            loadPEMKey(null, private_data);
        }
    }
}
