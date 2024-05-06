public class App {
    public static void printOne(String[] patterns, String[] numbers)
    {
        System.out.printf("'-----------'\n");
        System.out.printf("|%s          |\n", numbers[0]);
        System.out.printf("|           |\n");
        System.out.printf("|           |\n");
        System.out.printf("|           |\n");
        System.out.printf("|     %s     |\n", patterns[0]);
        System.out.printf("|           |\n");
        System.out.printf("|           |\n");
        System.out.printf("|           |\n");
        System.out.printf("|          %s|\n", numbers[0]);
        System.out.printf("'-----------'\n");
    }
    
    public static void printTwo(String[] patterns, String[] numbers)
    {
        System.out.printf("'-----------'    '-----------'\n");
        System.out.printf("|%s          |    |%s          |\n", numbers[0], numbers[1]);
        System.out.printf("|           |    |           |\n");
        System.out.printf("|           |    |           |\n");
        System.out.printf("|           |    |           |\n");
        System.out.printf("|     %s     |    |     %s     |\n", patterns[0], patterns[1]);
        System.out.printf("|           |    |           |\n");
        System.out.printf("|           |    |           |\n");
        System.out.printf("|           |    |           |\n");
        System.out.printf("|          %s|    |          %s|\n", numbers[0], numbers[1]);
        System.out.printf("'-----------'    '-----------'\n");
    }
    
    public static void printThree(String[] patterns, String[] numbers)
    {
        System.out.printf("'-----------'    '-----------'    '-----------'\n");
        System.out.printf("|%s          |    |%s          |    |%s          |\n", numbers[0], numbers[1], numbers[2]);
        System.out.printf("|           |    |           |    |           |\n");
        System.out.printf("|           |    |           |    |           |\n");
        System.out.printf("|           |    |           |    |           |\n");
        System.out.printf("|     %s     |    |     %s     |    |     %s     |\n", patterns[0], patterns[1], patterns[2]);
        System.out.printf("|           |    |           |    |           |\n");
        System.out.printf("|           |    |           |    |           |\n");
        System.out.printf("|           |    |           |    |           |\n");
        System.out.printf("|          %s|    |          %s|    |          %s|\n", numbers[0], numbers[1], numbers[2]);
        System.out.printf("'-----------'    '-----------'    '-----------'\n");
    }
    
    public static void printFour(String[] patterns, String[] numbers)
    {
        System.out.printf("'-----------'    '-----------'    '-----------'    '-----------'\n");
        System.out.printf("|%s          |    |%s          |    |%s          |    |%s          |\n", numbers[0], numbers[1], numbers[2], numbers[3]);
        System.out.printf("|           |    |           |    |           |    |           |\n");
        System.out.printf("|           |    |           |    |           |    |           |\n");
        System.out.printf("|           |    |           |    |           |    |           |\n");
        System.out.printf("|     %s     |    |     %s     |    |     %s     |    |     %s     |\n", patterns[0], patterns[1], patterns[2], patterns[3]);
        System.out.printf("|           |    |           |    |           |    |           |\n");
        System.out.printf("|           |    |           |    |           |    |           |\n");
        System.out.printf("|           |    |           |    |           |    |           |\n");
        System.out.printf("|          %s|    |          %s|    |          %s|    |          %s|\n", numbers[0], numbers[1], numbers[2], numbers[3]);
        System.out.printf("'-----------'    '-----------'    '-----------'    '-----------'\n");
    }
    
    public static void printFive(String[] patterns, String[] numbers)
    {
        System.out.printf("'-----------'    '-----------'    '-----------'    '-----------'    '-----------'\n");
        System.out.printf("|%s          |    |%s          |    |%s          |    |%s          |    |%s          |\n", numbers[0], numbers[1], numbers[2], numbers[3], numbers[4]);
        System.out.printf("|           |    |           |    |           |    |           |    |           |\n");
        System.out.printf("|           |    |           |    |           |    |           |    |           |\n");
        System.out.printf("|           |    |           |    |           |    |           |    |           |\n");
        System.out.printf("|     %s     |    |     %s     |    |     %s     |    |     %s     |    |     %s     |\n", patterns[0], patterns[1], patterns[2], patterns[3], patterns[4]);
        System.out.printf("|           |    |           |    |           |    |           |    |           |\n");
        System.out.printf("|           |    |           |    |           |    |           |    |           |\n");
        System.out.printf("|           |    |           |    |           |    |           |    |           |\n");
        System.out.printf("|          %s|    |          %s|    |          %s|    |          %s|    |          %s|\n", numbers[0], numbers[1], numbers[2], numbers[3], numbers[4]);
        System.out.printf("'-----------'    '-----------'    '-----------'    '-----------'    '-----------'\n");
        
    }

    public static void clearConsole() {
        // ANSI 이스케이프 시퀀스를 사용하여 콘솔을 지웁니다.
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    public static void main(String[] args) throws Exception {

        String[] numbers = {"A", "2", "3", "4", "5"};
        String[] patterns = {"◈", "♥", "♣️", "♥", "♠️"};

        printOne(patterns, numbers);
        clearConsole();
        printTwo(patterns, numbers);

        // printThree(patterns, numbers);
        // printFour(patterns, numbers);
        // printFive(patterns, numbers);
    }
}
