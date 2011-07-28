package com.google.bitcoin.core;

import com.google.bitcoin.store.BlockStore;
import com.google.bitcoin.store.MemoryBlockStore;
import java.math.BigInteger;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Mark Tolley
 */
public class SpendTest
{
    static final NetworkParameters params = NetworkParameters.unitTests();
    
    private Address myAddress;
    private Wallet wallet;
    private BlockStore blockStore;
    private ECKey myKey;

    @Before
    public void setUp() throws Exception {
        myKey = new ECKey();
        myAddress = myKey.toAddress(params);
        wallet = new Wallet(params);
        wallet.addKey(myKey);
        blockStore = new MemoryBlockStore(params);
    }
    
    @Test
    public void basicTest() throws Exception
    {
        Spend spend = Spend.create(myAddress, Utils.toNanoCoins(1, 0));
        boolean sent = false;
        do
        {
            PreparedSpend proposedSpend = wallet.prepareSpend(spend);
            if (!proposedSpend.getFee().equals(BigInteger.ZERO))
            {
                // we don't want to pay any fees
                return;
            }
            // if our spend has been invalidated, keep trying until it goes through
            sent = wallet.spend(proposedSpend);
        }
        while(!sent);
    }
}
