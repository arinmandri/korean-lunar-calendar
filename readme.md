xyz.arinmandri.koreanLunarCalendar

음력

음력 날짜 구하는 법: 규칙은 모르겠고 그냥 한국천문연구원에서 제공하는 음력 월별 1일의 양력 날짜 하고 대월/소월 값만으로 일수 세기 했음.

## 등장인물

##### KLunarDate

우리의 주인공. `java.time.LocalDate`처럼 썼으면 좋겠다고 생각했다.

##### NonexistentDateException

없는 날짜(25월 99일이라든가)를 생성하려면 생기는 예외.

##### OutOfRangeException

지원 범위 밖의 날짜를 생성하려면 생기는 예외. 음력의 지원범위는 양력인 LocalDate에 비하면 아주 작다.
