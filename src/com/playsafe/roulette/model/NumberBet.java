package com.playsafe.roulette.model;

public class NumberBet extends Bet {
    private Integer number;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public void calculateWinnings(int numberDrawn) {
        if (number == numberDrawn) {
            formatWinnings(number.toString(), new Integer(getBetAmount()*36).toString(), "WIN");
        } else {
            formatWinnings(number.toString(), new Integer(0).toString(), "LOSS");
        }
    }

    public NumberBet(String player, Integer number, Integer betAmount) {
        this.number = number;
        setType(BetType.NUMBER);
        setBetAmount(betAmount);
        setPlayer(player);
    }
}
