package pl.michalkarwowski.api.util;

public class NumberToWord {
    public static String translate(long number) {
        String[] units = {"", "jeden ", "dwa ", "trzy ", "cztery ",
                "pięć ", "sześć ", "siedem ", "osiem ", "dziewięć ",};

        String[] teens = {"", "jedenaście ", "dwanaście ", "trzynaście ",
                "czternaście ", "piętnaście ", "szesnaście ", "siedemnaście ",
                "osiemnaście ", "dziewiętnaście ",};

        String[] tens = {"", "dziesięć ", "dwadzieścia ",
                "trzydzieści ", "czterdzieści ", "pięćdziesiąt ",
                "sześćdziesiąt ", "siedemdziesiąt ", "osiemdziesiąt ",
                "dziewięćdziesiąt ",};

        String[] hundreds = {"", "sto ", "dwieście ", "trzysta ", "czterysta ",
                "pięćset ", "sześćset ", "siedemset ", "osiemset ",
                "dziewięćset ",};

        String[][] groups = {{"", "", ""},
                {"tysiąc ", "tysiące ", "tysięcy "},
                {"milion ", "miliony ", "milionów "},
                {"miliard ", "miliardy ", "miliardów "},
                {"bilion ", "biliony ", "bilionów "},
                {"biliard ", "biliardy ", "biliardów "},
                {"trylion ", "tryliony ", "trylionów "},};

        long u = 0;
        long te = 0;
        long t = 0;
        long h = 0;
        long g = 0;
        long ends = 0;
        String inWords = "";
        String sign = "";

        if (number < 0) {
            sign = "minus ";
            number = -number;
        }
        if (number == 0) {
            sign = "zero ";
        }

        while (number != 0) {
            h = number % 1000 / 100;
            t = number % 100 / 10;
            u = number % 10;

            if (t == 1 & u > 0) {
                te = u;
                t = 0;
                u = 0;
            } else {
                te = 0;
            }

            if (u == 1 & h + t + te == 0) {
                ends = 0;

                if (h + t == 0 && g > 0) {
                    u = 0;
                    inWords = groups[(int) g][(int) ends] + inWords;
                }
            } else if (u == 2 || u == 3 || u == 4) {
                ends = 1;
            } else {
                ends = 2;
            }

            if (h + t + te + u > 0) {
                inWords = hundreds[(int) h] + tens[(int) t] + teens[(int) te] + units[(int) u] + groups[(int) g][(int) ends] + inWords;
            }

            number = number / 1000;
            g = g + 1;
        }

        inWords = sign + inWords;
        return inWords;

    }
}
