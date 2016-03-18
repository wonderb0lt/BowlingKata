package de.zuehkle.dojo.bowling;

import com.google.common.collect.Lists;

import java.util.*;

public class Game {
    private static final Integer MAX_FRAMES = 10;

    private LinkedList<Frame> frames = Lists.newLinkedList();

    public void addRoll(int pins) {
        if (this.isOver()) {
            throw new IllegalStateException("Can't add a roll to a finished game");
        }

        if (isInitialRoll()) {
            handleInitialRoll(pins);
        } else {
            handleRollDuringGame(pins);

        }

    }

    private void handleInitialRoll(int pins) {
        frames.add(new Frame(pins));
    }

    private void handleRollDuringGame(int pins) {
        // We need to propagate the score before we (possibly) add a new frame
        // Otherwise, it may claim it's own score (when it becomes a strike/spare
        backpropagateScore(pins);

        Frame currentFrame = frames.getLast();

        if (currentFrame.isFinished()) {
            frames.add(createFrameForCurrentState(pins));
        } else {
            currentFrame.addRoll(pins);
        }
    }

    private Frame createFrameForCurrentState(int pins) {
        if (frames.size() < MAX_FRAMES - 1) {
            return new Frame(pins);
        } else {
            return new FinalFrame(pins);
        }
    }

    private void backpropagateScore(int pins) {
        frames.stream()
                .filter(Frame::claimsRolls) // Only look at frames that still need backpropagation
                .forEach(f -> f.addClaimedScore(pins)); // Give them the delicious points!
    }

    private boolean isInitialRoll() {
        return frames.isEmpty();
    }

    public LinkedList<Frame> getFrames() {
        return frames;
    }

    public int getTotalScore() {
        // Sum up the scores of the single scores.
        return frames.stream()
                .mapToInt(Frame::getScore)
                .sum();
    }

    public boolean isOver() {
        return frames.size() == 10 && frames.getLast().isFinished();
    }
}
