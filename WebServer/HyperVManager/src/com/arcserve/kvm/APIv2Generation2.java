package com.arcserve.kvm;

import org.openwsman.Client;

import com.arcserve.winrm.Classes;

public class APIv2Generation2 {
    public static APIContract createAgainstTarget(final Classes classes, final Client targetServer,
        final String serverName) throws Exception {
        /**
         * NOTE: APIv2Generation2 is now deprecated, it's logic is merged into APIv2
         * */
        throw new Exception(HyperVException.ERROR_INVALID_HOST);
    }
}
