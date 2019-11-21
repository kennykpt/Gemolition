package main.java;

import java.util.Set;
import java.util.TreeSet;

public class GemMatchGroup {

    private Set<Gem> gems;
    private GemType gemType;

    public GemMatchGroup(Set<Gem> gems) {
        this.gems = gems;
        if (!gems.isEmpty())
            gemType = gems.iterator().next().getType();
    }

    public Set<Gem> getGems() {
        return gems;
    }

    public GemType getGemType() {
        return gemType;
    }

    /**
     * Order the gems in ascending row values, then column values
     */
    public TreeSet<Gem> getOrderedGems() {
        TreeSet<Gem> gems = new TreeSet<>((gem, otherGem) -> {
            if (gem.getRow() != otherGem.getRow())
                return gem.getRow() - otherGem.getRow();
            return gem.getCol() - otherGem.getCol();
        });
        gems.addAll(this.gems);
        return gems;
    }
}
