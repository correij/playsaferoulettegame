package com.playsafe.roulette;

import com.playsafe.roulette.model.Bet;
import com.playsafe.roulette.controller.GameInstance;
import com.playsafe.roulette.model.NumberBet;
import com.playsafe.roulette.model.OddOrEvenBet;
import com.playsafe.roulette.tools.IntRandomNumberGenerator;
import com.playsafe.roulette.tools.PrinterUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;

/**
 * Creates a simple command line multiplayer version of Roulette
 *
 * @author josecorreia
 */
public class RouletteGame {
    final public static Logger logger = LogManager.getRootLogger();

    private IntRandomNumberGenerator numberGenerator;

    private ConcurrentHashMap<String, String> players;

    private ConcurrentLinkedQueue<Bet> bets;

    public ConcurrentLinkedQueue<Bet> getBets() {
        return bets;
    }


    public RouletteGame(ConcurrentHashMap<String, String> players) {
        this.players = players;
        numberGenerator = new IntRandomNumberGenerator(1, 36);
        bets = new ConcurrentLinkedQueue<>();
    }

    public void addBet(Bet bet) {
        bets.add(bet);
    }

    public void updatePlayerStats(String player, Integer betAmount, Integer winningAmount) {
        String currentStats = players.get(player);
        String[] details = currentStats.split(",");

        if (details.length == 1) {
            //first time
            players.computeIfPresent(player, (key, oldValue) -> player + "," + winningAmount + "," + betAmount);
        } else {
            players.computeIfPresent(player, (key, oldValue) -> {
                Integer totalWinnings = Integer.parseInt(details[1]) + winningAmount;
                Integer totalBetAmount = Integer.parseInt(details[2]) + betAmount;

                return player + "," + totalWinnings + "," + totalBetAmount;
            });
        }
    }

    public void printTotalWinnings() {
        RouletteGame.logger.debug("\n");
        RouletteGame.logger.debug(PrinterUtils.appendNameSpacing("Player") + PrinterUtils.appendSpacing("Total Win") + PrinterUtils.appendSpacing("Total Bet"));
        for(String playerStat : players.keySet()) {
            String[] details = players.get(playerStat).split(",");
            if (details.length == 1) {
                RouletteGame.logger.debug(PrinterUtils.appendNameSpacing(details[0]) + PrinterUtils.appendSpacing("0") + PrinterUtils.appendSpacing("0"));
            } else {
                RouletteGame.logger.debug(PrinterUtils.appendNameSpacing(details[0]) + PrinterUtils.appendSpacing(details[1]) + PrinterUtils.appendSpacing(details[2]));
            }
        }
        RouletteGame.logger.debug("---");
    }

    public void clearBets() {
       bets = new ConcurrentLinkedQueue<>();
    }

    public int generateRouletteNumber() {
        return numberGenerator.nextInt();
    }

    public static void main(String[] args) {
        Logger.getRootLogger().setLevel(Level.DEBUG);
        logger.debug("Welcome to Roulette Online Game! You have 30 seconds to place bets");
        ConcurrentHashMap<String, String> players = readPlayersFromFile();
        if (players.isEmpty()) {
            logger.debug("Game Players file is empty");
            exitGame();
        }
        RouletteGame game = new RouletteGame(players);
        startGame(game);

        while (true)   {
            String input = System.console().readLine();
            processInput(players, game, input);
        }
    }

    private static void processInput(ConcurrentHashMap<String, String> players, RouletteGame game, String input) {
        if ("exit".equals(input)) {
            logger.debug("Thanks for playing Online Roulette. Come back soon!");
            exitGame();
        }
        String[] items = input.split(" ");
        if (items.length != 3) {
            logger.debug("Place a number bet or an ODD or EVEN bet with following format");
            logger.debug("Number bet: Name_of_player number amount");
            logger.debug("Example: Barbara 5 100");
            logger.debug("ODD or EVEN bet: Name_of_player type amount");
            logger.debug("Example: Barbara EVEN 200\n");
            return;
        }
        String player = items[0];

        if (!players.containsKey(player)) {
            logger.debug("Unknown player: " + player);
            return;
        }
        String value = items[1];
        if (!isValidBet(value)) {
            return;
        }
        String amount = items[2];
        if (!isValidAmount(amount)) {
            return;
        }
        if (value.equalsIgnoreCase("EVEN") || value.equalsIgnoreCase("ODD")) {
            game.addBet(new OddOrEvenBet(player, (value.equalsIgnoreCase("ODD") ? true : false), Integer.parseInt(amount)));
        } else {
            game.addBet(new NumberBet(player, Integer.parseInt(value), Integer.parseInt(amount)));
        }
    }

    private static boolean isValidAmount(String amount) {
        String valueErrorMessage = "You betted: " + amount + ". It needs to be a valid number between 1-1000\n";
        try {
            int numberValue = Integer.parseInt(amount);
            if (numberValue < 1 || numberValue > 1000) {
                logger.debug(valueErrorMessage);
                return false;
            }
        } catch (NumberFormatException e) {
            logger.debug(valueErrorMessage);
            return false;
        }
        return true;
    }
    private static boolean isValidBet(String value) {
        String valueErrorMessage = "Value either needs to either be EVEN or ODD or a number between 1 and 36\n";

        if (!value.equalsIgnoreCase("EVEN") && !value.equalsIgnoreCase("ODD")) {
            //check if number
            try {
                int numberValue = Integer.parseInt(value);
                if (numberValue < 1 || numberValue > 36) {
                    logger.debug(valueErrorMessage);
                    return false;
                }
            } catch (NumberFormatException e) {
                logger.debug(valueErrorMessage);
                return false;
            }
        }
        return true;
    }

    private static void startGame(RouletteGame game) {
        GameInstance instance = new GameInstance(game);
        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(instance, 30, 30, TimeUnit.SECONDS);
    }

    private static ConcurrentHashMap<String, String> readPlayersFromFile() {
        Path gamePlayersFile = Paths.get("roulettegameplayers.txt");
        ConcurrentHashMap<String, String> players = new ConcurrentHashMap<>();

        if (Files.notExists(gamePlayersFile, LinkOption.NOFOLLOW_LINKS)) {
            logger.debug("Game Players file " + gamePlayersFile.getFileName() + " does not exist");
            exitGame();
        }
        Charset charset = Charset.forName("US-ASCII");
        try (BufferedReader reader = Files.newBufferedReader(gamePlayersFile, charset)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().equals("")) {
                    String[] details = line.split(",");
                    if (details.length == 1) {
                        // no stored stats yet
                        players.put(details[0], details[0]);
                    } else {
                        players.put(details[0], line);
                    }
                }
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        return players;
    }

    private static void exitGame() {
        System.exit(0);
    }
}
