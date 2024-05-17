import java.util.Scanner;
import javax.management.monitor.StringMonitor;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

class Card {
    public String suit;
    public int number;
} 

abstract class Deck {
    private static LinkedList<Card> deck = new LinkedList<>();

    abstract void deckGenerate();
    void initialize() {
        deck.clear();
        deckGenerate();
        Collections.shuffle(deck);
    }

    static Card draw() {
        Card card = deck.getLast();
        deck.removeLast();
        return card;
    }

    // 외부에서 deck으로의 접근 제공
    protected static LinkedList<Card> getDeck() {
        return deck; 
    }
}

class PokerDeck extends Deck {
    public final String[] SUIT = { "♠️", "♣️", "◈", "♥" };
    static private LinkedList<Card> table = new LinkedList<>(); // 포커 테이블

    PokerDeck() {
        initialize();
    }
    
    @Override
    void initialize() {
        super.getDeck().clear();
        table.clear();
        deckGenerate();
        Collections.shuffle(super.getDeck());
    }

    @Override
    void deckGenerate() {
        for (int i = 0; i < SUIT.length; ++i) {
            for (int j = 0; j < 13; ++j) {
                Card card = new Card();
                card.suit = SUIT[i];
                card.number = j + 1;
                super.getDeck().add(card);
            }
        }
    }

    void tableSet() {
        for (int i = 0; i < 5; ++i) {
            table.add(draw());
        }
    }

    // 외부에서 table로의 접근 제공
    static LinkedList<Card> getTable() {
        return table;
    }

}

class Player {
    LinkedList<Card> hands = new LinkedList<>();
    
    void clear() {
        hands.clear();
    }

    void drawing() {
        hands.addAll(PokerDeck.getTable());
        for (int i = 0; i < 2; ++i) {
            hands.add(PokerDeck.draw());
        }
    }
 
    void getHands() {
        for (int i = 0; i < hands.size(); ++i) {
            System.out.println(hands.get(i).suit + " " + hands.get(i).number);
        }
    }
}

