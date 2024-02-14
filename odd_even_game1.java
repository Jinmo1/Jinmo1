//홀짝과 승패 결과를 나타내는 2개의 Enum 파일
public enum Number {
    ODD, //홀
    EVEN //짝
}

public enum Result {
    WIN,
    LOSE
}

/* 임시 1


Main
//사용자의 선택 값 인자를 받아 OddEvenGame(홀짝 게임) 객체의 play 실행
public class Main {
    
    public final static OddEvenGame game = new OddEvenGame();
    
    public static void main(String[] args) {
        playGame(Number.EVEN);
        playGame(Number.ODD);
        playGame(Number.EVEN);
    }

    public static void playGame(Number input){
        System.out.println("============");
        System.out.println("Game Start");
        System.out.println("My : " + input.name());
        System.out.println("Result : " + game.play(input));
        System.out.println("Game End");
        System.out.println("============");
    }
}

OddEvenGame
/* < play 함수 >
  홀짝 중 하나를 리턴하는 Private Method 를 호출하여 컴퓨터의 값을 얻고, 이 값을 사용자로부터 받은 값과 비교하여 결과를 리턴
< generateOddOrEven 함수 >
  랜덤 유틸을 이용하여 랜덤 값를 얻어 홀짝으로 치환 후 리턴 */
  
public class OddEvenGame {

    public Result play(Number my){
        Number computer = generateOddOrEven();
        System.out.println("Computer : " + computer.name());
        return computer == my ? Result.WIN : Result.LOSE;
    }
    
    private Number generateOddOrEven(){
        int random = NumberUtil.generateRandom();
        System.out.println("Random : " + random);
        return random % 2 == 1 ? Number.ODD : Number.EVEN;
    }
}
NumberUtil
//홀짝을 만들기 위한 랜덤 값을 생성 유틸
public class NumberUtil {

    /** 홀짝을 위한 숫자 랜덤 생성 */
    public static int generateRandom(){
        return new Random().nextInt(2)+1;
    }
}

/*흐름은 아래와 같습니다.


홀짝 게임 시퀀스 다이어그램
테스트 코드 작성 팁
이제 만든 홀짝 게임의 소스들이 잘 돌아가는지 테스트 코드 팁들을 이용하여 작성해보겠습니다.

1. Static Method Test
Util과 같은 Static Method의 경우는 일반적인 @Mock 사용 등의 방법으로 Mocking이 되질 않아 테스트에 어려움이 있습니다.
그래서 Static Method의 경우는 아래와 같은 방법으로 Mocking을 하여 테스트를 진행합니다.

 

1. testImplementation 'org.mockito:mockito-inline:3.6.0' dependency 추가
2. 아래과 같이 Mocking 할 Util 객체 선언
private static MockedStatic<NumberUtil> numberUtil;

@BeforeAll
public static void beforeAll() {
    numberUtil = mockStatic(NumberUtil.class);
}

@AfterAll
public static void afterAll() {
    numberUtil.close();
}​
3. Test Method에서 Mocking

given(NumberUtil.generateRandom()).willReturn(1);
 

홀짝 게임에서도 OddEvenGame 객체의 play 함수를 테스트하기 위해서는 NumberUtil의 Mocking이 필요합니다.
그래서 다음과 같이 Mocking을 하여 테스트 코드를 구현하였습니다.*/

public class OddEvenGameTest {

    private static MockedStatic<NumberUtil> numberUtil;

    @BeforeAll
    public static void beforeAll() {
        numberUtil = mockStatic(NumberUtil.class);
    }

    @AfterAll
    public static void afterAll() {
        numberUtil.close();
    }

    @Test
    void playOddTest(){
        OddEvenGame game = new OddEvenGame();
        //내가 홀을 내고 컴퓨터가 홀이 나오면 WIN
        given(NumberUtil.generateRandom()).willReturn(1);
        assertEquals(Result.WIN , game.play(Number.ODD));
        //내가 홀을 내고 컴퓨터가 짝이면 LOSE
        given(NumberUtil.generateRandom()).willReturn(2);
        assertEquals(Result.LOSE , game.play(Number.ODD));
    }
}
/*
2. Private Method Test
private Method의 경우는 외부에서 사용이 불가하여 테스트 코드에서 호출이 불가합니다.
그래서 테스트를 하기 위하여 private Method를 호출하는 Public Method를 테스트하거나 아래와 같은 방법으로 테스트가 가능합니다.

 

Java의 Reflection을 이용하여 Private Method의 Accessible을 True로 변경하고 Method Invoke
Method method = game.getClass().getDeclaredMethod("generateOddOrEven");
method.setAccessible(true);
Number methodResult = (Number) method.invoke(game);​
스프링 사용 중이라면 스프링에서 제공하는 Test Util을 사용하여 한 줄로 깔끔하게 처리 가능합니다.

Number methodResult = ReflectionTestUtils.invokeMethod(game, "generateOddOrEven");
 

 

해당 팁을 이용하여 OddEvenGame.generateOddOrEven()를 테스트하였습니다.

@Test */

