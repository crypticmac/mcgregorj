/**
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.melmccann;

import com.google.bitcoin.core.*;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.params.TestNet3Params;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

import java.util.Arrays;

import static org.junit.Assert.*;

public class McGregorAddressTest {
    static final NetworkParameters mainParams = McGregorParams.get();

    @Test
    public void stringification() throws Exception {
        Address b = new Address(mainParams, Hex.decode("9fa1276bd68bfc598341869483ffe386b9341701"));
        assertEquals("MmnoiDSU36z8j1RXoKEH3FXtUUqjEkcZBf", b.toString());
    }
    
    @Test
    public void decoding() throws Exception {
        Address b = new Address(mainParams, "MmnoiDSU36z8j1RXoKEH3FXtUUqjEkcZBf");
       // System.out.println("\n\n\n\n\n\n  MELBO\n\n\n\n"+Utils.bytesToHexString(b.getHash160())+"\n\n\n\n\n\n-------------------------------");
        assertEquals("9fa1276bd68bfc598341869483ffe386b9341701", Utils.bytesToHexString(b.getHash160()));
    }

    @Test
    public void errorPaths() {
        // Check the case of a mismatched network.
        /*try {
           // new Address(TestNet3Params.get(), "LRywuuNCDLJCX4cnQ8RxQQoLbtM5FRsgto");
           // fail();
        } catch (WrongNetworkException e) {
            // Success.
            //assertEquals(e.verCode, McGregorParams.get().getAddressHeader());
            assertTrue(Arrays.equals(e.acceptableVersions, TestNet3Params.get().getAcceptableAddressCodes()));
        } catch (AddressFormatException e) {
            fail();
        }
        */
    }

    @Test
    public void getNetwork() throws Exception {
        NetworkParameters params = Address.getParametersFromAddress("MmnoiDSU36z8j1RXoKEH3FXtUUqjEkcZBf");
        assertEquals(McGregorParams.get().getId(), params.getId());
    }
}
