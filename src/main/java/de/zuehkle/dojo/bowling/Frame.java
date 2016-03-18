package de.zuehkle.dojo.bowling;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Represents a single frame of the game.
 *
 * Frames have the concept of "claimed" points, which means that if the frame turns into a spare or a strike, it will
 * claim the next 1 or 2 rolls. The owning instance (e.g. {@link Game}) is responsible for giving the outstanding claimed
 * points to the frame instance. The frame instance is responsible for keeping track of how many claimed rolls are still
 * outstanding.
 */
public class Frame {
    public static final Integer MAX_ROLLS = 2;

    private List<Integer> pinsRolled = Lists.newArrayListWithCapacity(3);
    private int score;
    private int claimedRolls = 0;

    public Frame(int pins) {
        addRoll(pins);
    }

    public void addRoll(int pins) {
        if (this.isFinished()) {
            throw new IllegalStateException("Trying to add roll to finished frame");
        }

        this.pinsRolled.add(pins);
        this.score += pins;
        this.claimedRolls = calculateClaimedRolls();
    }

    private int calculateClaimedRolls() {
        if (this.isSpare()) {
            return 1;
        } else if (this.isStrike()) {
            return 2;
        } else {
            return 0;
        }
    }

    public int getScore() {
        return score;
    }

    public boolean isFinished() {
        return pinsRolled.size() == MAX_ROLLS // Standard frame
                || isSpare()
                || isStrike();
    }

    public boolean isSpare() {
        return pinsRolled.size() == 2 && score >= 10;
    }


    public boolean isStrike() {
        return pinsRolled.size() == 1 && score >= 10;
    }

    public void addClaimedScore(int score) {
        this.score += score; // Yay delicious pins
        this.claimedRolls -= 1; // We received a score - we now need one claim less!
    }

    public boolean claimsRolls() {
        return claimedRolls > 0;
    }

    public List<Integer> getPinsRolled() {
        return pinsRolled;
    }
}
