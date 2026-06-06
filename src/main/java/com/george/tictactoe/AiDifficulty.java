package com.george.tictactoe;

public enum AiDifficulty {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard");

    private final String label;

    AiDifficulty(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
