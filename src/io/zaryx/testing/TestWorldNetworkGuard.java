package io.zaryx.testing;

import java.security.Permission;

/** JDK 11 backstop: allow incoming game clients, refuse outbound integrations. */
public final class TestWorldNetworkGuard extends SecurityManager {
    @Override public void checkPermission(Permission permission) {
        if(permission instanceof RuntimePermission && permission.getName().equals("setSecurityManager"))
            throw new SecurityException("The test world's network guard cannot be disabled.");
    }
    @Override public void checkPermission(Permission permission,Object context){checkPermission(permission);}
    @Override public void checkConnect(String host,int port){
        // Windows selectors create a private JDK loopback pipe for wakeups.
        if (host.equals("127.0.0.1") || host.equals("::1")) {
            for (Class<?> frame : getClassContext()) {
                if (frame.getClassLoader() == null && frame.getName().equals("sun.nio.ch.PipeImpl$Initializer$LoopbackConnector")) return;
            }
        }
        if(port!=-1)throw new SecurityException("Outbound network connections are disabled on World 2.");
    }
    @Override public void checkConnect(String host,int port,Object context){checkConnect(host,port);}
}
