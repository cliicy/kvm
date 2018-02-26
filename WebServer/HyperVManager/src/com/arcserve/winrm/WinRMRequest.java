package com.arcserve.winrm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

import com.arcserve.hyperv.CommonUtil;

import jcifs.util.Base64;
import jcifs.util.HMACT64;
import jcifs.util.Hexdump;
import jcifs.util.RC4;


public abstract class WinRMRequest {
    private static final Logger logger = Logger.getLogger(WinRMRequest.class);
    
    static {        
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }                    
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}                    
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) { logger.error("Cannot init https context", e); }

        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession sess) { return true; }
            
        });

        HttpsURLConnection.setDefaultAllowUserInteraction(false);
        HttpsURLConnection.setFollowRedirects(false);
        HttpURLConnection.setDefaultAllowUserInteraction(false);
        HttpURLConnection.setFollowRedirects(false);

        /**
         * To avoid a rare failure uploding LiveCD on to the HyperV server.
         * Which will happen when the HyperV's hostname resolves to someone else's IP.
         * */
        jcifs.Config.setProperty("jcifs.smb.client.dfs.disabled", "true");
        jcifs.Config.setProperty("jcifs.smb.client.dfs.ttl", "0");  // added to double check we won't waste time in dfs
        /** To make sure we're doing good */
        jcifs.Config.setProperty("jcifs.smb.lmCompatibility", "4");
        jcifs.Config.setProperty("jcifs.smb.client.useExtendedSecurity", "true");
        

    }
    
    private final static byte[]       clientSigningKeyMagic = new byte[] { 0x73, 0x65, 0x73, 0x73, 0x69, 0x6f, 0x6e,
        0x20, 0x6b, 0x65, 0x79, 0x20, 0x74, 0x6f, 0x20, 0x63, 0x6c, 0x69, 0x65, 0x6e, 0x74, 0x2d, 0x74, 0x6f, 0x2d,
        0x73, 0x65, 0x72, 0x76, 0x65, 0x72, 0x20, 0x73, 0x69, 0x67, 0x6e, 0x69, 0x6e, 0x67, 0x20, 0x6b, 0x65, 0x79,
        0x20, 0x6d, 0x61, 0x67, 0x69, 0x63, 0x20, 0x63, 0x6f, 0x6e, 0x73, 0x74, 0x61, 0x6e, 0x74, 0x00 };
    private final static byte[]       serverSigningKeyMagic = new byte[] { 0x73, 0x65, 0x73, 0x73, 0x69, 0x6f, 0x6e,
        0x20, 0x6b, 0x65, 0x79, 0x20, 0x74, 0x6f, 0x20, 0x73, 0x65, 0x72, 0x76, 0x65, 0x72, 0x2d, 0x74, 0x6f, 0x2d,
        0x63, 0x6c, 0x69, 0x65, 0x6e, 0x74, 0x20, 0x73, 0x69, 0x67, 0x6e, 0x69, 0x6e, 0x67, 0x20, 0x6b, 0x65, 0x79,
        0x20, 0x6d, 0x61, 0x67, 0x69, 0x63, 0x20, 0x63, 0x6f, 0x6e, 0x73, 0x74, 0x61, 0x6e, 0x74, 0x00 };
    private final static byte[]       clientSealingKeyMagic = new byte[] { 0x73, 0x65, 0x73, 0x73, 0x69, 0x6f, 0x6e,
        0x20, 0x6b, 0x65, 0x79, 0x20, 0x74, 0x6f, 0x20, 0x63, 0x6c, 0x69, 0x65, 0x6e, 0x74, 0x2d, 0x74, 0x6f, 0x2d,
        0x73, 0x65, 0x72, 0x76, 0x65, 0x72, 0x20, 0x73, 0x65, 0x61, 0x6c, 0x69, 0x6e, 0x67, 0x20, 0x6b, 0x65, 0x79,
        0x20, 0x6d, 0x61, 0x67, 0x69, 0x63, 0x20, 0x63, 0x6f, 0x6e, 0x73, 0x74, 0x61, 0x6e, 0x74, 0x00 };
    private final static byte[]       serverSealingKeyMagic = new byte[] { 0x73, 0x65, 0x73, 0x73, 0x69, 0x6f, 0x6e,
        0x20, 0x6b, 0x65, 0x79, 0x20, 0x74, 0x6f, 0x20, 0x73, 0x65, 0x72, 0x76, 0x65, 0x72, 0x2d, 0x74, 0x6f, 0x2d,
        0x63, 0x6c, 0x69, 0x65, 0x6e, 0x74, 0x20, 0x73, 0x65, 0x61, 0x6c, 0x69, 0x6e, 0x67, 0x20, 0x6b, 0x65, 0x79,
        0x20, 0x6d, 0x61, 0x67, 0x69, 0x63, 0x20, 0x63, 0x6f, 0x6e, 0x73, 0x74, 0x61, 0x6e, 0x74, 0x00 };
    private final static WinRMRequest self               = new WinRMRequest() {};
    private final static String       userAgent             = "Arcserve Instant VM Client for HyperV";
    
    public enum WinRMRequestFailureType {
        WinRMRequestSuccess,
        WinRMRequestConnectionFailure,
        WinRMRequestAuthenticationFailure,
        WinRMRequestUnknownFailure,
        WinRMRequestArgumentFailure
    };
    public abstract class Response {
        public abstract int getStatusCode();
        public abstract String getResponseContent();
        public abstract Map<String, List<String>> getReponseHeaders();
        public String toString() {
            return String.format("Response %d\n%s", this.getStatusCode(), this.getResponseContent());
        }
    }
    public class WinRMRequestException extends Exception {
        private static final long serialVersionUID = 3980976892544461402L;
        private WinRMRequestFailureType exType = WinRMRequestFailureType.WinRMRequestSuccess;
        private int statusCode = 500;

        public WinRMRequestException(WinRMRequestFailureType type, int requestStatusCode, Throwable cause) {
            super(cause);
            this.exType = type;       
            this.statusCode = requestStatusCode;
        }
        public WinRMRequestException(WinRMRequestFailureType type, int requestStatusCode,String message, Throwable cause) {
            super(message, cause);
            this.exType = type;         
            this.statusCode = requestStatusCode;   
        }
        public WinRMRequestException(WinRMRequestFailureType type, int requestStatusCode,String message) {
            super(message);
            this.exType = type;
            this.statusCode = requestStatusCode;
        }
        public String toString() {
            return String.format("%s %s", this.exType, super.toString());
        }     
        public WinRMRequestFailureType getFailureType() {
            return this.exType;
        }
        public int getStatusCode() {
            return this.statusCode;
        }
    }
    
    public static Response request(WinRMCredential auth, URL target, String content) throws WinRMRequestException {
        if(!target.getProtocol().equalsIgnoreCase("http") && !target.getProtocol().equalsIgnoreCase("https")) {
            throw self.new WinRMRequestException(WinRMRequestFailureType.WinRMRequestArgumentFailure, 500,
                String.format("Invalid protocal %s", target.getProtocol()));
        }
        // try Negotiate auth at the very beginning
        try {
            return tryNegotiateAuth(auth, target, content);
        } catch (IOException ioeNego) {
            throw self.new WinRMRequestException(WinRMRequestFailureType.WinRMRequestConnectionFailure, 500, ioeNego);
        } catch (WinRMRequestException weNego) { 
            if(weNego.getFailureType() != WinRMRequestFailureType.WinRMRequestAuthenticationFailure)
                throw weNego;
            
            logger.error("Failed initial Negotiate auth, trying basic ...", weNego);             
            try {
                return tryBasicAuth(auth, target, content);
            } catch (IOException ioeBasic) {
                throw self.new WinRMRequestException(WinRMRequestFailureType.WinRMRequestConnectionFailure, 500, ioeBasic);
            } catch (WinRMRequestException weBasic) { 
                if (weBasic.getFailureType() == WinRMRequestFailureType.WinRMRequestAuthenticationFailure) {
                    throw self.new WinRMRequestException(WinRMRequestFailureType.WinRMRequestAuthenticationFailure, 401,
                        String.format("Failed both Negotiate and Basic auth -\nNego  : %s\nBasic : %s", weNego.getMessage(), weBasic.getMessage()));
                } else throw weBasic;
            }
        }        
    }
    private static void openConnectionWithCommonHeaders(HttpURLConnection conn) throws ProtocolException, IOException {
        conn.setRequestProperty("User-Agent", userAgent);
        conn.setRequestProperty("Accept", "*");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        if((System.getenv("DEBUG") != null) || (System.getenv("DEBUG_WINRM") != null)) {
            conn.setReadTimeout(3600 * 1000);
            conn.setConnectTimeout(3600 * 1000);
        } else {
            conn.setReadTimeout(300 * 1000);
            conn.setConnectTimeout(300 * 1000);
        }
        conn.connect();
    }
    private static byte[] getConnOutput(HttpURLConnection conn) {
        try {
            return CommonUtil.readFully(conn.getInputStream(), -1, true);
        } catch(IOException ioe) {
            try {
                return CommonUtil.readFully(conn.getErrorStream(), -1, true);
            } catch(Exception e) {
                logger.info("Failed to read both inputstream and errorstream");
                logger.info("inputstream:", ioe);
                logger.info("errorstream:", e);
                return new byte[0];
            }
        }
    }
    private static Response tryBasicAuth(WinRMCredential auth, URL target, String content) throws IOException, WinRMRequestException {
        HttpURLConnection conn = null;
        byte[] bytesToSend = content.getBytes("UTF-16");

        try {
            conn = (HttpURLConnection)target.openConnection();
            conn.setRequestProperty("Authorization", auth.getBasicAuthString());
            conn.setRequestProperty("Content-Length", bytesToSend.length + "");
            conn.setRequestProperty("Content-Type", "application/soap+xml;charset=UTF-16");
            
            openConnectionWithCommonHeaders(conn);

            conn.getOutputStream().write(bytesToSend);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();            

            if ((conn.getResponseCode() < 200) || (conn.getResponseCode() >= 300))
                throw self.new WinRMRequestException(
                    WinRMRequestFailureType.WinRMRequestAuthenticationFailure, conn.getResponseCode(),
                    String.format("Failed Basic auth, response %d %s", conn.getResponseCode(),
                        conn.getResponseMessage()));

            final int status = conn.getResponseCode();
            final String response = new String(getConnOutput(conn), "UTF-16");
            final Map<String, List<String>> headers = conn.getHeaderFields();
            return self.new Response() {
                public int getStatusCode() { return status; }
                public String getResponseContent() { return response; }
                public Map<String, List<String>> getReponseHeaders() { return headers; }
            };
        } finally { if (conn != null) conn.disconnect(); }        
    }
    private static Response tryNegotiateAuth(WinRMCredential auth, URL target, String content) throws IOException, WinRMRequestException {
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection)target.openConnection();
            
            conn.setRequestProperty("Authorization", "Negotiate " + Base64.encode(auth.getType1Message()));
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Length", "0");
            
            openConnectionWithCommonHeaders(conn);
            
            conn.getOutputStream().close();
            
            if (conn.getResponseCode() != 401)
                throw self.new WinRMRequestException(
                    WinRMRequestFailureType.WinRMRequestAuthenticationFailure, conn.getResponseCode(),
                    String.format("Invalid first auth response %d %s", conn.getResponseCode(),
                        conn.getResponseMessage()));
            
            String challenge = null;
            for(String authStr: conn.getHeaderFields().get("WWW-Authenticate"))
                if(authStr.startsWith("Negotiate"))
                    challenge = authStr.replace("Negotiate", "").trim();
            if((challenge == null) || challenge.isEmpty())
                throw self.new WinRMRequestException(WinRMRequestFailureType.WinRMRequestAuthenticationFailure, conn.getResponseCode(),
                    "Server probably does not accept Negotiate authentication.");
                
            byte[] t3msg = auth.getType3Message(Base64.decode(challenge));
            byte[] bytesToSend = new byte[0];
            
            conn = (HttpURLConnection)target.openConnection();      // Must reopen connection, under the hood java will reuse the connection if keep alive is accepted
            conn.setRequestProperty("Authorization", "Negotiate " + Base64.encode(t3msg));
            conn.setRequestProperty("Connection", "Keep-Alive");
            if ((content != null) && !content.isEmpty()) {
                conn.setRequestProperty("Content-Type",
                    "multipart/encrypted;protocol=\"application/HTTP-SPNEGO-session-encrypted\";boundary=\"Encrypted Boundary\"");
                //                conn.setRequestProperty("Content-Type", "application/soap+xml");
                bytesToSend = encryptDataWithNTLM(content.getBytes("UTF-16"), auth.getType3MessageMasterKey(), true);
            }
            conn.setRequestProperty("Content-Length", bytesToSend.length + "");
            
            openConnectionWithCommonHeaders(conn);
            if ((content != null) && !content.isEmpty()) {
                conn.getOutputStream().write(bytesToSend);
                conn.getOutputStream().flush();
            }
            conn.getOutputStream().close();
            
            if(conn.getResponseCode() == 401)
                throw self.new WinRMRequestException(WinRMRequestFailureType.WinRMRequestAuthenticationFailure, 401, "Unknown authentication failure");
