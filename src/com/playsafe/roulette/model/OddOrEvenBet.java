package com.playsafe.roulette.model;

import com.playsafe.roulette.RouletteGame;

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
    public void calculateWinnings(RouletteGame game, int generatedNumber) {
        boolean isGeneratedNumberOdd = (generatedNumber % 2 != 0);

        Integer winningAmount = null;
        if (isGeneratedNumberOdd && isOdd()) {
            winningAmount = new Integer(getBetAmount()*2);

            formatWinnings(toString(), winningAmount.toString(), "WIN");
        } else if(!isGeneratedNumberOdd && !isOdd()) {
            winningAmount = new Integer(getBetAmount()*2);

            formatWinnings(toString(), winningAmount.toString(), "WIN");
        } else {
            winningAmount = new Integer(0);
            formatWinnings(toString(), winningAmount.toString(), "LOSS");
        }
        game.updatePlayerStats(getPlayer(), getBetAmount(), winningAmount);
    }

    @Override
    public String toString() {
        return (isOdd() ? "ODD" : "EVEN");
    }
}
