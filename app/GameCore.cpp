#include "GameCore.h"
#include "src/main/java/com/example/dotamatchthree/presentation/ui/game/GameConstants.h"
#include <algorithm> // Для std::swap

GameCore::GameCore() : swapped(false), dropStop(true) {
    // Инициализация генератора случайных чисел
    std::random_device rd;
    rng = std::mt19937(rd());
}

void GameCore::init(const Level& lvl) {
    level = lvl;
    moves = level.moves;
    goal = level.goal;

    board.assign(BOARD_HEIGHT, std::vector<Hero>(BOARD_WIDTH));
    topBoard.assign(BOARD_WIDTH, Hero{0,0,0});

    std::vector<std::vector<int>> grid(BOARD_HEIGHT, std::vector<int>(BOARD_WIDTH));

    for (int i = 0; i < BOARD_HEIGHT; ++i) {
        for (int j = 0; j < BOARD_WIDTH; ++j) {
            grid[i][j] = generateNewJewel();
        }
    }

    for (int i = 0; i < BOARD_HEIGHT; ++i) {
        for (int j = 0; j < BOARD_WIDTH; ++j) {
            board[i][j] = Hero{
                    DRAW_X + CELL_WIDTH * j,
                    DRAW_Y + CELL_WIDTH * i,
                    grid[i][j]
            };
        }
    }
}

int GameCore::generateNewJewel() {
    std::uniform_int_distribution<int> distrib(level.rangeFrom, level.rangeTo -1);
    return distrib(rng);
}

void GameCore::prepareSwap(int i, int j, const std::string& dir) {
    posI = i;
    posJ = j;
    direction = dir;

    if (direction == "right") { newPosI = i; newPosJ = j + 1; }
    else if (direction == "left") { newPosI = i; newPosJ = j - 1; }
    else if (direction == "up") { newPosI = i - 1; newPosJ = j; }
    else if (direction == "down") { newPosI = i + 1; newPosJ = j; }
}

void GameCore::swap() {
    // Важно: в C++ ядре мы меняем только логическое положение.
    // Анимацию движения и изменение posX/posY лучше делать в Kotlin.
    // Здесь мы просто меняем элементы в массиве.
    std::swap(board[posI][posJ], board[newPosI][newPosJ]);
    moves--;

    // Если вам все же нужно сохранить логику позиций:
    board[posI][posJ].posX = DRAW_X + CELL_WIDTH * posJ;
    board[posI][posJ].posY = DRAW_Y + CELL_WIDTH * posI;
    board[newPosI][newPosJ].posX = DRAW_X + CELL_WIDTH * newPosJ;
    board[newPosI][newPosJ].posY = DRAW_Y + CELL_WIDTH * newPosI;
}


void GameCore::findMatches() {
    search.clear();
    swapped = false;

    // Поиск по горизонтали
    for (int i = 0; i < BOARD_HEIGHT; ++i) {
        for (int j = 0; j < BOARD_WIDTH - 2; ) {
            if (board[i][j].color > 0) {
                int color = board[i][j].color;
                int k = j + 1;
                while (k < BOARD_WIDTH && board[i][k].color == color) {
                    k++;
                }
                if (k - j >= 3) {
                    std::vector<Point> match;
                    for (int m = j; m < k; ++m) {
                        match.push_back({i, m});
                        if (color == level.goalType) goal--;
                    }
                    search.push_back(match);
                    swapped = true;
                }
                j = k;
            } else {
                j++;
            }
        }
    }

    // Поиск по вертикали
    for (int j = 0; j < BOARD_WIDTH; ++j) {
        for (int i = 0; i < BOARD_HEIGHT - 2; ) {
            if (board[i][j].color > 0) {
                int color = board[i][j].color;
                int k = i + 1;
                while (k < BOARD_HEIGHT && board[k][j].color == color) {
                    k++;
                }
                if (k - i >= 3) {
                    std::vector<Point> match;
                    for (int m = i; m < k; ++m) {
                        match.push_back({m, j});
                        if (color == level.goalType) goal--;
                    }
                    search.push_back(match);
                    swapped = true;
                }
                i = k;
            } else {
                i++;
            }
        }
    }

    // Фильтруем матчи, которые нельзя сейчас уничтожить (висящие в воздухе)
    // Этот код был в вашем оригинале, но может быть не нужен, если падение происходит сразу
    // auto it = std::remove_if(search.begin(), search.end(), [this](const auto& match) {
    //     return !allowCrushing(match);
    // });
    // search.erase(it, search.end());
}


bool GameCore::allowCrushing(const std::vector<Point>& points) {
    for(const auto& p : points) {
        if (p.x < BOARD_HEIGHT - 1 && board[p.x + 1][p.y].color == 0) {
            return false;
        }
    }
    return true;
}


void GameCore::crush() {
    for (const auto& match : search) {
        for (const auto& point : match) {
            board[point.x][point.y].color = 0;
        }
    }
    search.clear();
}

bool GameCore::needsToDrop() {
    for (int i = 0; i < BOARD_HEIGHT; ++i) {
        for (int j = 0; j < BOARD_WIDTH; ++j) {
            if (board[i][j].color == 0) {
                return true;
            }
        }
    }
    return false;
}

void GameCore::fillTopBoard() {
    for (int j = 0; j < BOARD_WIDTH; ++j) {
        if (topBoard[j].color == 0) {
            topBoard[j].color = generateNewJewel();
            topBoard[j].posX = DRAW_X + j * CELL_WIDTH;
            topBoard[j].posY = DRAW_Y - CELL_WIDTH; // Позиция над полем
        }
    }
}

void GameCore::drop() {
    // Падение из верхнего ряда в основной
    for (int j = 0; j < BOARD_WIDTH; ++j) {
        if (board[0][j].color == 0 && topBoard[j].color > 0) {
            board[0][j].color = topBoard[j].color;
            topBoard[j].color = 0;
        }
    }

    // Падение внутри основного поля
    for (int i = BOARD_HEIGHT - 2; i >= 0; --i) {
        for (int j = 0; j < BOARD_WIDTH; ++j) {
            if (board[i+1][j].color == 0 && board[i][j].color > 0) {
                board[i+1][j].color = board[i][j].color;
                board[i][j].color = 0;
            }
        }
    }
}

// Геттеры и сеттеры
const std::vector<std::vector<Hero>>& GameCore::getBoard() const { return board; }
int GameCore::getMoves() const { return moves; }
int GameCore::getGoal() const { return goal; }
bool GameCore::wasSwapped() const { return swapped; }
void GameCore::resetSwappedFlag() { swapped = false; }