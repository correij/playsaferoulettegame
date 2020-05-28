package com.playsafe.roulette.model;

import com.playsafe.roulette.RouletteGame;

public abstract class Bet {
    private String player;
    private BetType type;
    private Integer betAmount;

    public Integer getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(Integer betAmount) {
        this.betAmount = betAmount;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public BetType getType() {
        return type;
    }

    public void setType(BetType type) {
        this.type = type;
    }

    public static String appendNameSpacing(String value) {
        if (value == null) {
            return "";
        }
        if (value.length() > 20) {
            return value.substring(0, 20);
        }
        int index = value.length();
        while (index <= 20) {
            value += " ";
            index++;
        }
        return value;
    }

    public static void printWinningsHeader() {
        RouletteGame.logger.debug(appendNameSpacing("Player")+appendSpacing("Bet")+appendSpacing("Outcome")+appendSpacing("Winnings"));
        RouletteGame.logger.debug("---");
    }

    public static String appendSpacing(String value) {
        if (value == null) {
            return "";
        }
        if (value.length() > 10) {
            return value.substring(0, 10);
        }
        int index = value.length();
        while (index <= 10) {
            value += " ";
            index++;
        }
        return value;
    }

    public void formatWinnings(String bet, String winnings, String outcome) {
       RouletteGame.logger.debug(appendNameSpacing(getPlayer())+appendSpacing(bet)+appendSpacing(outcome)+appendSpacing(winnings));
    }

    public abstract void calculateWinnings(int generatedNumber);
}
