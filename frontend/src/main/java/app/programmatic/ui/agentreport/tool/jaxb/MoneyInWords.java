package app.programmatic.ui.agentreport.tool.jaxb;

public class MoneyInWords {
    private static final String dig1[][] = {{"одна", "две", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять"},
            {"один", "два"}}; //dig[0] - female, dig[1] - male
    private static final String dig10[] = {"десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать",
            "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать"};
    private static final String dig20[] = {"двадцать", "тридцать", "сорок", "пятьдесят",
            "шестьдесят", "семьдесят", "восемьдесят", "девяносто"};
    private static final String dig100[] = {"сто", "двести", "триста", "четыреста", "пятьсот",
            "шестьсот", "семьсот", "восемьсот", "девятьсот"};
    private static final String leword[][] = {{"копейка", "копейки", "копеек", "0"},
            {"рубль", "рубля", "рублей", "1"},
            {"тысяча", "тысячи", "тысяч", "0"},
            {"миллион", "миллиона", "миллионов", "1"},
            {"миллиард", "миллиарда", "миллиардов", "1"},
            {"триллион", "триллиона", "триллионов", "1"}};

    private static String numToWords(long num, int level) {
        StringBuilder words = new StringBuilder(50);
        if (num == 0) words.append("ноль ");
        int sex = leword[level][3].indexOf("1") + 1;
        int h = (int) (num % 1000);
        int d = h / 100;
        if (d > 0) words.append(dig100[d - 1]).append(" ");
        int n = h % 100;
        d = n / 10;
        n = n % 10;
        switch (d) {
            case 0:
                break;
            case 1:
                words.append(dig10[n]).append(" ");
                break;
            default:
                words.append(dig20[d - 2]).append(" ");
        }
        if (d == 1) n = 0;
        switch (n) {
            case 0:
                break;
            case 1:
            case 2:
                words.append(dig1[sex][n - 1]).append(" ");
                break;
            default:
                words.append(dig1[0][n - 1]).append(" ");
        }
        switch (n) {
            case 1:
                words.append(leword[level][0]);
                break;
            case 2:
            case 3:
            case 4:
                words.append(leword[level][1]);
                break;
            default:
                if ((h != 0) || ((h == 0) && (level == 1)))
                    words.append(leword[level][2]);
        }
        long nextNum = num / 1000;
        if (nextNum > 0) {
            return (numToWords(nextNum, level + 1) + " " + words.toString()).trim();
        } else {
            return words.toString().trim();
        }
    }

    public static String inWords(double money) {
        if (money < 0.0 || money > 1000000000000000.0) {
            throw new RuntimeException("Money value " + money + " is incorrect");
        }

        String sm = String.format("%.2f", money);
        String skop = sm.substring(sm.length() - 2, sm.length());
        int iw;
        switch (skop.substring(1)) {
            case "1":
                iw = 0;
                break;
            case "2":
            case "3":
            case "4":
                iw = 1;
                break;
            default:
                iw = 2;
        }

        long num = (long) Math.floor(money);
        return numToWords(num, 1) + " " + skop + " " + leword[0][iw];
    }
}
