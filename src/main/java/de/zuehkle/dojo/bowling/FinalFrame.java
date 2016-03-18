package de.zuehkle.dojo.bowling;

import java.util.List;

/**
 * The final frame can have three rolls and has a different definition of what a spare/strike is
 */
public class FinalFrame extends Frame {
    public static final Integer MAX_ROLLS_FOR_SPARES_AND_STRIKES = 3;

    public FinalFrame(int pins) {
        super(pins);
    }

    @Override
    public boolean isFinished() {
        if (isSpare() || isStrike()) {
            return getPinsRolled().size() == MAX_ROLLS_FOR_SPARES_AND_STRIKES;
        } else {
            return getPinsRolled().size() == MAX_ROLLS;
        }
    }

    @Override
    public boolean isSpare() {
        List<Integer> pins = getPinsRolled();
        return (pins.size() > 1 && pins.get(0) + pins.get(1) == 10);
    }

    @Override
    public boolean isStrike() {
        List<Integer> pins = getPinsRolled();
        return (pins.size() > 0 && pins.get(0) == 10);
    }

    @Override
    public boolean claimsRolls() {
        return false; // The final roll can never claim rolls
    }
}
