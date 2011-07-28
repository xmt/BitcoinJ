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
    private final LinkedHashMap<Address, BigInteger> spends;
    private final List<TransactionOutput> inputs;
    private final BigInteger fee;

    PreparedSpend(final LinkedHashMap<Address, BigInteger> spends,
            final List<TransactionOutput> inputs, final BigInteger fee)
    {
        this.spends = spends;
        this.inputs = inputs;
        this.fee = fee;
    }
    
    public BigInteger getFee()
    {
        return fee;
    }
}
