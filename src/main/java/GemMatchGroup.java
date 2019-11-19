package main.java;

import java.util.Set;

public class GemMatchGroup {

    private Set<Gem> gems;
    private GemType gemType;

    public GemMatchGroup(Set<Gem> gems) {
        this.gems = gems;
        gemType = gems.iterator().next().getType();
    }

    public Set<Gem> getGems() {
        return gems;
    }

    public GemType getGemType() {
        return gemType;
    }

    public int getMaxRowIndex(int col) {
        int maxRowIndex = -1;
        for (Gem gem : gems)
            if (gem.getCol() == col)
                maxRowIndex = Math.max(maxRowIndex, gem.getRow());

        return maxRowIndex;
    }

    public int getNumberGemsCleared(int col) {
        int numGemsCleared = 0;
        for (Gem gem : gems)
            if (gem.getCol() == col)
                numGemsCleared++;

        return numGemsCleared;
    }
}
