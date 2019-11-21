package main.java;

import javafx.scene.canvas.GraphicsContext;

import java.util.*;
import java.util.stream.Collectors;

public class Board {

    private Gem[][] gems;
    private Gem selectedGem = null;
    private Gem selectedGem2 = null;
    private EnumMap<GemType, Set<GemMatchGroup>> gemTypeToMatchGroup = new EnumMap<>(GemType.class);

    public Board(int boardWidth, int boardHeight) {
        gems = new Gem[boardWidth][boardHeight];
        populateBoard();
        buildGemTypeToGroupMap();
    }

    public Gem[][] getGems() {
        return gems;
    }

    public Gem getSelectedGem() {
        return selectedGem;
    }

    public Gem getSelectedGem2() {
        return selectedGem2;
    }

    public void resetSelectedGems() {
        selectedGem = null;
        selectedGem2 = null;
    }

    /**
     * Populates the board by placing gems from left to right, and then top to bottom
     */
    public void populateBoard() {
        for (int i = 0; i < gems.length; i++) {
            for (int j = 0; j < gems[0].length; j++) {
                gems[i][j] = new Gem(i, j, GemType.getRandomType());
                GemType current = gems[i][j].getType();

                // Check left
                if (j >= 2) {
                    GemType left = gems[i][j - 1].getType();
                    GemType leftTwo = gems[i][j - 2].getType();
                    if (current == left && current == leftTwo) {
                        gems[i][j] = new Gem(i, j, GemType.getRandomType());
                        j--;
                    }
                }
                // Check above
                if (i >= 2) {
                    GemType up = gems[i - 1][j].getType();
                    GemType upTwo = gems[i - 2][j].getType();
                    if (current == up && current == upTwo) {
                        gems[i][j] = new Gem(i, j, GemType.getRandomType());
                        j--;
                    }
                }
            }
        }
    }

    public void draw(GraphicsContext gc) {
        Arrays.stream(gems)
                .flatMap(Arrays::stream)
                .forEach(gem -> gem.draw(gc));
    }

    public void buildGemTypeToGroupMap() {
        for (GemType gemType : GemType.values())
            gemTypeToMatchGroup.put(gemType, new HashSet<>());
    }

    public void resetGemTypeToGroupMap() {
        for (GemType gemType : GemType.values())
            gemTypeToMatchGroup.get(gemType).clear();
    }

