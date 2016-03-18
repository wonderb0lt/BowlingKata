package de.zuehlke.dojo.bowling;

import de.zuehkle.dojo.bowling.Game;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class GameLogicTest {
    private Game game;

    @Before
    public void setUp() throws Exception {
        game = new Game();
    }

    @Test
    public void testRollingAllZeroesGivesZeroScore() {
        for (int i = 0; i < 20; i++) {
            game.addRoll(0);
        }

        assertThat(game.getTotalScore(), is(0));
    }

    @Test
    public void rollOneGivesOneScore() {
        game.addRoll(1);

        assertThat(game.getTotalScore(), is(1));
    }

    @Test
    public void rollOneAndFourGivesScoreFive() {
        addRolls(1, 4);

        assertThat(game.getTotalScore(), is(5));
        assertThat(game.getFrames(), hasSize(1));
    }

    @Test
    public void rollThreeRollsGivesTwoFrames() {
        addRolls(1, 4, 4);

        assertThat(game.getFrames(), hasSize(2));
    }

    @Test
    public void rollingSparePropagatesNextRollBack() {
        addRolls(6, 4, 5);

        assertThat(game.getFrames(), hasSize(2));
        assertThat(game.getTotalScore(), is(20));
    }

    @Test
    public void rollingSpareDoesNotPropagateNextTwoRollsBack() {
        addRolls(6, 4, 5, 1);
        assertFrameScores(game, 15, 6);
        assertThat(game.getTotalScore(), is(21));
    }

    @Test
    public void rollingStrikePropagatesNextTwoRollsBack() {
        addRolls(10, 1, 1);

        assertFrameScores(game, 12, 2);
        assertThat(game.getTotalScore(), is(14));
    }

    @Test
    public void rollingStrikeDoesNotPropagateThirdRollBack() {
        addRolls(10, 1, 1, 1);

        assertThat(game.getFrames(), hasSize(3));
        assertFrameScores(game, 12, 2, 1);
        assertThat(game.getTotalScore(), is(15));
    }

    @Test
    public void testRollingTwoConsecutiveSpares() {
        addRolls(6, 4, 7, 3, 1, 1);

        int expectedFirstFrameScore = 10 + 7; // Spare + next roll
        int expectedSecondFrameScore = 10 + 1;
        int expectedThirdFrameScore = 2;
        int expectedFinalScore = expectedFirstFrameScore + expectedSecondFrameScore + expectedThirdFrameScore;

        assertFrameScores(game,
                10 + 7, // Spare + next roll
                10 + 1, // Same
                2 // Regular frame
        );
        assertThat(game.getTotalScore(), is(10 + 7 + 10 + 1 + 2));
    }

    @Test
    public void testRollingTwoConsecutiveStrikes() {
        addRolls(10, 10, 3, 4);

        int expectedFirstFrameScore = 10 + 10 + 3; // First strike + next 2 rolls (one of them is also a strike)
        int expectedSecondFrameScore = 10 + 3 + 4; // Second strike + next 2 rolls
        int expectedThirdFrameScore = 3 + 4; // Regular frame
        int expectedFinalScore = expectedFirstFrameScore + expectedSecondFrameScore + expectedThirdFrameScore;

        assertFrameScores(game, expectedFirstFrameScore, expectedSecondFrameScore, expectedThirdFrameScore);
        assertThat(game.getTotalScore(), is(expectedFinalScore));
    }

    @Test
    public void testAlmostFinishedGameIsntOver() {
        playNineFramesWithZeroPins();
        addRolls(3);

        assertThat(game.getFrames(), hasSize(10));
        assertThat(game.getTotalScore(), is(3));
        assertThat(game.getFrames().getLast().getScore(), is(3));
        assertThat(game.isOver(), is(false));
    }

    @Test
    public void testFinalFrameIsRegular() {
        playNineFramesWithZeroPins();
        addRolls(3, 4);

        assertThat(game.getFrames(), hasSize(10));
        assertThat(game.getTotalScore(), is(7));
        assertThat(game.getFrames().getLast().getScore(), is(7));
        assertThat(game.isOver(), is(true));
    }

    @Test
    public void testFinalFrameIsSpare() {
        playNineFramesWithZeroPins();
        addRolls(3, 7, 4);

        assertThat(game.getFrames(), hasSize(10));
        assertThat(game.getTotalScore(), is(14));
        assertThat(game.getFrames().getLast().getScore(), is(14));
        assertThat(game.getFrames().getLast().getPinsRolled(), hasSize(3));
        assertThat(game.getFrames().getLast().getPinsRolled(), contains(3, 7, 4));
        assertThat(game.isOver(), is(true));
    }

    @Test
    public void testFinalFrameIsStrike() {
        playNineFramesWithZeroPins();
        addRolls(10, 2, 4);

        assertThat(game.getFrames(), hasSize(10));
        assertThat(game.getTotalScore(), is(16));
        assertThat(game.getFrames().getLast().getScore(), is(16));
        assertThat(game.getFrames().getLast().getPinsRolled(), hasSize(3));
        assertThat(game.getFrames().getLast().getPinsRolled(), contains(10, 2, 4));
        assertThat(game.isOver(), is(true));
    }

    @Test
    public void playBobsGame() {
        addRolls(1, 4, 4, 5, 6, 4, 5, 5, 10, 0, 1, 7, 3, 6, 4, 10, 2, 8, 6);
        assertThat(game.isOver(), is(true));
        assertThat(game.getTotalScore(), is(133));
    }

    @Test(expected = IllegalStateException.class)
    public void testCantContinueFinishedGame() {
        playNineFramesWithZeroPins();
        addRolls(0, 0);
        addRolls(0);
    }

    private void playNineFramesWithZeroPins() {
        for (int i = 0; i < 9; i++) {
            addRolls(0, 0); // Boo!
        }
    }

    private void addRolls(int... rolls) {
        for (int roll : rolls) {
            game.addRoll(roll);
        }
    }

    private static void assertFrameScores(Game game, int... scores) {
        assertThat("Game does not have enough frames to assert frame scores", game.getFrames(), hasSize(scores.length));

        for (int i = 0; i < scores.length; i++) {
            int expectedScore = scores[i];
            int actualScore = game.getFrames().get(i).getScore();

            assertThat("Score for frame " + i + " does not match", actualScore, is(expectedScore));
        }
    }

}
