/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.rs.security.oauth2.jwe;

import java.security.Key;

import org.apache.cxf.rs.security.oauth2.jwt.Algorithm;
import org.apache.cxf.rs.security.oauth2.utils.crypto.CryptoUtils;
import org.apache.cxf.rs.security.oauth2.utils.crypto.KeyProperties;

public class WrappedKeyJweDecryptor extends AbstractJweDecryptor {
    private Key cekDecryptionKey;
    private boolean unwrap;
    public WrappedKeyJweDecryptor(Key cekDecryptionKey) {    
        this(cekDecryptionKey, true);
    }
    public WrappedKeyJweDecryptor(Key cekDecryptionKey, boolean unwrap) {    
        this(cekDecryptionKey, unwrap, null);
    }
    public WrappedKeyJweDecryptor(Key cekDecryptionKey, JweCryptoProperties props) {
        this(cekDecryptionKey, true, props);
    }
    public WrappedKeyJweDecryptor(Key cekDecryptionKey, boolean unwrap,
                                  JweCryptoProperties props) {    
        super(props);
        this.cekDecryptionKey = cekDecryptionKey;
        this.unwrap = unwrap;
    }
    protected byte[] getContentEncryptionKey() {
        KeyProperties keyProps = new KeyProperties(getKeyEncryptionAlgorithm());
        if (!unwrap) {
            keyProps.setBlockSize(getKeyCipherBlockSize());
            return CryptoUtils.decryptBytes(getEncryptedContentEncryptionKey(), getCekDecryptionKey(), keyProps);
        } else {
            return CryptoUtils.unwrapSecretKey(getEncryptedContentEncryptionKey(), 
                                               getContentEncryptionAlgorithm(), 
                                               getCekDecryptionKey(), 
                                               keyProps).getEncoded();
        }
    }
    protected Key getCekDecryptionKey() {
        return cekDecryptionKey;
    }
    protected int getKeyCipherBlockSize() {
        return -1;
    }
    protected String getKeyEncryptionAlgorithm() {
        return Algorithm.toJavaName(getHeaders().getKeyEncryptionAlgorithm());
    }
}
