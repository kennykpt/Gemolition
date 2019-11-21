package test.java;

import main.java.Board;
import main.java.Constants;
import main.java.Gem;
import org.junit.Assert;
import org.junit.Test;

public class BoardTest {

    private Board board = new Board(Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);
    private Gem[][] gems = board.getGems();

    @Test
    public void testPopulateBoard() {
        for (int i = 0; i < gems.length; i++)
            for (int j = 0; j < gems[0].length; j++)
                Assert.assertNotNull(gems[i][j]);
    }

    @Test
    public void testSwap() {
        Gem gem = gems[0][0];
        Gem gem2 = gems[0][1];
        Gem gem3 = gems[1][0];

        board.swap(gem2, gem3);
        Assert.assertEquals(gem, gems[0][0]);
        Assert.assertEquals(gem2, gems[1][0]);
        Assert.assertEquals(gem3, gems[0][1]);

        board.swap(gem, gem2);
        Assert.assertEquals(gem, gems[1][0]);
        Assert.assertEquals(gem2, gems[0][0]);
        Assert.assertEquals(gem3, gems[0][1]);
    }

    @Test
    public void testIsNeighbor4() {
        Assert.assertTrue(board.isNeighbor4(gems[0][0], gems[0][1]));
        Assert.assertTrue(board.isNeighbor4(gems[0][1], gems[1][1]));
        Assert.assertTrue(board.isNeighbor4(gems[1][1], gems[1][0]));
        Assert.assertTrue(board.isNeighbor4(gems[1][0], gems[0][0]));
        Assert.assertFalse(board.isNeighbor4(gems[0][0], gems[1][1]));
        Assert.assertFalse(board.isNeighbor4(gems[0][1], gems[1][0]));
    }
}