void generateOddTest() throws Exception {
    OddEvenGame game = new OddEvenGame();
    //랜덤 값은 1이 나올 것이다.
    given(NumberUtil.generateRandom()).willReturn(1);

    Method method = game.getClass().getDeclaredMethod("generateOddOrEven");
    method.setAccessible(true);
    Number methodResult = (Number) method.invoke(game);

    //랜덤 값이 홀수이면 ODD
    assertEquals(Number.ODD, methodResult);
}
/*
3. ParameterizedTest
이 팁은 저도 비교적 최근에 알게 되었습니다.
저희 스마일 페이 팀에서는 매주 수요일 개발자들이 모여 일주일 간 업무를 하면서 새롭게 알게 된 개발 지식이나 나누고 싶은 지식을 공유하는 시간이 있습니다.
이때 동료로부터 공유받아서 알게 된 팁입니다.

이전까지만 해도 동일한 로직에 파라미터만 여러 개를 테스트하기 위하여 공통부분을 따로 함수로 빼든 혹은 코드의 중복을 허용하는 테스트 코드를 작성하였습니다. 하지만 해당 팁을 활용하면 간단하게 여러 파라미터에 대한 테스트 코드를 작성할 수 있습니다.
사용법은 아래와 같이 간단합니다.

1. testImplementation 'org.junit.jupiter:junit-jupiter-params:5.4.2' dependency 추가

2. @Test 어노테이션 대신 @ParameterizedTest 사용

3. @EnumSource(value = Number.class, names = { "ODD", "EVEN" }) 같이 테스트할 파라미터 나열

4. Test Method에 파라미터 인자 추가


파라미터 나열 방법은 다양한 종류가 있어서 해당 페이지를 참고하면 좋을 것 같습니다.
(https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests)

해당 팁을 이용하여 홀수, 짝수 각각에 대한 테스트를 하나의 테스트 함수로 처리하였습니다.

@Test 사용

@Test */

void playOddTest(){
    OddEvenGame game = new OddEvenGame();
    //내가 홀을 내고 컴퓨터가 홀이 나오면 WIN
    given(NumberUtil.generateRandom()).willReturn(1);
    assertEquals(Result.WIN , game.play(Number.ODD));
    //내가 홀을 내고 컴퓨터가 짝이면 LOSE
    given(NumberUtil.generateRandom()).willReturn(2);
    assertEquals(Result.LOSE , game.play(Number.ODD));
}

@Test
void playEvenTest(){
    OddEvenGame game = new OddEvenGame();
    //내가 짝을 내고 컴퓨터가 홀이 나오면 LOSE
    given(NumberUtil.generateRandom()).willReturn(2);
    assertEquals(Result.WIN , game.play(Number.EVEN));
    //내가 짝을 내고 컴퓨터가 홀이면 LOSE
    given(NumberUtil.generateRandom()).willReturn(1);
    assertEquals(Result.LOSE , game.play(Number.EVEN));
}
//@ParameterizedTest 사용

@ParameterizedTest
@EnumSource(value = Number.class, names = { "ODD", "EVEN" })
void playTestUseParameterizedTest(Number my){
    OddEvenGame game = new OddEvenGame();
    //나와 컴퓨터가 같으면 WIN
    given(NumberUtil.generateRandom()).willReturn(my == Number.ODD ? 1 : 2);
    assertEquals(Result.WIN , game.play(my));
    //나와 컴퓨터가 다르면 LOSE
    given(NumberUtil.generateRandom()).willReturn(my == Number.ODD ? 2 : 1);
    assertEquals(Result.LOSE , game.play(my));
}

//마무리
//여러 테스트 팁을 활용하여 작성한 최종 테스트 코드 및 테스트 결과입니다.

public class OddEvenGameTest {

    private static MockedStatic<NumberUtil> numberUtil;

    @BeforeAll
    public static void beforeAll() {
        numberUtil = mockStatic(NumberUtil.class);
    }

    @AfterAll
    public static void afterAll() {
        numberUtil.close();
    }

    @ParameterizedTest
    @EnumSource(value = Number.class, names = { "ODD", "EVEN" })
    void playTestUseParameterizedTest(Number my){
        OddEvenGame game = new OddEvenGame();
        //나와 컴퓨터가 같으면 WIN
        given(NumberUtil.generateRandom()).willReturn(my == Number.ODD ? 1 : 2);
        assertEquals(Result.WIN , game.play(my));
        //나와 컴퓨터가 다르면 LOSE
        given(NumberUtil.generateRandom()).willReturn(my == Number.ODD ? 2 : 1);
        assertEquals(Result.LOSE , game.play(my));
    }

  //  @Test
    void generateOddTest() throws Exception {
        OddEvenGame game = new OddEvenGame();
        //랜덤 값은 1이 나올 것이다.
        given(NumberUtil.generateRandom()).willReturn(1);

        Method method = game.getClass().getDeclaredMethod("generateOddOrEven");
        method.setAccessible(true);
        Number methodResult = (Number) method.invoke(game);

        //랜덤 값이 홀수이면 ODD
        assertEquals(Number.ODD, methodResult);
    }

}