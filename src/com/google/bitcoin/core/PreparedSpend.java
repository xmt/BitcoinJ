package com.google.bitcoin.core;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Mark Tolley
 */
public class PreparedSpend
{
    private final long timestamp;
    
    private final LinkedHashMap<Address, BigInteger> spends;
    private final List<TransactionOutput> inputs;
    private final BigInteger fee;

    PreparedSpend(long timestamp, final LinkedHashMap<Address, BigInteger> spends,
            final List<TransactionOutput> inputs, final BigInteger fee)
    {
        this.timestamp = timestamp;
        this.spends = spends;
        this.inputs = inputs;
        this.fee = fee;
    }
    
    public BigInteger getFee()
    {
        return fee;
    }
    
    long getTimestamp()
    {
        return timestamp;
    }
}
