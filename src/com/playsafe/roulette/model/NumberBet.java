package com.playsafe.roulette.model;

import com.playsafe.roulette.RouletteGame;

public class NumberBet extends Bet {
    private Integer number;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public void calculateWinnings(RouletteGame game, int numberDrawn) {
        Integer winningAmount = null;
        if (number == numberDrawn) {
            winningAmount = new Integer(getBetAmount()*36);
            formatWinnings(number.toString(), winningAmount.toString(), "WIN");
       } else {
            winningAmount = new Integer(0);
            formatWinnings(number.toString(), winningAmount.toString(), "LOSS");
        }
        game.updatePlayerStats(getPlayer(), getBetAmount(), winningAmount);
    }

    public NumberBet(String player, Integer number, Integer betAmount) {
        this.number = number;
        setType(BetType.NUMBER);
        setBetAmount(betAmount);
        setPlayer(player);
    }
}
