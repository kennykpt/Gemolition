package main.java;

import javafx.scene.canvas.GraphicsContext;

import java.util.*;
import java.util.stream.Collectors;

public class Board {

    private Gem[][] gems;
    private List<Gem> gemsToSwap = new ArrayList<>();
    private Gem selectedGem = null;
    private EnumMap<GemType, Set<GemGroup>> gemTypeToGroup = new EnumMap<>(GemType.class);

    public Board(int boardWidth, int boardHeight) {
        gems = new Gem[boardWidth][boardHeight];
        populateBoard();
        buildGemTypeToGroupMap();
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
        for (int i = 0; i < gems.length; i++)
            for (int j = 0; j < gems[0].length; j++)
                gems[i][j].draw(gc);
    }

    public void drawSwap(GraphicsContext gc) {
        for (Gem gem : gemsToSwap) {
            gems[gem.getRow()][gem.getCol()].removeGem(gc);
            gems[gem.getRow()][gem.getCol()].draw(gc);
        }
    }

    // General swap algorithm for GemType, does not depend on neighbors
    // Eventually animate so that the rows and cols need switching too
    public void swap(Gem gem, Gem otherGem) {
        GemType tempType = gems[gem.getRow()][gem.getCol()].getType();
        gems[gem.getRow()][gem.getCol()].setType(otherGem.getType());
        gems[otherGem.getRow()][otherGem.getCol()].setType(tempType);
    }

    public boolean isNeighbor4(Gem gem, Gem otherGem) {
        if (gem.getRow() == otherGem.getRow())
            return Math.abs(gem.getCol() - otherGem.getCol()) == 1;
        else if (gem.getCol() == otherGem.getCol())
            return Math.abs(gem.getRow() - otherGem.getRow()) == 1;
        return false;
    }

    public void mouseClicked(int row, int col) {
        if (selectedGem == null)
            selectedGem = gems[row][col];
        else {
            // If selectedGem2 is not a neighbor, then reselect
            Gem selectedGem2 = gems[row][col];
            if (!isNeighbor4(selectedGem, selectedGem2))
                selectedGem = gems[row][col];
            else {
                swap(selectedGem, selectedGem2);
                gemsToSwap.add(gems[selectedGem.getRow()][selectedGem.getCol()]);
                gemsToSwap.add(gems[selectedGem2.getRow()][selectedGem2.getCol()]);
                // Check for a match before swapping back
                selectedGem = null;
            }
        }
    }

    public void clearMatchedGems() {
        for (GemGroup gemGroup : getAllGroups()) {
            for (int j = 0; j < gems[0].length; j++) {
                int maxRowIndex = gemGroup.getMaxRowIndex(j);
                int totalGemsCleared = gemGroup.getNumberGemsCleared(j);
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

    public Set<GemGroup> getAllGroups() {
        return gemTypeToGroup.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    // Don't have to do this for every gem
    public void formGemGroups() {
        for (int i = 0; i < gems.length; i++) {
            for (int j = 0; j < gems[0].length; j++) {
                Gem gem = gems[i][j];

                // Optimization: any neighboring gems part of a prior match are already in gemTypeToGroup
                if (!gem.isInGroup()) {
                    GemGroup candidateGemGroup = createNeighboringMatch(gem);
                    Set<GemGroup> gemGroups = gemTypeToGroup.get(candidateGemGroup.getGemType());
                    addGemGroup(candidateGemGroup, gemGroups);  // does this already add to the map?
                }
            }
        }
    }

    // Should this be void?
    public void addGemGroup(GemGroup gemGroup, Set<GemGroup> gemGroups) {
        for (Iterator<GemGroup> it = gemGroups.iterator(); it.hasNext(); ) {
            GemGroup existingGemGroup = it.next();

            // Create temporary gem storage for set intersections
            Set<Gem> tempGems = new HashSet<>(gemGroup.getGems());
            tempGems.retainAll(existingGemGroup.getGems());  // does this work?
            if (!tempGems.isEmpty()) {
                it.remove();

                gemGroup.getGems().addAll(existingGemGroup.getGems());
                gemGroups.add(gemGroup);
            }
        }
    }

    public GemGroup createNeighboringMatch(Gem gem) {
        int row = gem.getRow();
        int col = gem.getCol();
        GemType type = gem.getType();

        Set<Gem> gemsToFormGroup = new HashSet<>();

        // Check for column match
        for (int i = row - 2; i <= row + 2; i++) {
            if (gemsToFormGroup.size() < 2) {
                Gem currentGem = gems[i][col];
                if (currentGem.getType() == type)
                    gemsToFormGroup.add(currentGem);
                else
                    gemsToFormGroup.clear();
            }
        }

        // Check for row match
        for (int j = col - 2; j <= col + 2; j++) {
            if (gemsToFormGroup.size() < 2) {
                Gem currentGem = gems[row][j];
                if (currentGem.getType() == type)
                    gemsToFormGroup.add(currentGem);
                else
                    gemsToFormGroup.clear();
            }
        }

        // If count is ever 2, then setInGroup = true
        if (gemsToFormGroup.size() == 2)
            for (Gem gemInGroup : gemsToFormGroup)
                gemInGroup.setInGroup(true);

        // Can be empty
        return new GemGroup(gemsToFormGroup);
    }

    public static boolean isInsideBoard(int row, int col) {
        return row >= 0 && row <= Constants.BOARD_HEIGHT && col >= 0 && col <= Constants.BOARD_WIDTH;
    }
}
