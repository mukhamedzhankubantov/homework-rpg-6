package com.narxoz.rpg.tournament;

import com.narxoz.rpg.arena.ArenaFighter;
import com.narxoz.rpg.arena.ArenaOpponent;
import com.narxoz.rpg.arena.TournamentResult;
import com.narxoz.rpg.chain.ArmorHandler;
import com.narxoz.rpg.chain.BlockHandler;
import com.narxoz.rpg.chain.DefenseHandler;
import com.narxoz.rpg.chain.DodgeHandler;
import com.narxoz.rpg.chain.HpHandler;
import com.narxoz.rpg.command.ActionQueue;
import com.narxoz.rpg.command.AttackCommand;
import com.narxoz.rpg.command.DefendCommand;
import com.narxoz.rpg.command.HealCommand;
import java.util.Random;

public class TournamentEngine {
    private final ArenaFighter hero;
    private final ArenaOpponent opponent;
    private Random random = new Random(1L);

    public TournamentEngine(ArenaFighter hero, ArenaOpponent opponent) {
        this.hero = hero;
        this.opponent = opponent;
    }

    public TournamentEngine setRandomSeed(long seed) {
        this.random = new Random(seed);
        return this;
    }

    public TournamentResult runTournament() {
        TournamentResult result = new TournamentResult();
        int round = 0;
        final int maxRounds = 20;

        // TODO: Build the defense chain using fluent setNext():
        //   DodgeHandler -> BlockHandler -> ArmorHandler -> HpHandler
        // Hint: use hero stats for each handler's parameters.
        //   new DodgeHandler(hero.getDodgeChance(), <seed>)
        //   new BlockHandler(hero.getBlockRating() / 100.0)   <-- note the int-to-double conversion
        //   new ArmorHandler(hero.getArmorValue())
        //   new HpHandler()
        // Chain them: dodge.setNext(block).setNext(armor).setNext(hp)
        long dodgeSeed = random.nextLong();
        DefenseHandler dodge = new DodgeHandler(hero.getDodgeChance(), dodgeSeed);
        DefenseHandler block = new BlockHandler(hero.getBlockRating() / 100.0);
        DefenseHandler armor = new ArmorHandler(hero.getArmorValue());
        DefenseHandler hp = new HpHandler();
        dodge.setNext(block).setNext(armor).setNext(hp);


        // TODO: Create an ActionQueue (the invoker).

        // TODO: Simulate rounds until hero or opponent is defeated (or maxRounds is reached).
        // Each round should:
        //   1) Increment round counter.
        //   2) Enqueue hero actions: AttackCommand, HealCommand, DefendCommand.
        //      Use hero.getAttackPower() for AttackCommand, a fixed heal amount for HealCommand,
        //      and a small dodge boost for DefendCommand.
        //   3) Print the queued commands using actionQueue.getCommandDescriptions().
        //   4) Call actionQueue.executeAll() to run all hero actions.
        //   5) If the opponent is still alive: have the opponent attack the hero.
        //      Route the attack through the defense chain: defenseChain.handle(opponent.getAttackPower(), hero)
        //      Do NOT call hero.takeDamage() directly here.
        //   6) Log round results (e.g. "[Round N] Opponent HP: X | Hero HP: Y").
        //   7) Add the log line to result.addLine(...).

        // TODO: After the loop, determine the winner.
        //   result.setWinner(hero.isAlive() ? hero.getName() : opponent.getName());
        while (hero.isAlive() && opponent.isAlive() && round < maxRounds) {
            round++;

            ActionQueue actionQueue = new ActionQueue();
            actionQueue.enqueue(new AttackCommand(opponent, hero.getAttackPower()));

            if (hero.getHealPotions() > 0 && hero.getHealth() < hero.getMaxHealth()) {
                actionQueue.enqueue(new HealCommand(hero, 20));
            }

            actionQueue.enqueue(new DefendCommand(hero, 0.10));

            System.out.println("[Round " + round + "] Queued actions:");
            for (String desc : actionQueue.getCommandDescriptions()) {
                System.out.println("  " + desc);
            }

            actionQueue.executeAll();

            if (opponent.isAlive()) {
                System.out.println("[Round " + round + "] " + opponent.getName() + " attacks for "
                        + opponent.getAttackPower() + " damage!");
                dodge.handle(opponent.getAttackPower(), hero);
            }

            String logLine = "[Round " + round + "] Opponent HP: " + opponent.getHealth()
                    + " | Hero HP: " + hero.getHealth();
            result.addLine(logLine);
        }

        if (hero.isAlive() && !opponent.isAlive()) {
            result.setWinner(hero.getName());
        } else if (!hero.isAlive() && opponent.isAlive()) {
            result.setWinner(opponent.getName());
        } else if (hero.isAlive()) {
            result.setWinner(hero.getName());
        } else {
            result.setWinner(opponent.getName());
        }

        result.setRounds(round);
        return result;
    }
}
