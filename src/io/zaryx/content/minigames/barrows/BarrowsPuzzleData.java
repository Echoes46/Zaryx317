package io.zaryx.content.minigames.barrows;

public enum BarrowsPuzzleData {

    ARROWS(6713, new int[] { 6716, 6717, 6718 }, new int[] { 6713, 6714, 6715 }),
    SQUARES(6719, new int[] { 6722, 6723, 6724 }, new int[] { 6719, 6720, 6721 }),
    SQUARES_OFFSET(6725, new int[] { 6728, 6729, 6730 }, new int[] { 6725, 6726, 6727 }),
    SHAPES(6731, new int[] { 6734, 6735, 6736 }, new int[] { 6731, 6732, 6733 });

    /**
     * The answer to the barrows puzzle.
     */
    private final int answer;

    /**
     * The initial puzzle sequence.
     */
    private final int[] sequence;

    /**
     * The options to finish the sequence.
     */
    private final int[] options;

    /**
     * Creates a new barrow puzzle.
     *
     * @param answer
     *            The answer to the barrows puzzle.
     * @param sequence
     *            The initial puzzle sequence.
     * @param options
     *            The options to finish the sequence.
     */
    private BarrowsPuzzleData(int answer, int[] sequence, int[] options) {
        this.answer = answer;
        this.sequence = sequence;
        this.options = options;
    }

    public static final BarrowsPuzzleData[] PUZZLES = BarrowsPuzzleData.values();

    public int getAnswer() {
        return answer;
    }

    public int getSequenceModel(int index) {
        return sequence[index];
    }

    public int[] getSequence() {
        return sequence;
    }

    public int[] getOptions() {
        return options;
    }
}