package ru.nsu.ccfit.Game;

import java.util.ArrayList;

public class Field {
    private final ArrayList<Checker> figures;
    private CheckerColor currentTurn;

    public boolean isPossible(Move move) {
        Checker figureToMove = null;
        for (var f : figures) {
            if (move.start.equals(new Coordinates(f.x, f.y))) {
                figureToMove = f;
                break;
            }
        }
        if (figureToMove == null || figureToMove.color != currentTurn) {
            return false;
        }
        if (figureToMove.color == CheckerColor.White) {
            Checker left = null;
            Checker right = null;
            for (var f : figures) {
                if (new Coordinates(f.x, f.y).equals(new Coordinates(figureToMove.x+1, figureToMove.y+1))) {
                    right = f;
                }
                if (new Coordinates(f.x, f.y).equals(new Coordinates(figureToMove.x-1, figureToMove.y+1))) {
                    left = f;
                }
            }
            Coordinates rightMove = null;
            if (right == null) {
                if (figureToMove.x <= 7 && figureToMove.y <= 7) {
                    rightMove = new Coordinates(figureToMove.x+1, figureToMove.y+1);
                }
            } else if (right.color == CheckerColor.Black && figureToMove.x <= 6 && figureToMove.y <= 6) {
                rightMove = new Coordinates(figureToMove.x+2, figureToMove.y+2);
            }
            Coordinates leftMove = null;
            if (left == null) {
                if (figureToMove.x >= 2 && figureToMove.y <= 7) {
                    leftMove = new Coordinates(figureToMove.x-1, figureToMove.y+1);
                }
            } else if (left.color == CheckerColor.Black && figureToMove.x >= 3 && figureToMove.y <= 6) {
                leftMove = new Coordinates(figureToMove.x-2, figureToMove.y+2);
            }
            return rightMove != null && move.end.equals(rightMove) ||
                    (leftMove != null && move.end.equals(leftMove));
        } else {
            Checker left = null;
            Checker right = null;
            for (var f : figures) {
                if (new Coordinates(f.x, f.y).equals(new Coordinates(figureToMove.x+1, figureToMove.y-1))) {
                    right = f;
                }
                if (new Coordinates(f.x, f.y).equals(new Coordinates(figureToMove.x-1, figureToMove.y-1))) {
                    left = f;
                }
            }
            Coordinates rightMove = null;
            if (right == null) {
                if (figureToMove.x <= 7 && figureToMove.y >= 2) {
                    rightMove = new Coordinates(figureToMove.x+1, figureToMove.y-1);
                }
            } else if (right.color == CheckerColor.White && figureToMove.x <= 6 && figureToMove.y >= 3) {
                rightMove = new Coordinates(figureToMove.x+2, figureToMove.y-2);
            }
            Coordinates leftMove = null;
            if (left == null) {
                if (figureToMove.x >= 2 && figureToMove.y >= 2) {
                    leftMove = new Coordinates(figureToMove.x-1, figureToMove.y-1);
                }
            } else if (left.color == CheckerColor.White && figureToMove.x >= 3 && figureToMove.y >= 3) {
                leftMove = new Coordinates(figureToMove.x-2, figureToMove.y-2);
            }
            return rightMove != null && move.end.equals(rightMove) ||
                    (leftMove != null && move.end.equals(leftMove));
        }
    }

    public void refillField() {
        currentTurn = CheckerColor.White;
        figures.clear();
        for (int i = 1; i <= 8; ++i) {
            for (int j = 1; j <= 8; ++j) {
                if ((i + j) % 2 == 0) {
                    if (j <= 3) {
                        figures.add(new Checker(CheckerColor.White, CheckerType.Pawn, new Coordinates(i, j)));
                    } else if (j >= 6) {
                        figures.add(new Checker(CheckerColor.Black, CheckerType.Pawn, new Coordinates(i, j)));
                    }
                }
            }
        }
    }

    public Field() {
        currentTurn = CheckerColor.White;
        figures = new ArrayList<>();
        refillField();
    }

    public void makeMove(Move move) throws WrongMoveException {
        Checker figureToMove = null;
        for (var f : figures) {
            if (new Coordinates(f.x, f.y).equals(move.start)) {
                figureToMove = f;
            }
        }
        if (figureToMove == null || !isPossible(move)) {
            throw new WrongMoveException("Not possible move");
        }
        figureToMove.x = move.end.x;
        figureToMove.y = move.end.y;
        if (Math.abs(move.start.x - move.end.x) == 2) {
            figures.removeIf(f -> f.x == (move.start.x + move.end.x) / 2 && f.y == (move.start.y + move.end.y) / 2);
        }
        currentTurn = (currentTurn == CheckerColor.Black) ? (CheckerColor.White) : (CheckerColor.Black);
    }

    public ArrayList<Checker> getFigures() {
        return figures;
    }
}