//            else if((conn.getResponseCode() < 200) || (conn.getResponseCode() >= 300)) {
//                String res = null;
//                try { res = decryptDataWithNTLM(CommonUtil.readFully(conn.getErrorStream(), -1, true), conn.getContentLength(), auth.getType3MessageMasterKey()); }
//                catch(Exception e) { logger.info("Failed to get response under error condition.", e); res = null; }
//                throw self.new WinRMRequestException(WinRMRequestFailureType.WinRMRequestUnknownFailure, conn.getResponseCode(),
//                    String.format("Unknown WinRM request failure %d %s.\nreq:\n%s\nres:\n%s", conn.getResponseCode(), conn.getResponseMessage(), content, res));
//            } 
            final int status = conn.getResponseCode();
            final String response = decryptDataWithNTLM(getConnOutput(conn), conn.getContentLength(), auth.getType3MessageMasterKey());
            final Map<String, List<String>> headers = conn.getHeaderFields();
            return self.new Response() {
                public int getStatusCode() { return status; }
                public String getResponseContent() { return response; }
                public Map<String, List<String>> getReponseHeaders() { return headers; }
            };
        } finally { if (conn != null) conn.disconnect(); }        
    }

    private static byte[] encryptDataWithNTLM(byte[] input, byte[] signingKey, boolean seal) throws WinRMRequestException {        
        String[] fieldsBeforeSecTokenLength = new String[] {
            "--Encrypted Boundary",
            "\tContent-Type: application/HTTP-SPNEGO-session-encrypted",
            "\tOriginalContent: type=application/soap+xml;charset=UTF-16;Length=" + input.length,
            "--Encrypted Boundary",
            "\tContent-Type: application/octet-stream" 
            //"Content-Type: application/soap+xml"            
        };
        String prefix = "";
        for(String l: fieldsBeforeSecTokenLength)
            prefix += l + "\r\n";
        
        MessageDigest md5 = null;
        try { 
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            self.new WinRMRequestException(WinRMRequestFailureType.WinRMRequestAuthenticationFailure, 401,
                "Cannot perform Negotiate auth cause of lacking MD5 functionality");
        }
        byte[] clientSigningKey = md5.digest(CommonUtil.concat(signingKey, clientSigningKeyMagic));
        byte[] clientSealingKey = md5.digest(CommonUtil.concat(signingKey, clientSealingKeyMagic));
        byte[] wrapped = wrap(clientSigningKey, clientSealingKey, input, true);
        
//        CommonUtil.log_debug(String.format("%s\n%s", "wrapped:", Hexdump.toHexString(wrapped, 0, wrapped.length * 2)));
        
        try {
            return CommonUtil.concat(prefix.getBytes("ASCII"), CommonUtil.toBytesLE(16), wrapped, "--Encrypted Boundary--\r\n".getBytes("ASCII"));
        } catch (UnsupportedEncodingException e) {
            throw self.new WinRMRequestException(WinRMRequestFailureType.WinRMRequestUnknownFailure, 500,
                "Cannot perform Negotiate auth cause of lacking proper encoding support", e);
        }
    }
    private static byte[] wrap(byte[] signingKey, byte[] sealingKey, byte[] data, boolean doSeal) {
        int seqNum = 0;        
        byte[] sig = new byte[8];
        byte[] sealed = doSeal ? new byte[data.length] : data;
        byte[] sigInput = CommonUtil.concat(CommonUtil.toBytesLE(seqNum), data); 
        RC4 rc4 = new RC4(sealingKey);
        HMACT64 hmac = new HMACT64(signingKey);
        
        if(doSeal) rc4.update(data, 0, data.length, sealed, 0);
        sigInput = hmac.digest(sigInput);        
        rc4.update(sigInput, 0, 8, sig, 0);
        
        return CommonUtil.concat(new byte[] { 0x01, 0x00, 0x00, 0x00 }, sig, CommonUtil.toBytesLE(seqNum), sealed);
    }
    private static byte[] unwrap(byte[] signingKey, byte[] sealingKey, byte[] data, byte[] signature, boolean sealed) throws WinRMRequestException {
        byte[] unsealed = sealed ? new byte[data.length] : data;
        RC4 rc4 = new RC4(sealingKey);
        
        if(sealed) rc4.update(data, 0, data.length, unsealed, 0);
        
        byte[] rewrapped =  wrap(signingKey, sealingKey, unsealed, sealed);
        
        for(int i = 0; i < signature.length; i ++)            
            if(signature[i] != rewrapped[i])
                throw self.new WinRMRequestException(WinRMRequestFailureType.WinRMRequestUnknownFailure, 401,
                    String.format("Failed to verify server response signature, expected %s, server sent %s", 
                        Hexdump.toHexString(rewrapped, 0, signature.length * 2),
                        Hexdump.toHexString(signature, 0, signature.length * 2)));
        
        return unsealed;
    }
    private static String decryptDataWithNTLM(byte[] res, int expectedLen, byte[] signingKey) throws WinRMRequestException {
        try {
            String resStringWrap = new String(res, "ASCII");
            String[] resStringWrapLiens = resStringWrap.split("\r\n", 6);
            
            if((resStringWrapLiens.length != 6) || 
                !resStringWrapLiens[0].equalsIgnoreCase("--Encrypted Boundary") || 
                !resStringWrapLiens[1].trim().equalsIgnoreCase("Content-Type: application/HTTP-SPNEGO-session-encrypted") ||
                !resStringWrapLiens[2].trim().toUpperCase().startsWith("OriginalContent: type=application/soap+xml;charset=".toUpperCase()) ||
                !resStringWrapLiens[3].equalsIgnoreCase("--Encrypted Boundary") ||
                !resStringWrapLiens[4].trim().equalsIgnoreCase("Content-Type: application/octet-stream"))
                throw self.new WinRMRequestException(WinRMRequestFailureType.WinRMRequestUnknownFailure, 401, "Failed to decrypt server response");
            
            byte[] remaining = CommonUtil.slice(res, resStringWrapLiens[0].length() + 
                resStringWrapLiens[1].length() + resStringWrapLiens[2].length() +
                resStringWrapLiens[3].length() + resStringWrapLiens[4].length() + ("\r\n".length() * 5));
            String[] info = resStringWrapLiens[2].trim().toUpperCase().replace("OriginalContent: type=application/soap+xml;charset=".toUpperCase(), "").split(";Length=".toUpperCase());
            String encoding = info[0];
            int originalLength = Integer.parseInt(info[1]);
            
            byte[] message = CommonUtil.slice(remaining, 0, 4 + 16 + originalLength);
            byte[] trailing = CommonUtil.slice(remaining, 4 + 16 + originalLength); 
            
            if(!new String(trailing, "ASCII").equalsIgnoreCase("--Encrypted Boundary--\r\n"))
                throw self.new WinRMRequestException(WinRMRequestFailureType.WinRMRequestUnknownFailure, 401, "Failed to decrypt server response, invalid trailing");
            
            ByteBuffer bb = ByteBuffer.wrap(message);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            if(bb.getInt(0) != 16)
                throw self.new WinRMRequestException(WinRMRequestFailureType.WinRMRequestUnknownFailure, 401, "Failed to decrypt server response, invalid signature length. Expected 16, got " + bb.getInt(0));
            
            byte[] sig = CommonUtil.slice(message, 4, 16);
            byte[] sealed = CommonUtil.slice(message, 4 + 16);
            
            MessageDigest md5 = null;
            try { 
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                self.new WinRMRequestException(WinRMRequestFailureType.WinRMRequestAuthenticationFailure, 500,
                    "Cannot perform Negotiate auth cause of lacking MD5 functionality");
            }
            
            byte[] serverSigningKey = md5.digest(CommonUtil.concat(signingKey, serverSigningKeyMagic));
            byte[] serverSealingKey = md5.digest(CommonUtil.concat(signingKey, serverSealingKeyMagic));
            
            return new String(unwrap(serverSigningKey, serverSealingKey, sealed, sig, true), encoding);
        } catch(UnsupportedEncodingException uee) {
            throw self.new WinRMRequestException(WinRMRequestFailureType.WinRMRequestUnknownFailure, 500, "Client does not support server encoding", uee);
        }
    }
}


