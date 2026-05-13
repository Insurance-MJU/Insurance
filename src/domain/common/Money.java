package domain.common;

import java.io.Serializable;

public class Money implements Serializable {
    private static final long serialVersionUID = 1L;
    private long amount;
    private String currency;

    public Money(long amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public long getAmount() { return amount; }
    public String getCurrency() { return currency; }
}