class Rank {
    public static int cardRank(LinkedList<Card> cards) {
    /*
     * 다섯 장을 판별하는 방법:
     * 1번째 카드: 2~5번째 카드와 비교
     * 2번째 카드: 3~5번째 카드와 비교
     * 3번째 카드: 4~5번째 카드와 비교
     * 4번째 카드: 5번째 카드와 비교
     * 
     * i == 0, j == 1~4
     * i == 1, j == 2~4
     * i == 2, j == 3~4
     * i == 3, j == 4
     */

        int pairCount = 0;
        for (int i = 0; i < 4; ++i) {
            for (int j = i + 1; j < 5; ++j) {
                if (cards.get(i).number == cards.get(j).number) {
                    pairCount++;
                    /*
                     * 원페어: 1
                     * 투페어: 2
                     * 트리플: 3
                     * 풀하우스: 4 ∵ 3 + 1
                     * 포카드: 6 ∵ 2s, 2h, 2d, 2c, 3c일 때 똑같은 숫자의 쌍: 2s-2h, 2s-2d, 2s-2c, 2h-2d, 2h-2c, 2d-2c
                     */
                }
            }
        }

    // 숫자별 정렬
        LinkedList<Integer> sortedNum = new LinkedList<>();
        for (int i = 0; i < 5; ++i) {
            sortedNum.add(cards.get(i).number); // sortedNum에 값 복사: cards의 number값
        }
        Collections.sort(sortedNum);        

    // straight 여부 판정
        boolean straight = false;
        if (pairCount == 0) {
            // [1, 2, 3, 4, 5], [2, 3, 4, 5, 6], …, [9, 10, 11, 12, 13]: 가장 큰 수와 가장 작은 수의 차가 4
            if (sortedNum.get(4) - sortedNum.get(0) == 4) {
                straight = true;
            }
            if (sortedNum.get(0) == 1 && sortedNum.get(1) == 10) { // [A, 10, J, Q, K]
                straight = true;
            }
        }

    // flush 여부 판정
        LinkedList<String> sortedSuit = new LinkedList<>(); // suit: 문양별 정렬
        for (int i = 0; i < 5; ++i) {
            sortedSuit.add(cards.get(i).suit); // sortedSuit에 값 복사: cards의 suit값
        }
        Collections.sort(sortedSuit);

    // 문양별로 정렬한 후 suit[0]과 suit[4]가 같을 경우, index 0~4까지 같은 문양
        boolean flush = false;
        if (sortedSuit.get(0).equals(sortedSuit.get(4))) {
            flush = true;
        }

    /* 
     * rank: 숫자가 클수록 높은 순위
     * 총 8자리:
     * 1~2: 족보
     * 3~4: 페어(1)
     * 5~6: 페어(2)
     * 7~8: 하이 카드
     */ 
    
        int rank = 2_00_00_00;
        if (straight && flush) {
            rank = 10_00_00_00;
        } 

        else if (pairCount == 6) { // 포카드
            rank = 9_00_00_00;
        } 
        
        else if (pairCount == 4) { // 풀하우스
            rank = 8_00_00_00;
        } 
        
        else if (flush) {
            rank = 7_00_00_00;
        } 

        else if (straight) {
            rank = 6_00_00_00;
        } 
        
        else if (pairCount == 3) {
            rank = 5_00_00_00;
        } 
        
        else if (pairCount == 2) {
            rank = 4_00_00_00;
        } 
        
        else if (pairCount == 1) {
            rank = 3_00_00_00;
        } 
        
        else rank = 2_00_00_00;     // 하이 카드(족보)
        

    // highCard 정보를 덧셈 형식으로 추가, 형식 예시: rank = 700 + highCard;
        rank += highCard(sortedNum, rank);
        return rank;
}

// 각 족보의 하이 카드 return: 같은 rank일 때 족보를 따져보는 용도
    public static int highCard(LinkedList<Integer> sortedNum, int rank) {   
    // 계산의 편리를 위해 sortedNum의 1(A) -> 14로 변환
        for (int i = 0; i < sortedNum.size(); ++i) {
            if (sortedNum.get(i) == 1) {
                sortedNum.set(i, 14);
            }
        }
        Collections.sort(sortedNum);

        switch (rank) {
        // 5장 내에서 판단해야 하는 경우 중 스트레이트 플러시, 플러시, 스트레이트, 하이 카드
            case 10_00_00_00:
            case 7_00_00_00:
            case 6_00_00_00:
            case 2_00_00_00:
                return sortedNum.getLast();
        
        // 풀하우스
            case 8_00_00_00:
            /* 
            * 풀하우스: 트리플 + 투페어, 트리플의 족보 > 투페어의 족보 > 키커의 족보
            * 2 2 2 9 9 < 3 3 4 4 4
            * -> 3~4: 트리플, 5~6: 페어, 키커 별도 확인: 5장씩 비교해서 비교 불가
            * 
            * 어떤 수가 트리플을 이루는지 확인
            * arr1: 2 2 2 9 9 < arr2: 3 3 4 4 4,
            * arr1[0] == arr1[2], arr2[0] != arr2[2]
            */
                if (sortedNum.get(0) == sortedNum.get(2)) {
                    return sortedNum.get(2) * 10000 + sortedNum.get(4) * 100;
                } 
                
                else {
                    return sortedNum.get(2) * 10000 + sortedNum.get(0) * 100;
                }

        // 포카드, 트리플, 페어 등 5장 내에서 판단하지 않을 경우
        // 포카드: 5 5 5 5 6 / 5 6 6 6 6
            case 9_00_00_00:
            // 5 5 5 5 6일 경우
                if (sortedNum.get(0) == sortedNum.get(3)) {
                    return (sortedNum.get(0) * 10000 + sortedNum.get(4)); // 포카드 + 키커
                }

            // 5 6 6 6 6일 경우 
                else { 
                    return sortedNum.get(4) * 10000 + sortedNum.get(0); // 포카드 + 키커
                }

        // 트리플: 3 3 3 4 5 / 3 4 4 4 5 / 3 4 5 5 5
            case 5_00_00_00:
            // 3 3 3 4 5일 경우
                if (sortedNum.get(0) == sortedNum.get(2)) {
                // 트리플 값 + 키커1 + 키커2
                    return (sortedNum.get(0) * 10000 + sortedNum.get(4) * 100 + sortedNum.get(3));
                }

                else if (sortedNum.get(1) == sortedNum.get(3)) {
            // 3 4 4 4 5일 경우 
                    return (sortedNum.get(1) * 10000 + sortedNum.get(4) * 100 + sortedNum.get(0));
                } 
                
                else { // 3 4 5 5 5일 경우
                    return (sortedNum.get(2) * 10000 + sortedNum.get(1) * 100 + sortedNum.get(0));
                }

        // 원 페어: 2 2 3 4 5 / 2 3 3 4 5 / 2 3 4 4 5 / 2 3 4 5 5
            case 3_00_00_00:
                List<Integer> kickers = new ArrayList<>();
                int pairIndex = -1;

            // 페어 찾기
                for (int i = 0; i < 4; i++) {
                    if (sortedNum.get(i) == sortedNum.get(i + 1)) {
                        pairIndex = sortedNum.get(i);
                        break;
                    }
                }

            // 킥커 수집
                for (int i = 0; i < 5; i++) {
                    if (sortedNum.get(i) != pairIndex) {
                        kickers.add(sortedNum.get(i));
                    }
                }
                Collections.sort(kickers);

            // 페어 + 3개 킥커        
                return (pairIndex * 10000 + kickers.get(2) * 100 + kickers.get(1) + kickers.get(0));

        // 투 페어: 2 2 3 3 4 / 2 2 3 4 4 / 2 3 3 4 4
            case 4_00_00_00:              
                int firstPair, secondPair, kicker;

            // 2 2 3 3 4일 경우
                if (sortedNum.get(0) == sortedNum.get(1) && sortedNum.get(2) == sortedNum.get(3)) {
                    firstPair = sortedNum.get(2);
                    secondPair = sortedNum.get(0);
                    kicker = sortedNum.get(4);
                }

            // 2 2 3 4 4일 경우 
                else if (sortedNum.get(0) == sortedNum.get(1) && 
                        sortedNum.get(3) == sortedNum.get(4)) { 
                    firstPair = sortedNum.get(3);
                    secondPair = sortedNum.get(0);
                    kicker = sortedNum.get(2);
                } 
            
                else { // 2 3 3 4 4일 경우
                    firstPair = sortedNum.get(4);
                    secondPair = sortedNum.get(2);
                    kicker = sortedNum.get(0);
                }
                return (firstPair * 10000 + secondPair * 100 + kicker); // 페어1 > 페어2 > 키커
        }
        return rank;
    }

