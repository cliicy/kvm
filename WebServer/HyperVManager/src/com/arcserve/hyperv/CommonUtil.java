package com.arcserve.hyperv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.log4j.Logger;

public class CommonUtil {
    private static final Logger logger = Logger.getLogger(CommonUtil.class);

    public static byte[] toBytes(int src) {
        return toBytesBE(src);
    }
    
    public static byte[] toBytesLE(int src) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(src);
        return buffer.array();
    }
    
    public static byte[] toBytesBE(int src) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(src);
        return buffer.array();
    }
    
    public static byte[] readFully(InputStream is, int length, boolean readAll) throws IOException {
        byte[] output = {};
        if (length == -1)
            length = Integer.MAX_VALUE;
        int pos = 0;
        while (pos < length) {
            int bytesToRead;
            if (pos >= output.length) { // Only expand when there's no room
                bytesToRead = Math.min(length - pos, output.length + 1024);
                if (output.length < pos + bytesToRead) {
                    output = Arrays.copyOf(output, pos + bytesToRead);
                }
            } else {
                bytesToRead = output.length - pos;
            }
            int cc = is.read(output, pos, bytesToRead);
            if (cc < 0) {
                if (readAll && length != Integer.MAX_VALUE) {
                    throw new EOFException("Detect premature EOF");
                } else {
                    if (output.length != pos) {
                        output = Arrays.copyOf(output, pos);
                    }
                    break;
                }
            }
            pos += cc;
        }
        return output;
    }
    
    public static byte[] concat(byte[]... byteArrays) {
        int length = 0;
        
        for (byte[] b : byteArrays)
            length += b.length;
        
        byte[] res = new byte[length];
        int currIdx = 0;
        
        for (byte[] b : byteArrays) {
            System.arraycopy(b, 0, res, currIdx, b.length);
            currIdx += b.length;
        }
        
        return res;
    }
    
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    
    public static byte[] slice(byte[] src, int start) {
        return slice(src, start, -1);
    }
    
    public static byte[] slice(byte[] src, int start, int length) {
        if (length == -1)
            length = src.length - start;
        byte[] res = new byte[length];
        System.arraycopy(src, start, res, 0, length);
        return res;
    }
    
    public static String encryptUsingD2DCmdUtil(String input) throws IOException {
        Path execPath = Paths.get(System.getenv("D2DSVR_HOME"), "bin", "d2dutil");
        if((System.getenv("D2DSVR_HOME") == null) || (!execPath.toFile().canExecute()))
            throw new IOException("Cannot determint d2dutil cmdline"); 
        
        StringBuilder encrypted = new StringBuilder();
        
        int rc = cmdWithIO(input, encrypted, execPath.toAbsolutePath().toString(), "encrypt");
        if(rc != 0)
            throw new IOException("Cannot encrypt with d2dutil, rc=" + rc); 
        
        return encrypted.toString();
    }
    
    public static int cmd(String... args) {
        return cmdWithIO(null, null, args);
    }
    
    public static int cmdWithIO(String input, StringBuilder redirectedOutput, String... args) {
        try {
            // return nativeSvc.getNativeFacade().executeCommand(cmdstr, true);
            ProcessBuilder builder = new ProcessBuilder(args);
            builder.redirectErrorStream(true);
            Process proc = builder.start();
            
            if (input != null) {
                OutputStream stdin = proc.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
                writer.write(input);
                writer.close();
            }
            
            StringBuilder outputContent = new StringBuilder();
            InputStream stdout = proc.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
            String line;
            while ((line = reader.readLine()) != null) {
                outputContent.append(line);
            }
            reader.close();
            
            if (redirectedOutput != null)
                redirectedOutput.append(outputContent);
                
            int rc = proc.waitFor();
            if (rc != 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("Possible failed cmd, rc: %d\n arguments: ", rc));
                for (String a : args)
                    sb.append(String.format("├ %s", a)); 
                sb.append(String.format("└─Output: %s", outputContent));
                logger.debug(sb.toString());
            }
            return rc;
        } catch (Exception e) {
            logger.error("Failed to run command", e);
            return -1;
        }
    }
    
}
