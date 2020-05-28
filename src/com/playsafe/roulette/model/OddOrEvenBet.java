package com.playsafe.roulette.model;

import java.util.Objects;

public class OddOrEvenBet extends Bet {
    private boolean odd;

    public boolean isOdd() {
        return odd;
    }

    public OddOrEvenBet(String player, boolean odd, Integer betAmount) {
        this.odd = odd;
        setType(BetType.ODD_OR_EVEN);
        setBetAmount(betAmount);
        setPlayer(player);
    }

    public void setOdd(boolean odd) {
        this.odd = odd;
    }

    @Override
    public void calculateWinnings(int generatedNumber) {
        boolean isGeneratedNumberOdd = (generatedNumber % 2 != 0);

        if (isGeneratedNumberOdd && isOdd()) {
            formatWinnings(toString(), new Integer(getBetAmount()*2).toString(), "WIN");
        } else {
            formatWinnings(toString(), new Integer(0).toString(), "LOSS");
        }
    }

    @Override
    public String toString() {
        return (isOdd() ? "ODD" : "EVEN");
    }
}