    public static int bestRank(LinkedList<Card> cards) {
        int bestRank = 2_00_00_00; // 가장 낮은 랭크로 초기화
    /*
     * 7장의 카드 중 5장을 선택하는 모든 조합을 확인
     * i = 0, j = 1 ~ 6까지 검사
     * i = 1, j = 2 ~ 6까지 검사
     * ... 
     * i = 5, j = 6까지 검사
     */ 

        for (int i = 0; i < cards.size(); ++i) {
            for (int j = i + 1; j < cards.size(); ++j) {
                LinkedList<Card> temp = new LinkedList<>(cards); // temp에 cards의 값 복사
            
                // i, j의 카드 제외: 뒤의 index부터 제거해야 오류 X
                temp.remove(j);
                temp.remove(i);

                int rank = cardRank(temp); // 선별된 5장의 카드 랭크 계산
                if (rank > bestRank) {
                    bestRank = rank; // 더 좋은 랭크가 나오면 bestRank에 업데이트
                }
            }
        }
        return bestRank;
}

    public static List<Integer> determineWinner(Player[] players) {
        int maxRank = 0; // 최대 랭크값 초기화
        List<Integer> winnersIndex = new ArrayList<>(); // 우승자의 인덱스를 저장할 리스트

    // 각 플레이어의 최종 순위를 계산하여 최대 랭크를 찾음
        for (int i = 0; i < players.length; i++) {
            int rank = bestRank(players[i].hands); // 최종 랭크 계산
            if (rank > maxRank) {
                maxRank = rank; // 더 높은 순위 발견 시 최대 랭크 업데이트
                winnersIndex.clear(); // 이전 우승자 인덱스 초기화
                winnersIndex.add(i); // 현재 플레이어를 우승자로 설정
            } 
            
            else if (rank == maxRank) {
                winnersIndex.add(i); // 최대 랭크를 가진 플레이어를 우승자로 추가
            }
        }
    return winnersIndex;
    }
}

