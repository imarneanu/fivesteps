package com.imarneanu.fivesteps;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by imarneanu on 4/28/16.
 */
public class RegexTest {

    @Test
    public void threeLetterRegex_ReturnsTrue() {
        String regex = "[A-Z][a-z]{2}";

        assertFalse("THU".matches(regex));
        assertFalse("Thus".matches(regex));
        assertFalse("Th".matches(regex));
        assertFalse("Th3".matches(regex));

        assertTrue("Thu".matches(regex));
    }

    @Test
    public void weekdayRegex_ReturnsTrue() {
        String regex = "Mon|Tue|Wed|Thu|Fri|Sat|Sun";

        assertFalse("THU".matches(regex));
        assertTrue("Thu".matches(regex));
    }

    @Test
    public void monthRegex_ReturnsTrue() {
        String regex = "Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec";

        assertFalse("JAN".matches(regex));
        assertTrue("Apr".matches(regex));
    }

    @Test
    public void monthAndDayRegex_ReturnsTrue() {
        String regex = "[A-Z][a-z]{2}\\s\\d{1,2}";

        assertTrue("Apr 28".matches(regex));
        assertTrue("Apr 1".matches(regex));
        assertTrue("Apr 01".matches(regex));
        assertFalse("Apr".matches(regex));
        assertFalse("Apr4".matches(regex));
    }

    @Test
    public void weekdayMonthAndDayRegex_ReturnsTrue() {
        String regex = "[A-Z][a-z]{2},\\s[A-Z][a-z]{2}\\s\\d{1,2}";

        assertTrue("Wed, Apr 28".matches(regex));
        assertFalse("Wed,Apr 28".matches(regex));
    }

    @Test
    public void weekdayMonthDayYearRegex_ReturnsTrue() {
        String regex = "[A-Z][a-z]{2},\\s[A-Z][a-z]{2}\\s\\d{1,2},\\s\\d{4}";

        assertTrue("Wed, Apr 28, 2016".matches(regex));
        assertFalse("Wed, Apr 28, 22".matches(regex));
    }

    @Test
    public void validWeekdayMonthDayYearRegex_ReturnsTrue() {
        String regex = "[M|T|W|F|S][a-z]{2},\\s[J|F|M|A|J|S|O|N|D][a-z]{2}\\s\\d{1,2},\\s\\d{4}";

        assertTrue("Wed, Apr 28, 2016".matches(regex));
        assertFalse("Lun, Apr 28, 22".matches(regex));
        assertFalse("Wed, Iun 28, 22".matches(regex));
    }
}
