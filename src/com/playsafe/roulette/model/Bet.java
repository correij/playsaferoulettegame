package com.playsafe.roulette.model;

import com.playsafe.roulette.RouletteGame;
import com.playsafe.roulette.tools.PrinterUtils;

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

    public abstract void calculateWinnings(RouletteGame game, int generatedNumber);

    public void formatWinnings(String bet, String winnings, String outcome) {
        RouletteGame.logger.debug(PrinterUtils.appendNameSpacing(getPlayer())+PrinterUtils.appendSpacing(bet)+PrinterUtils.appendSpacing(outcome)+PrinterUtils.appendSpacing(winnings));
    }

    public static void printWinningsHeader() {
        RouletteGame.logger.debug(PrinterUtils.appendNameSpacing("Player")+PrinterUtils.appendSpacing("Bet")+PrinterUtils.appendSpacing("Outcome")+PrinterUtils.appendSpacing("Winnings"));
        RouletteGame.logger.debug("---");
    }
}
