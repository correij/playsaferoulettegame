package com.playsafe.roulette;

import com.playsafe.roulette.model.Bet;
import com.playsafe.roulette.model.GameInstance;
import com.playsafe.roulette.model.NumberBet;
import com.playsafe.roulette.model.OddOrEvenBet;
import com.playsafe.roulette.tools.IntRandomNumberGenerator;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Creates a simple command line multiplayer version of Roulette
 *
 * @author josecorreia
 */
public class RouletteGame {
    final public static Logger logger = LogManager.getRootLogger();

    private IntRandomNumberGenerator numberGenerator;

    private HashSet<String> players;

    private ConcurrentLinkedQueue bets;

    public ConcurrentLinkedQueue getBets() {
        return bets;
    }

    public void setBets(ConcurrentLinkedQueue bets) {
        this.bets = bets;
    }

    public RouletteGame(HashSet<String> players) {
        this.players = players;
        numberGenerator = new IntRandomNumberGenerator(1, 36);
        bets = new ConcurrentLinkedQueue<>();
    }

    public void addBet(Bet bet) {
        bets.add(bet);
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
        HashSet<String> players = readPlayersFromFile();
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

    private static void processInput(HashSet<String> players, RouletteGame game, String input) {
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

        if (!players.contains(player)) {
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

    private static HashSet readPlayersFromFile() {
        Path gamePlayersFile = Paths.get("roulettegameplayers.txt");
        HashSet<String> players = new HashSet<>();

        if (Files.notExists(gamePlayersFile, LinkOption.NOFOLLOW_LINKS)) {
            logger.debug("Game Players file " + gamePlayersFile.getFileName() + " does not exist");
            exitGame();
        }
        Charset charset = Charset.forName("US-ASCII");
        try (BufferedReader reader = Files.newBufferedReader(gamePlayersFile, charset)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().equals("")) {
                    players.add(line);
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
