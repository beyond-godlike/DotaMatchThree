#pragma once

#include <vector>
#include <string>
#include <random>
#include "src/main/java/com/example/dotamatchthree/presentation/ui/game/GameTypes.h"

class GameCore {
public:
    // Конструктор
    GameCore();

    // --- Публичный API для вызова из Kotlin ---
    void init(const Level& lvl);
    void prepareSwap(int i, int j, const std::string& direction);
    void swap();
    void findMatches();
    void crush();
    bool needsToDrop();
    void drop();
    void fillTopBoard();

    // --- Геттеры для получения состояния ---
    const std::vector<std::vector<Hero>>& getBoard() const;
    int getMoves() const;
    int getGoal() const;
    bool wasSwapped() const;
    void resetSwappedFlag();


private:
    // --- Приватные поля (состояние игры) ---
    Level level;
    std::vector<std::vector<Hero>> board;
    std::vector<Hero> topBoard;
    std::vector<std::vector<Point>> search;

    int moves;
    int goal;

    // Состояние для обмена
    int posI, posJ;
    int newPosI, newPosJ;
    std::string direction;

    bool swapped;
    bool dropStop;

    // Генератор случайных чисел
    std::mt19937 rng;

    // --- Приватные вспомогательные методы ---
    int generateNewJewel();
    bool allowCrushing(const std::vector<Point>& points);
};