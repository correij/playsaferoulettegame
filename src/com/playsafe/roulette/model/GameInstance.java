package com.playsafe.roulette.model;

import com.playsafe.roulette.RouletteGame;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Starts a worker thread that will perform a roulette roll and award winnings to the bets that made the round.
 */
public class GameInstance implements Runnable {
    private RouletteGame game;

    public GameInstance(RouletteGame game) {
        this.game = game;
    }

    @Override
    public void run() {
        //generate roulette number
        int generatedNumber = game.generateRouletteNumber();

        //iterate through current bets for this round
        ConcurrentLinkedQueue<Bet> bets = game.getBets();
        if (bets.isEmpty()) {
            RouletteGame.logger.debug("No bets placed for this round");
        } else {
            RouletteGame.logger.debug("Number: " + generatedNumber);
            Bet.printWinningsHeader();

            for (Bet bet : bets) {
                bet.calculateWinnings(generatedNumber);
            }

            RouletteGame.logger.debug("\nPlace your new bets\n");
        }
        game.clearBets();
    }
}
