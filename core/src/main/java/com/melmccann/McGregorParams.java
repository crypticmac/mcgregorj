/*
 * Copyright 2013 Google Inc.
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
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptOpCodes;
import com.lambdaworks.crypto.SCrypt;
import org.spongycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;

import static com.google.common.base.Preconditions.checkState;

/**
 * Parameters for the testnet, a separate public instance of Bitcoin that has relaxed rules suitable for development
 * and testing of applications and new Bitcoin versions.
 */
public class McGregorParams extends NetworkParameters {
    public McGregorParams() {
        super();
        id = "com.melmccann.production";
        proofOfWorkLimit = Utils.decodeCompactBits(0x1e0fffffL);
        addressHeader = 51;
        acceptableAddressCodes = new int[] { 51 };
        port = 18888;
        //packetMagic = 0xfbc0b6dbL;
        packetMagic = 0xf6f0b6cbL;
        dumpedPrivateKeyHeader = 128 + addressHeader;

        targetTimespan = (int)(1.5 * 24 * 60 * 60);
        interval = targetTimespan/((int)(1.5 * 60));

        genesisBlock.setDifficultyTarget(0x1e0ffff0L);//genesis.nBits
        genesisBlock.setTime(1457725764L);
        genesisBlock.setNonce(696898L);
        genesisBlock.removeTransaction(0);
        Transaction t = new Transaction(this);
        try {
            // A script containing the difficulty bits and the following message:
            //
            //   ""
            byte[] bytes = Hex.decode
                    ("04ffff001d01043c5768656e206f6e65206f6620757320676f657320746f207761722c20776520616c6c20676f20746f207761722e20436f6e6f72204d63477265676f72");
            t.addInput(new TransactionInput(this, t, bytes));
            ByteArrayOutputStream scriptPubKeyBytes = new ByteArrayOutputStream();
            Script.writeBytes(scriptPubKeyBytes, Hex.decode
                    ("04678afdb0fe5548271967f1a67130b7105cd6a828e03909a67962e0ea1f61deb649f6bc3f4cef38c4f35504e51ec112de5c384df7ba0b8d578a4c702b6bf11d5f"));
            scriptPubKeyBytes.write(ScriptOpCodes.OP_CHECKSIG);
            t.addOutput(new TransactionOutput(this, t, Utils.toNanoCoins(50, 0), scriptPubKeyBytes.toByteArray()));
        } catch (Exception e) {
            // Cannot happen.
            throw new RuntimeException(e);
        }
        genesisBlock.addTransaction(t);
        String genesisHash = genesisBlock.getHashAsString();
        checkState(genesisHash.equals("0cc2a9ab776bd0eaf0d69894816066754d6ff1a60212005db6c75b4857a38b40"),
                genesisBlock);

        subsidyDecreaseBlockCount = 840000;

        dnsSeeds = new String[] {
                "dnsseed.mcgregorproject.com"
        };
    }

    private static BigInteger MAX_MONEY = Utils.COIN.multiply(BigInteger.valueOf(84000000));
    @Override
    public BigInteger getMaxMoney() { return MAX_MONEY; }

    private static McGregorParams instance;
    public static synchronized McGregorParams get() {
        if (instance == null) {
            instance = new McGregorParams();
        }
        return instance;
    }

    /** The number of previous blocks to look at when calculating the next Block's difficulty */
    @Override
    public int getRetargetBlockCount(StoredBlock cursor) {
        if (cursor.getHeight() + 1 != getInterval()) {
            //Logger.getLogger("wallet_ltc").info("Normal LTC retarget");
            return getInterval();
        } else {
            //Logger.getLogger("wallet_ltc").info("Genesis LTC retarget");
            return getInterval() - 1;
        }
    }

    @Override public String getURIScheme() { return "McGregor:"; }

    /** Gets the hash of the given block for the purpose of checking its PoW */
    public Sha256Hash calculateBlockPoWHash(Block b) {
        byte[] blockHeader = b.cloneAsHeader().bitcoinSerialize();
        try {
            return new Sha256Hash(Utils.reverseBytes(SCrypt.scrypt(blockHeader, blockHeader, 1024, 1, 1, 32)));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        NetworkParameters.registerParams(get());
        NetworkParameters.PROTOCOL_VERSION = 70002;
    }
}
