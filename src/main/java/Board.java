package main.java;

import javafx.scene.canvas.GraphicsContext;

import java.util.*;
import java.util.stream.Collectors;

public class Board {

    private Gem[][] gems;
    private Gem selectedGem = null;
    private Gem selectedGem2 = null;
    private EnumMap<GemType, Set<GemMatchGroup>> gemTypeToGroup = new EnumMap<>(GemType.class);

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

    // Populates the board by placing gems from left to right, top to bottom
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

    public void buildGemTypeToGroupMap() {
        for (GemType gemType : GemType.values())
            gemTypeToGroup.put(gemType, new HashSet<>());
    }

    public void draw(GraphicsContext gc) {
        Arrays.stream(gems)
                .flatMap(Arrays::stream)
                .forEach(gem -> gem.draw(gc));
    }

    /**
     * Make sure row and col are set for each gem for animation
     */
    public void swap(Gem gem, Gem otherGem) {
        gem.setBeforeSwapXAndY();
        otherGem.setBeforeSwapXAndY();

        int tempRow = gem.getRow();
        int tempCol = gem.getCol();
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
                selectedGem.setAnimating(true);
                selectedGem2.setAnimating(true);
                swap(selectedGem, selectedGem2);
                return true;
            }
        }
    }

    public void clearMatchedGems() {
        for (GemMatchGroup gemMatchGroup : getAllGroups()) {
            for (int j = 0; j < gems[0].length; j++) {
                int maxRowIndex = gemMatchGroup.getMaxRowIndex(j);
                int totalGemsCleared = gemMatchGroup.getNumberGemsCleared(j);
                updateColumn(maxRowIndex, j, totalGemsCleared);
            }
        }
    }

    // maxRowIndex is index of bottom-most cleared gem in column col
    // totalGemsCleared in column col
    public void updateColumn(int maxRowIndex, int col, int totalGemsCleared) {
        int numGemsMoved = 0;
        for (int i = maxRowIndex; i >= 0; i--) {
            if (gems[i][col].isInGroup()) {
                gems[i][col].setInGroup(false);
                gems[maxRowIndex - numGemsMoved][col] = gems[i][col];
                numGemsMoved++;
            }
        }
        for (int i = maxRowIndex - totalGemsCleared; i >= 0; i--)
            gems[i][col] = new Gem(i, col, GemType.getRandomType());
    }

    public Set<GemMatchGroup> getAllGroups() {
        return gemTypeToGroup.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    // Don't have to do this for every gem
    public void formGemGroups() {
        for (int i = 0; i < gems.length; i++) {
            for (int j = 0; j < gems[0].length; j++) {
                Gem gem = gems[i][j];

                // Optimization: any neighboring gems part of a prior match are already in gemTypeToGroup
                if (!gem.isInGroup()) {
                    GemMatchGroup candidateGemMatchGroup = createNeighboringMatch(gem);
                    Set<GemMatchGroup> gemMatchGroups = gemTypeToGroup.get(candidateGemMatchGroup.getGemType());
                    addGemGroup(candidateGemMatchGroup, gemMatchGroups);  // does this already add to the map?
                }
            }
        }
    }

    // Should this be void?
    public void addGemGroup(GemMatchGroup gemMatchGroup, Set<GemMatchGroup> gemMatchGroups) {
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

    public GemMatchGroup createNeighboringMatch(Gem gem) {
        int row = gem.getRow();
        int col = gem.getCol();
        GemType type = gem.getType();

        Set<Gem> gemsToFormMatchGroup = new HashSet<>();

        // Check for column match
        for (int i = row - 2; i <= row + 2; i++) {
            if (gemsToFormMatchGroup.size() < 2) {
                Gem currentGem = gems[i][col];
                if (currentGem.getType() == type)
                    gemsToFormMatchGroup.add(currentGem);
                else
                    gemsToFormMatchGroup.clear();
            }
        }

        // Check for row match
        for (int j = col - 2; j <= col + 2; j++) {
            if (gemsToFormMatchGroup.size() < 2) {
                Gem currentGem = gems[row][j];
                if (currentGem.getType() == type)
                    gemsToFormMatchGroup.add(currentGem);
                else
                    gemsToFormMatchGroup.clear();
            }
        }

        // If count is ever 2, then setInGroup = true
        if (gemsToFormMatchGroup.size() == 2)
            for (Gem gemInMatchGroup : gemsToFormMatchGroup)
                gemInMatchGroup.setInGroup(true);

        // Can be empty
        return new GemMatchGroup(gemsToFormMatchGroup);
    }
}
