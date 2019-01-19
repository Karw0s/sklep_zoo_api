package pl.michalkarwowski.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import pl.michalkarwowski.api.util.NumberToWord;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class NumberToWordTests {

    @Test
    public void numberTest() {

        assertEquals("minus trzynaście ", NumberToWord.translate(-13L));
        assertEquals("zero ", NumberToWord.translate(0L));
        assertEquals("sto ", NumberToWord.translate(100L));
        assertEquals("trzy tysiące ", NumberToWord.translate(3000L));
        assertEquals("czternaście tysięcy dziewięćdziesiąt dwa ",
                NumberToWord.translate(14092L));
        assertEquals("sto tysięcy ", NumberToWord.translate(100000L));
        assertEquals("sto jedenaście tysięcy czterysta pięćdziesiąt trzy ",
                NumberToWord.translate(111453L));
        assertEquals("dwanaście milionów tysiąc czterysta pięćdziesiąt trzy ",
                NumberToWord.translate(12001453L));
    }

}