    public Set<GemMatchGroup> getAllMatchGroups() {
        return gemTypeToMatchGroup.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    /**
     * The row and col for each gem in the swap must be set for animation.
     * The two gems have to both swap on the physical board, along with the corresponding row, col values.
     */
    public void swap(Gem gem, Gem otherGem) {
        // Set up the animation step using starting animation state
        gem.setAnimating(true);
        gem.setBeforeSwapXAndY();
        otherGem.setAnimating(true);
        otherGem.setBeforeSwapXAndY();

        // Keeps track of the final animation state, which is the swap result
        int tempRow = gem.getRow();
        int tempCol = gem.getCol();
        Gem tempGem = gems[tempRow][tempCol];
        gems[gem.getRow()][gem.getCol()] = gems[otherGem.getRow()][otherGem.getCol()];
        gems[otherGem.getRow()][otherGem.getCol()] = tempGem;
        gem.setRowAndCol(otherGem.getRow(), otherGem.getCol());
        otherGem.setRowAndCol(tempRow, tempCol);
    }

    public boolean isNeighbor4(Gem gem, Gem otherGem) {
        if (gem.getRow() == otherGem.getRow())
            return Math.abs(gem.getCol() - otherGem.getCol()) == 1;
        else if (gem.getCol() == otherGem.getCol())
            return Math.abs(gem.getRow() - otherGem.getRow()) == 1;
        return false;
    }

    /**
     * A mouse click selects a gem if either case is satisfied:
     * 1) No gem was previously selected
     * 2) A gem was previously selected and the second gem is not a neighbor of the selected gem
     * Return false in the above cases to signify that no animation needs to be handled.
     * Return true otherwise - this implies two gems will swap.
     */
    public boolean mousePressed(int row, int col) {
        if (selectedGem == null) {
            selectedGem = gems[row][col];
            return false;
        } else {
            // If selectedGem2 is not a neighbor, then reselect
            selectedGem2 = gems[row][col];
            if (!isNeighbor4(selectedGem, selectedGem2)) {
                selectedGem = gems[row][col];
                return false;
            } else {
                swap(selectedGem, selectedGem2);
                return true;
            }
        }
    }

    /**
     * Animates descending gems above the gems that are matched and cleared
     */
    public void clearMatchedGems() {
        for (GemMatchGroup gemMatchGroup : getAllMatchGroups()) {
            int smallestRow = gemMatchGroup.getOrderedGems().iterator().next().getRow();
            for (Gem gem : gemMatchGroup.getOrderedGems()) {
                int row = gem.getRow();
                int col = gem.getCol();
                int nNewGems = row - smallestRow + 1;

                for (int i = row; i > 0; i--) {
                    Gem gemAbove = gems[i - 1][col];
                    if (!gemAbove.isInMatchGroup() && !gemAbove.isAnimating())
                        gemAbove.setBeforeSwapXAndY();
                    gemAbove.setAnimating(true);
                    gems[i][col] = gemAbove;
                    gemAbove.setRowAndCol(i, col);
                }
                Gem replacementGem = new Gem(-nNewGems, col, GemType.getRandomType());
                replacementGem.setAnimating(true);
                replacementGem.setBeforeSwapXAndY();
                gems[0][col] = replacementGem;
                replacementGem.setRowAndCol(0, col);
            }
        }
    }

    // Don't have to do this for every gem
    public void formGemGroups() {
        resetGemTypeToGroupMap();
        for (int i = 0; i < gems.length; i++) {
            for (int j = 0; j < gems[0].length; j++) {
                Gem gem = gems[i][j];

                // Optimization: any neighboring gems part of a prior match are already in gemTypeToGroup
                if (!gem.isInMatchGroup()) {
                    GemMatchGroup candidateGemMatchGroup = createNeighboringMatch(gem);
                    if (!candidateGemMatchGroup.getGems().isEmpty()) {
                        Set<GemMatchGroup> gemMatchGroups = gemTypeToMatchGroup.get(candidateGemMatchGroup.getGemType());
                        addGemMatchGroup(candidateGemMatchGroup, gemMatchGroups);
                    }
                }
            }
        }
    }

    /**
     * Add the current match group to the set if the latter is empty.
     * Otherwise, loop through the existing match groups in the set. If the current match group shares any gems in
     * common with any of the existing ones, then make a larger match group.
     */
    public void addGemMatchGroup(GemMatchGroup gemMatchGroup, Set<GemMatchGroup> gemMatchGroups) {
        if (!gemMatchGroups.isEmpty()) {
            for (Iterator<GemMatchGroup> it = gemMatchGroups.iterator(); it.hasNext(); ) {
                GemMatchGroup existingGemMatchGroup = it.next();

                // Create temporary gem storage for set intersections
                Set<Gem> tempGems = new HashSet<>(gemMatchGroup.getGems());
                tempGems.retainAll(existingGemMatchGroup.getGems());  // does this work?
                if (!tempGems.isEmpty()) {
                    it.remove();

                    gemMatchGroup.getGems().addAll(existingGemMatchGroup.getGems());
                    gemMatchGroups.add(gemMatchGroup);
                }
            }
        }
        else
            gemMatchGroups.add(gemMatchGroup);
    }

    /**
     * Focus on a single gem and check whether it participates in a match group.
     */
    public GemMatchGroup createNeighboringMatch(Gem gem) {
        int row = gem.getRow();
        int col = gem.getCol();
        GemType type = gem.getType();

        Set<Gem> gemsToFormMatchGroup = new HashSet<>();

        // Check for column match
        for (int i = row - 2; i <= row + 2; i++) {
            if (gemsToFormMatchGroup.size() < 3 && GameFlow.isInsideBoard(i, col)) {
                Gem currentGem = gems[i][col];
                if (currentGem.getType() == type)
                    gemsToFormMatchGroup.add(currentGem);
                else
                    gemsToFormMatchGroup.clear();
            }
        }

        // Check for row match
        if (gemsToFormMatchGroup.size() < 3) {
            gemsToFormMatchGroup.clear();
            for (int j = col - 2; j <= col + 2; j++) {
                if (gemsToFormMatchGroup.size() < 3 && GameFlow.isInsideBoard(row, j)) {
                    Gem currentGem = gems[row][j];
                    if (currentGem.getType() == type)
                        gemsToFormMatchGroup.add(currentGem);
                    else
                        gemsToFormMatchGroup.clear();
                }
            }
        }

        // If count is ever 3, then setInGroup = true
        if (gemsToFormMatchGroup.size() == 3)
            for (Gem gemInMatchGroup : gemsToFormMatchGroup)
                gemInMatchGroup.setInMatchGroup(true);
        else
            gemsToFormMatchGroup.clear();

        // Can be empty
        return new GemMatchGroup(gemsToFormMatchGroup);
    }
}
