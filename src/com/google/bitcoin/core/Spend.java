package com.google.bitcoin.core;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mark Tolley
 */
public class Spend
{
    private static final Logger log = LoggerFactory.getLogger(Spend.class);
    
    private LinkedHashMap<Address, BigInteger> spends;
    
    private Spend()
    {
        spends = new LinkedHashMap<Address, BigInteger>();
    }
    
    public static Spend create(final Address address, final BigInteger nanocoins)
    {
        Spend spend = new Spend();
        
        spend.add(address, nanocoins);
        
        return spend;
    }
    
    public void add(final Address address, final BigInteger nanocoins)
    {
        BigInteger amount = nanocoins;
        
        if (spends.containsKey(address))
        {
            amount = amount.add(spends.get(address));
        }
        
        spends.put(address, amount);
    }
    
    /**
     * Subclasses can override this method to change the coin selection algorithm and fee rules
     */
    PreparedSpend prepare(final Wallet wallet)
    {
        // sort coins into ascending order to combat wallet fragmentation and reduce fees
        final SortedSet<TransactionOutput> unspent = new TreeSet<TransactionOutput>(
                new Comparator<TransactionOutput>()
                {
                    public int compare(TransactionOutput o1, TransactionOutput o2)
                    {
                        return o1.getValue().compareTo(o2.getValue());
                    }
                });
        unspent.addAll(wallet.getAvailableOutputs());
        
        BigInteger total = BigInteger.ZERO;
        for (final BigInteger value : spends.values())
        {
            total = total.add(value);
        }
        
        //XXX: copied from Wallet.createSend
        //XXX: fees aren't calculated yet
        // To send money to somebody else, we need to do gather up transactions with unspent outputs until we have
        // sufficient value.
        log.info("Creating spend for " + Utils.bitcoinValueToFriendlyString(total));
        
        BigInteger fee = BigInteger.ZERO;
        BigInteger valueGathered = BigInteger.ZERO;
        List<TransactionOutput> inputs = new LinkedList<TransactionOutput>();
        
        for (final TransactionOutput output : unspent) {
            inputs.add(output);
            valueGathered = valueGathered.add(output.getValue());
            if (valueGathered.compareTo(total) >= 0) break;
        }
        
        // Can we afford this?
        if (inputs.isEmpty() || valueGathered.compareTo(total) < 0) {
            log.info("Insufficient value in wallet for send, missing " +
                    Utils.bitcoinValueToFriendlyString(total.subtract(valueGathered)));
            //XXX: this shouldn't be a runtime exception
            throw new IllegalStateException("Insufficient funds");
        }

        BigInteger change = valueGathered.subtract(total);
        if (change.compareTo(BigInteger.ZERO) > 0) {
            // The value of the inputs is greater than what we want to send. Just like in real life then,
            // we need to take back some coins ... this is called "change". Add another output that sends the change
            // back to us.
            log.info("  with " + Utils.bitcoinValueToFriendlyString(change) + " coins change");
            spends.put(wallet.getChangeAddress(), change);
        }

        return new PreparedSpend(spends, inputs, fee);
    }
}
