#include <jni.h>
#include "GameCore.h"

// Глобальный указатель на наш игровой движок
GameCore* gameCore = nullptr;

extern "C" {

JNIEXPORT void JNICALL
Java_com_example_dotamatchthree_presentation_ui_game_Game_nativeInit(
        JNIEnv *env,
jobject /* this */) {
// Создаем экземпляр ядра игры
if(gameCore == nullptr) {
gameCore = new GameCore();
}

// Здесь нужно получить данные об уровне из Kotlin/Java
// Для простоты, создадим тестовый уровень здесь
Level level;
level.moves = 20;
level.goal = 15;
level.goalType = 1; // ID первого героя
level.rangeFrom = 1; // ID героев от
level.rangeTo = 6;   // ID героев до

gameCore->init(level);
}


JNIEXPORT void JNICALL
Java_com_example_dotamatchthree_presentation_ui_game_Game_nativePrepareSwap(
        JNIEnv *env,
jobject /* this */,
jint i, jint j, jstring direction) {
if(gameCore == nullptr) return;
const char *dirStr = env->GetStringUTFChars(direction, 0);
gameCore->prepareSwap(i, j, std::string(dirStr));
env->ReleaseStringUTFChars(direction, dirStr);
}

JNIEXPORT void JNICALL
Java_com_example_dotamatchthree_presentation_ui_game_Game_nativeFinalizeSwap(JNIEnv *env, jobject thiz) {
if (gameCore != nullptr) gameCore->swap();
}


JNIEXPORT void JNICALL
Java_com_example_dotamatchthree_presentation_ui_game_Game_nativeProcessTurn(JNIEnv *env, jobject thiz) {
if(gameCore == nullptr) return;

do {
gameCore->findMatches();
if (gameCore->wasSwapped()) {
gameCore->crush();

while(gameCore->needsToDrop()) {
gameCore->drop();
gameCore->fillTopBoard();
}
}
} while (gameCore->wasSwapped());
}

JNIEXPORT jintArray JNICALL
Java_com_example_dotamatchthree_presentation_ui_game_Game_nativeGetBoardState(
        JNIEnv *env,
        jobject /* this */) {
    if (gameCore == nullptr) return nullptr;

    const auto& board = gameCore->getBoard();
    jintArray resultArray = env->NewIntArray(BOARD_WIDTH * BOARD_HEIGHT);
    jint temp[BOARD_WIDTH * BOARD_HEIGHT];

    int index = 0;
    for (int i = 0; i < BOARD_HEIGHT; ++i) {
        for (int j = 0; j < BOARD_WIDTH; ++j) {
            temp[index++] = board[i][j].color;
        }
    }
    env->SetIntArrayRegion(resultArray, 0, BOARD_WIDTH * BOARD_HEIGHT, temp);
    return resultArray;
}


JNIEXPORT jint JNICALL
        Java_com_example_dotamatchthree_presentation_ui_game_Game_nativeGetMoves(JNIEnv *env, jobject thiz) {
if(gameCore == nullptr) return 0;
return gameCore->getMoves();
}

JNIEXPORT jint JNICALL
        Java_com_example_dotamatchthree_presentation_ui_game_Game_nativeGetGoal(JNIEnv *env, jobject thiz) {
if(gameCore == nullptr) return 0;
return gameCore->getGoal();
}


JNIEXPORT void JNICALL
Java_com_example_dotamatchthree_presentation_ui_game_Game_nativeDestroy(
        JNIEnv *env,
jobject /* this */) {
delete gameCore;
gameCore = nullptr;
}

} // extern "C"