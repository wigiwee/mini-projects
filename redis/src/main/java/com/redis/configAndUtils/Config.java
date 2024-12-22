package com.redis.configAndUtils;

import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Config {

    public static String dir = "";

    public static String dbfilename = "";

    public static final String CRLF = "\r\n";

    public static final String NIL = "$-1" + CRLF;

    public static int port = 6379;

    public static String role = Roles.MASTER;

    public static String hostName = "";

    public static int hostPort = -1;

    public static String version = "";

    public static boolean isHandshakeComplete = false;

    public static boolean giveAck = false;

    public static int bytesProcessedBySlave = 0;

    public static int bytesProcessedByMaster = 0;

    public static Set<OutputStream> replicas = Collections.synchronizedSet(new HashSet<>());

    public static void printConfig() {
        if(!dir.isEmpty()){
            System.out.println("dir: " + dir);
            System.out.println("dbfile: " + dbfilename);
        }
        System.out.println("server port: " + port);
        System.out.println("role: " + role);
        if (role.equals(Roles.SLAVE)) {
            System.out.println("\t hostname: " + hostName);
            System.out.println("\t hostport: " + hostPort);
        }
        if(!version.isEmpty()){
            System.out.println("version: " + version);
        }
    }
}