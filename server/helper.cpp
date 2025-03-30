#include "helper.h"

using namespace std;

int convertDayTimeToInt(DayTime time) {
    // convert DayTime to time in min
    int res = 0;
    res += time.minute;
    res += time.hour * 60;
    res += time.day * 24 * 60;
}
DayTime convertIntToDayTime(int time) {
    DayTime res;
    res.minute = time % 60;
    time /= 60;
    res.hour = time % 24;
    time /= 24;
    res.day = (Day)time;

    return res;
}