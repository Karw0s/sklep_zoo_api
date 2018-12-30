package pl.michalkarwowski.api.util;

public class NumberToWord {
    public static String translate(long number) {
        String[] units = { "", "jeden ", "dwa ", "trzy ", "cztery ",
                "pięć ", "sześć ", "siedem ", "osiem ", "dziewięć ", };

        String[] teens = { "", "jedenaście ", "dwanaście ", "trzynaście ",
                "czternaście ", "piętnaście ", "szesnaście ", "siedemnaście ",
                "osiemnaście ", "dziewiętnaście ", };

        String[] tens = { "", "dziesięć ", "dwadzieścia ",
                "trzydzieści ", "czterdzieści ", "pięćdziesiąt ",
                "sześćdziesiąt ", "siedemdziesiąt ", "osiemdziesiąt ",
                "dziewięćdziesiąt ", };

        String[] hundreds = { "", "sto ", "dwieście ", "trzysta ", "czterysta ",
                "pięćset ", "sześćset ", "siedemset ", "osiemset ",
                "dziewięćset ", };

        String[][] groups = { { "", "", "" },
                { "tysiąc ", "tysiące ", "tysięcy " },
                { "milion ", "miliony ", "milionów " },
                { "miliard ", "miliardy ", "miliardów " },
                { "bilion ", "biliony ", "bilionów " },
                { "biliard ", "biliardy ", "biliardów " },
                { "trylion ", "tryliony ", "trylionów " }, };

        // INICJACJA ZMIENNYCH
        long u = 0/* jedności */, te = 0/* nastki */, t = 0/* dziesiątki */, h = 0/* setki */, g = 0/* grupy */, k = 0/* końcówwki */;
        String inWords = "";
        String sign = "";

        // OPERACJA DOTYCZąCA ZNAKU

        if (number < 0) {
            sign = "minus ";
            number = -number; // bezwględna wartość ponieważ, jeśli będziemy
            // operować na liczbie z minusem tablica będzie
            // przyjmowała wartości ujemne i zwróci nam błąd
        }
        if (number == 0) {
            sign = "zero ";
        }

        // PĘTLA GŁÓWNA
        while (number != 0) {
            h = number % 1000 / 100;
            t = number % 100 / 10;
            u = number % 10;

            if (t == 1 & u > 0) // if zajmujący się nastkami
            {
                te = u;
                t = 0;
                u = 0;
            } else {
                te = 0;
            }

            // <---- KOŃCÓWKI

            if (u == 1 & h + t + te == 0) {
                k = 0;

                if (h + t == 0 && g > 0) // jeśli nie będzie dziesiątek ani setek, wtedy otrzymamy samą grupę
                { // przykładowo 1000 - wyświetli nam się "tysiąc", jeśli
                    // zakomentujemy tego if'a to otrzymamy "jeden tysiąc"
                    u = 0;
                    inWords = groups[(int) g][(int) k] + inWords;
                }
            } else if (u == 2) {
                k = 1;
            } else if (u == 3) {
                k = 1;
            } else if (u == 4) {
                k = 1;
            } else {
                k = 2;
            }

            // KONIEC KOŃCÓWEK -->

            if (h+t+te+u > 0) {
                inWords = hundreds[(int) h] + tens[(int) t] + teens[(int) te] + units[(int) u] + groups[(int) g][(int) k] + inWords;
            }

            // POZBYWAMY SIĘ TYCH LICZBY KTÓRE JUŻ PRZEROBILIŚMY czyli
            // przykładowo z 132132 zostaje nam 132 do obróbki
            number = number / 1000;
            // ORAZ ZWIĘKSZAMY G KTÓRE ODPOWIEDZIALNE JEST ZA NUMER POLA W
            // TABLICY WIELOWYMIAROWEJ
            g = g + 1;
        }

        inWords = sign + inWords;
        return inWords;

    }
}