//--------------------------------------------------------------------//

class Screen{
    protected static int selectedIndex = 0;
    public static void printScreen(){};
    public static void printCardFrame(){};
    public static void printMenu(){};
    public static void clearConsole() {
        // ANSI 이스케이프 시퀀스를 사용하여 콘솔을 지우기
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class StartScreen extends Screen {
    private static final String[] OPTIONS = {"Start Game", "Exit"};

    private static void printCover() {
        System.out.println("\n");
        System.out.println("                ▛▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▜");
        System.out.println("                ▌                                                                                            ▐");
        System.out.println("                ▌        □□□□□□□□□□□□    □□□□□□□□□□□□    □□        □□    □□□□□□□□□□□□    □□□□□□□□□□          ▐");
        System.out.println("                ▌        □□        □□    □□        □□    □□      □□      □□              □□        □□        ▐");
        System.out.println("                ▌        □□□□□□□□□□□□    □□        □□    □□□□□□□□□       □□□□□□□□□□□□    □□□□□□□□□□          ▐");
        System.out.println("                ▌        □□              □□        □□    □□      □□      □□              □□      □□          ▐");
        System.out.println("                ▌        □□              □□□□□□□□□□□□    □□        □□    □□□□□□□□□□□□    □□        □□        ▐");
        System.out.println("                ▌                                                                                            ▐");
        System.out.println("                ▙▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▟\n");
    }

    public static void printMenu() {
        for (int i = 0; i < OPTIONS.length; i++) {
            if (i == selectedIndex) {
                System.out.print("                                                       -> ");
            } else {
                System.out.print("                                                          ");
            }
            System.out.println(OPTIONS[i]);
        }
        System.out.println("\n                                             W: Move Up, S: Move Down, E: Select");
        System.out.print("                                             Select option >>  "); 
    }

    private static void moveSelectionUp() {
        selectedIndex = (selectedIndex - 1 + OPTIONS.length) % OPTIONS.length;
    }

    private static void moveSelectionDown() {
        selectedIndex = (selectedIndex + 1) % OPTIONS.length;
    }

    public static void printScreen(){
        Scanner scanner = new Scanner(System.in);
        while (true) {
            clearConsole();
            printCover(); // 시작 화면 표지 출력
            printMenu(); // 메뉴 출력

            if (scanner.hasNext()) {
                char input = scanner.next().charAt(0);
                if (input == 'W' || input == 'w') {
                    moveSelectionUp();
                    clearConsole();
                }

                else if (input == 'S' || input == 's') {
                    moveSelectionDown();
                    clearConsole();
                }

                else if (input == 'E' ||input == 'e') {
                    if (selectedIndex == 0) {
                        System.out.print("                                             Enter the number of players >>  ");
                        UserInterface.numPlayers = scanner.nextInt();
                        // Start Game을 선택한 경우, 다음 화면으로 넘어가는 작업 추가
                        break;
                    } 
                    
                    else if (selectedIndex == 1) {
                        // Exit를 선택한 경우, 프로그램 종료
                        System.exit(0);
                    }
                }
            }
        }
    }
}

class LoadingScreen extends Screen {
   
        // System.out.printf("▓▓▓▓▓▓▓▓▓▓▓▓▓\n");
        // System.out.printf("▓▒▒▒▒▒▒▒▒▒▒▒▓\n");
        // System.out.printf("▓▒▒▒▒▒▒▒▒▒▒▒▓\n");
        // System.out.printf("▓▒▒▒▒▒▒▒▒▒▒▒▓\n");
        // System.out.printf("▓▒▒▒▒▒▒▒▒▒▒▒▓\n");
        // System.out.printf("▓▒▒▒▒▒▒▒▒▒▒▒▓\n");
        // System.out.printf("▓▒▒▒▒▒▒▒▒▒▒▒▓\n");
        // System.out.printf("▓▒▒▒▒▒▒▒▒▒▒▒▓\n");
        // System.out.printf("▓▒▒▒▒▒▒▒▒▒▒▒▓\n");
        // System.out.printf("▓▒▒▒▒▒▒▒▒▒▒▒▓\n");
        // System.out.printf("▓▓▓▓▓▓▓▓▓▓▓▓▓\n");
        
    private static void printCardFrame(int frame) {

        System.out.printf("\n\n"); 
        System.out.println("                                            SUFFLILNG CARDS\n"); 
        
        System.out.printf("                "); // 앞 공백 중앙 정렬
        for (int i = 0; i < frame; i++) {
            System.out.printf("▓▓▓▓▓▓▓▓▓▓▓▓▓ "); // 카드 윗 부분 출력
        }
        System.out.println(); // 줄 바꿈
    
        for(int j = 0; j <9; j++){
            System.out.printf("                "); // 앞 공백 중앙 정렬
            for (int i = 0; i < frame; i++) {
                System.out.printf("▓▒▒▒▒▒▒▒▒▒▒▒▓ ");
            }
            System.out.println(); // 줄 바꿈
        }
    
        System.out.printf("                "); // 앞 공백 중앙 정렬
        for (int i = 0; i < frame; i++) {
            System.out.printf("▓▓▓▓▓▓▓▓▓▓▓▓▓ "); // 카드 아래 부분 출력
        }
        System.out.println(); // 줄 바꿈

        System.out.printf("\n\n"); 
        System.out.println("                                   START WITH PLAYER 1. GET READY!\n"); 
    }

    public static void printScreen() {
        int frame = 0;
        boolean turnpoint = false;
        long startTime = System.currentTimeMillis(); // 시작 시간 기록
        long duration = 5000; // 5초
    
        try {
            while (System.currentTimeMillis() - startTime < duration) {
                // ANSI 이스케이프 시퀀스를 사용하여 콘솔을 지우기
                clearConsole();
    
                // 로딩 프레임 출력
                printCardFrame(frame);
    
                // 다음 프레임으로 이동
                if (frame == 0) turnpoint = false;
                else if (frame == 5) turnpoint = true;
    
                if (turnpoint == false) frame++;
                else frame--;
    
                // 잠시 멈춤
                Thread.sleep(250); // 100ms 대기
            }
    
            // 로딩 시간이 지나면 콘솔을 지움
            clearConsole();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class MainGameScreen extends Screen {
    private static final String[] OPTIONS = makeOPTIONS(UserInterface.numPlayers);

    // 카드 숫자를 출력 환경, 조건에 맞게 컨버트
    private static String convertNumber(int number, boolean front) {
        String result;
        if (number == 10 && !front) {
            result = "\b10";
        } else {
            switch (number) {
                case 10:
                    result = "10";
                    break;
                case 11:
                    result = "J";
                    break;
                case 12:
                    result = "Q";
                    break;
                case 13:
                    result = "K";
                    break;
                default:
                    result = String.valueOf(number);
                    break;
            }
            
            if (front && number != 10) {
                result += " ";
            }
        }
        return result;
    }

    // 입력한 플레이어 수만큼 옵션 배열 생성해 주는 함수
    private static String[] makeOPTIONS(int n){
        String[] optionsArr = new String[n];
        for(int i = 0; i < n; ++i){
            optionsArr[i] = "PLAYER " + (i+1);
        }
        return optionsArr;
    }

    // Table 카드 회차 별 출력
    public static void printCardFrame(Player selectedPlayerHands, int selectedPlayer, int turn){
        // 0 ~ 4 인덱스에는 Table 카드
        // 5 ~ 6 인덱스에는 My Hand 카드
        // Table 카드 출력은 turn으로 개수 조절, turn은 5회차까지 
        // My hand 카드는 직접 인덱스 5, 6을 넣어줌
    
    // Table
        System.out.printf("\n"); 
        System.out.println("                                             >> Table <<\n"); 

        System.out.printf("                "); // 앞 공백 중앙 정렬
        for (int i = 0; i < turn; i++) {
            System.out.printf("'-----------' "); // 카드 윗 부분 출력
        }
        System.out.println(); // 줄 바꿈

        System.out.printf("                "); // 앞 공백 중앙 정렬
        for (int i = 0; i < turn; i++) {
            System.out.printf("|%s         | ", convertNumber(selectedPlayerHands.hands.get(i).number, true));// 카드 숫자 출력
        }
        System.out.println(); // 줄 바꿈

        for(int j = 0; j <3; j++){
            System.out.printf("                "); // 앞 공백 중앙 정렬
            for (int i = 0; i < turn; i++) {
                System.out.printf("|           | "); 
            }
            System.out.println(); // 줄 바꿈
        }

        System.out.printf("                "); // 앞 공백 중앙 정렬
        for (int i = 0; i < turn; i++) {
            System.out.printf("|     %s     | ", selectedPlayerHands.hands.get(i).suit);// 카드 문양 출력
        }
        System.out.println(); // 줄 바꿈
        
        for(int j = 0; j <3; j++){
            System.out.printf("                "); // 앞 공백 중앙 정렬
            for (int i = 0; i < turn; i++) {
                System.out.printf("|           | "); 
            }
            System.out.println(); // 줄 바꿈
        }

        System.out.printf("                "); // 앞 공백 중앙 정렬
        for (int i = 0; i < turn; i++) {
                System.out.printf("|          %s| ", convertNumber(selectedPlayerHands.hands.get(i).number, false));// 카드 숫자 출력
        }
        System.out.println(); // 줄 바꿈

        System.out.printf("                "); // 앞 공백 중앙 정렬
        for (int i = 0; i < turn; i++) {
            System.out.printf("'-----------' "); // 카드 아래 부분 출력
        }
        System.out.println(); // 줄 바꿈

    // My Hand
        System.out.printf("\n\n"); 
        System.out.println("                                            >> PLAYER " + (selectedPlayer+1) + " <<\n");

        System.out.printf("                                    '-----------'    '-----------'\n");
        System.out.printf("                                    |%s         |    |%s         |\n", convertNumber(selectedPlayerHands.hands.get(5).number, true), convertNumber(selectedPlayerHands.hands.get(6).number, true));
        System.out.printf("                                    |           |    |           |\n");
        System.out.printf("                                    |           |    |           |\n");
        System.out.printf("                                    |           |    |           |\n");
        System.out.printf("                                    |     %s     |    |     %s     |\n", selectedPlayerHands.hands.get(5).suit, selectedPlayerHands.hands.get(6).suit);
        System.out.printf("                                    |           |    |           |\n");
        System.out.printf("                                    |           |    |           |\n");
        System.out.printf("                                    |           |    |           |\n");
        System.out.printf("                                    |          %s|    |          %s|\n",convertNumber(selectedPlayerHands.hands.get(5).number, false), convertNumber(selectedPlayerHands.hands.get(6).number, false));
        System.out.printf("                                    '-----------'    '-----------'\n\n");
    }

    public static void printMenu() {
        for (int i = 0; i < OPTIONS.length; i++) {
            if (i == selectedIndex) {
                System.out.print("                                             -> ");
            } else {
                System.out.print("                                                ");
            }
            System.out.println(OPTIONS[i]);
        }
        System.out.println("\n                           W: Move Up, S: Move Down, N: Next Turn, E: Select");
        System.out.print("                                        Select option >>  ");
    }

    private static void moveSelectionUp() {
        selectedIndex = (selectedIndex - 1 + OPTIONS.length) % OPTIONS.length;
    }

    private static void moveSelectionDown() {
        selectedIndex = (selectedIndex + 1) % OPTIONS.length;
    }

    public static void printScreen(Player[] players){
        int turn = 1;
        int selectedPlayer = 0;
        Player selectedPlayerHands = players[0];
        Scanner scanner = new Scanner(System.in);

        while (true) {
            clearConsole();
            printCardFrame(selectedPlayerHands, selectedPlayer, turn);
            printMenu(); // 메뉴 출력

            if (scanner.hasNext()) {
                char input = scanner.next().charAt(0);
                if (input == 'W' || input == 'w') {
                    moveSelectionUp();
                    clearConsole();
                }

                else if (input == 'S' || input == 's') {
                    moveSelectionDown();
                    clearConsole();
                }

                else if (input == 'N' || input == 'n') {
                    ++turn;
                    if(turn == 6) {
                        clearConsole();
                        break;
                    }
                }

                else if (input == 'E' ||input == 'e') {
                    for(int i = 0; i < UserInterface.numPlayers; ++i){
                        if (selectedIndex == i) {
                            selectedPlayer = i;
                            selectedPlayerHands = players[selectedPlayer];
                        }
                    }  
                }
            }
        }
    }
}

class EndingScreen extends Screen{
    // 랭크 입력하면 문자열 반환해주는 함수
    static String rankOfCards(int rank) {
        String rankString;
        switch (rank / 1_00_00_00) {
            case 10:
                rankString = "Straight Flush";
                break;
            case 9:
                rankString = "Four of a Kind";
                break;
            case 8:
                rankString = "Full House";
                break;
            case 7:
                rankString = "Flush";
                break;
            case 6:
                rankString = "Straight";
                break;
            case 5:
                rankString = "Three of a Kind";
                break;
            case 4:
                rankString = "Two Pair";
                break;
            case 3:
                rankString = "One Pair";
                break;
            default:
                rankString = "High Card";
                break;
        }
        return rankString;
    }

    // 플레이어 별 rank를 담은 배열 만드는 함수(n은 플레이어 수)
    static String[] makeRankArr(Player[] players,int n){
        String[] playersRank = new String[n];
        
        for (int i = 0; i < playersRank.length; ++i) {
            int rank = Rank.bestRank(players[i].hands);
            playersRank[i] = rankOfCards(rank);
        }
        return playersRank;
    }

    public static void printScreen(Player[] players) {
        List<Integer> winnersIndex = Rank.determineWinner(players);
        String[] playersRank = makeRankArr(players, UserInterface.numPlayers);

        // 우승자 출력
        if (winnersIndex.size() == 1) {
            // 우승자 처리 로직
            System.out.println("\n                                   WINNER IS PLAYER " + (winnersIndex.get(0) + 1) + "!(" + playersRank[winnersIndex.get(0)] + ")");
            MainGameScreen.printCardFrame(players[winnersIndex.get(0)], winnersIndex.get(0), 5);
        } 
        
        else {
            // 동점자 처리 로직
            System.out.print("\n                                   TIE WINNERS ARE: ");
                for (int i = 0; i < winnersIndex.size(); i++) {
                    System.out.print("PLAYER " + (winnersIndex.get(i) + 1) + "(" + playersRank[winnersIndex.get(0)] + ")" + ", ");
                }
                System.out.println("!\n");

                for (int i = 0; i < winnersIndex.size(); i++) {
                    MainGameScreen.printCardFrame(players[winnersIndex.get(i)], winnersIndex.get(i), 5);
                }
            
        }
    }
}

public class UserInterface {
    public static int numPlayers = 0; // 플레이어수 전역변수
    public static int selectedIndex = 0;

    static Player[] playerGenerating(int n) {
        Player[] players = new Player[n];
        for (int i = 0; i < n; ++i) {
            players[i] = new Player();
        }
        return players;
    }

    public static void main(String[] args) {
        while(true){
        // 시작화면(front)
        Screen.clearConsole();
        StartScreen.printScreen();

        // 시작화면(back)
            PokerDeck deck = new PokerDeck();
            deck.initialize(); // 초기 설정
            deck.tableSet();
    
            Player[] players = new Player[numPlayers];
            players = playerGenerating(numPlayers);
    
        // 로딩화면(front)
            LoadingScreen.printScreen();
            
        // 로딩화면(back)
            for (int i = 0; i < numPlayers; ++i) {
                players[i].drawing(); // 각 플레이별 7장
            }
          
        // 본게임화면
            // 게임 중
            MainGameScreen.printScreen(players);
    
            // 우승자 출력화면 
            Scanner scanner = new Scanner(System.in);
            char input;
            while (true) {
                // 우승자 출력
                EndingScreen.printScreen(players);

                // 재시작 / 종료 선택
                System.out.println("          R: Restart, E: Exit");
                System.out.print("          Select option >> ");
                input = scanner.next().charAt(0);
    
                if (input == 'E' || input == 'e') {
                    System.exit(0);
                } 
                
                else if (input == 'R' || input == 'r') {
                    break;
                } 
                
                else {
                    Screen.clearConsole();
                }
            }
            scanner.close();
        }
    }
}
