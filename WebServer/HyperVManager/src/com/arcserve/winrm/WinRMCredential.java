package com.arcserve.winrm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import jcifs.ntlmssp.NtlmFlags;
import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;
import jcifs.util.Base64;

public abstract class WinRMCredential {
    public static WinRMCredential get(String username, String password) throws IllegalArgumentException {
        if((username == null) || username.isEmpty() || (password == null) || password.isEmpty())
            throw new IllegalArgumentException(String.format("Illegal user/pass: [%s]/[%s]", username, password));
        username = username.replace('\\', '/');
        if(username.contains("/")) {
            String[] usernameSplit = username.split("/", 2);
            switch(usernameSplit.length) {
            case 1:
                return get(".", usernameSplit[0], password);
            case 2:
                return get(usernameSplit[0], usernameSplit[1], password);
            default:
                throw new IllegalArgumentException(String.format("Illegal user/pass: [%s]/[%s]", username, password));
            }
        } else return get(".", username, password);
    }
    public static WinRMCredential get(final String domain, final String username, final String password)  throws IllegalArgumentException {
        if ((domain == null) || domain.isEmpty())
            return get(username, password);
        
        if((username == null) || username.isEmpty() || (password == null) || password.isEmpty())
            throw new IllegalArgumentException(String.format("Illegal user/pass: [%s]/[%s]", username, password));
        
        return new WinRMCredential() {
            public String getDomain() { return domain; }        
            public String getUsername() { return username; }
            public String getPassword() { return password; }            
        };
    }
    public abstract String getDomain();
    public abstract String getUsername();
    public abstract String getPassword();
    
    public String getBasicAuthString() throws UnsupportedEncodingException {
        return "Basic " + Base64.encode(String.format("%s:%s", this.getUsername(), this.getPassword()).getBytes("UTF-8"));
    }
    
    private static final String workstation = "ARC-WSMAN-11-12";
    private int ntlmsspFlags = 0;
    private byte[] masterKey;
    
    public byte[] getType1Message() {
        this.ntlmsspFlags = NtlmFlags.NTLMSSP_REQUEST_TARGET | NtlmFlags.NTLMSSP_NEGOTIATE_NTLM2 |
            NtlmFlags.NTLMSSP_NEGOTIATE_128 | NtlmFlags.NTLMSSP_NEGOTIATE_SIGN |
            NtlmFlags.NTLMSSP_NEGOTIATE_ALWAYS_SIGN | NtlmFlags.NTLMSSP_NEGOTIATE_KEY_EXCH | 
            NtlmFlags.NTLMSSP_NEGOTIATE_SEAL;
        Type1Message msg1 = new Type1Message(this.ntlmsspFlags, this.getDomain(), workstation);
        return msg1.toByteArray();
    }
    public byte[] getType3Message(byte[] type2Msg) throws IOException {
        Type2Message msg2 = new Type2Message(type2Msg);
        
        this.ntlmsspFlags &= msg2.getFlags();
        
        Type3Message msg3 = new Type3Message(msg2, this.getPassword(), this.getDomain(), this.getUsername(), workstation, this.ntlmsspFlags);        
        
        if ((this.ntlmsspFlags & NtlmFlags.NTLMSSP_NEGOTIATE_SIGN) != 0)
          this.masterKey = msg3.getMasterKey();

        return msg3.toByteArray();
    }
    public byte[] getType3MessageMasterKey() { return this.masterKey; }            
}
