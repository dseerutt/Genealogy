package Genealogy.Model.Date.RepublicanDate;

import fr.followthecode.republican.date.utils.Months;
import fr.followthecode.republican.date.utils.RepublicanMonths;
import fr.followthecode.republican.date.utils.RepublicanYears;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Code decompiled from external Jar
public class RepublicanCalendarDateConverter {
    private static RepublicanCalendarDateConverter instance;
    private Matcher matcher;
    private static final String REPUBLICAN_DATE = "(\\d+[er]*)\\s*(\\p{L}+[\\.]*)\\s*an\\s*([IVXLCDM]+)";
    private static Pattern PATTERN_REPUBLICAN_DATE = Pattern.compile("(\\d+[er]*)\\s*(\\p{L}+[\\.]*)\\s*an\\s*([IVXLCDM]+)");
    private static final boolean log = false;

    private RepublicanCalendarDateConverter() {
    }

    public static RepublicanCalendarDateConverter getConverter() {
        if(instance == null) {
            instance = new RepublicanCalendarDateConverter();
        }

        return instance;
    }

    public String convertAsString(String republicanDate, String pattern) {
        String dateString = null;
        this.matcher = PATTERN_REPUBLICAN_DATE.matcher(republicanDate);
        if(this.matcher.find()) {
            dateString = this.matcher.group(0);
        }

        if(dateString != null) {
            dateString = this.convertRepublicanToGregorian(dateString);
            Date date = this.getDate(dateString);
            if(date != null) {
                SimpleDateFormat format = new SimpleDateFormat(pattern);
                dateString = format.format(date);
            } else if (log){
                System.out.println("Failed to parse date : " + dateString + " from " + republicanDate + ".");
            }
        } else if (log) {
            System.out.println("Failed to extract date from: " + republicanDate);
        }

        return dateString;
    }

    public Date convertAsDate(String republicanDate) {
        String dateString = null;
        this.matcher = PATTERN_REPUBLICAN_DATE.matcher(republicanDate);
        if(this.matcher.find()) {
            dateString = this.matcher.group(0);
        }

        if(dateString != null) {
            dateString = this.convertRepublicanToGregorian(dateString);
            Date date = this.getDate(dateString);
            if(date != null) {
                return date;
            }
            if (log){
                System.out.println("Failed to parse date : " + dateString + " from " + republicanDate + ".");
            }
        } else if (log){
            System.out.println("Failed to extract date from: " + republicanDate);
        }

        return null;
    }

    public Long convertAsLong(String republicanDate) {
        String dateString = null;
        this.matcher = PATTERN_REPUBLICAN_DATE.matcher(republicanDate);
        if(this.matcher.find()) {
            dateString = this.matcher.group(0);
        }

        if(dateString != null) {
            dateString = this.convertRepublicanToGregorian(dateString);
            Date date = this.getDate(dateString);
            if(date != null) {
                return Long.valueOf(date.getTime());
            }
            if (log){
                System.out.println("Failed to parse date : " + dateString + " from " + republicanDate + ".");
            }
        } else if (log) {
            System.out.println("Failed to extract date from: " + republicanDate);
        }

        return null;
    }

    private Date getDate(String dateString) {
        ParsePosition pos = new ParsePosition(0);
        DateFormat format = DateFormat.getDateInstance(2, Locale.FRANCE);
        format.setLenient(false);
        Date date = format.parse(dateString, pos);
        return date;
    }

    private String convertRepublicanToGregorian(String dateString) {
        try {
            boolean year = true;
            int e = this.getDay(this.matcher.group(1));
            int month = this.getMonth(this.matcher.group(2));
            int year1 = this.getYear(this.matcher.group(3));
            if(this.paramsAreValid(e, month, year1)) {
                dateString = this.computeGregorianDate(e, month, year1);
            } else if (log){
                System.out.println("failed to extract date from " + dateString);
            }
        } catch (Exception var5) {
            if (log){
                System.out.println("ERROR: Non handled exception for " + dateString + ". error: " + var5);
            }
        }

        return dateString;
    }

    private boolean paramsAreValid(int day, int month, int year) {
        return day > -1 && month > -1 && year > -1;
    }

    private int getYear(String input) {
        RepublicanYears[] years = RepublicanYears.values();
        RepublicanYears[] var6 = years;
        int var5 = years.length;

        for(int var4 = 0; var4 < var5; ++var4) {
            RepublicanYears year = var6[var4];
            if(input.matches(year.getYear())) {
                return year.getIndex();
            }
        }

        return -1;
    }

    private int getMonth(String input) {
        RepublicanMonths[] months = RepublicanMonths.values();
        RepublicanMonths[] var6 = months;
        int var5 = months.length;

        for(int var4 = 0; var4 < var5; ++var4) {
            RepublicanMonths month = var6[var4];
            if(input.matches(month.getMonth())) {
                return month.getIndex();
            }
        }

        return -1;
    }

    private int getDay(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException var3) {
            return -1;
        }
    }

    private String computeGregorianDate(int day, int month, int year) {
        double y = 365.25D * (double)year;
        double m = (double)month;
        double j = (double)day;
        double refYear = 1792.0D;
        double result = y - 100.0D + 30.0D * m - -j;

        while(result >= 367.0D) {
            if(refYear % 4.0D == 0.0D) {
                if(refYear == 1800.0D) {
                    ++refYear;
                    result -= 365.0D;
                } else {
                    ++refYear;
                    result -= 366.0D;
                }
            } else {
                ++refYear;
                result -= 365.0D;
            }
        }

        byte flag;
        if(refYear % 4.0D == 0.0D) {
            if(refYear == 1800.0D) {
                flag = 0;
            } else {
                flag = 1;
            }
        } else {
            flag = 0;
        }

        String monthString;
        if(result < 32.0D) {
            monthString = Months.JANVIER.getMonth();
            j = result;
        } else if(result < (double)(60 + flag)) {
            monthString = Months.FEVRIER.getMonth();
            j = result - 31.0D;
        } else {
            result -= (double)flag;
            if(result < 91.0D) {
                monthString = Months.MARS.getMonth();
                j = result - 59.0D;
            } else if(result < 121.0D) {
                monthString = Months.AVRIL.getMonth();
                j = result - 90.0D;
            } else if(result < 152.0D) {
                monthString = Months.MAI.getMonth();
                j = result - 120.0D;
            } else if(result < 182.0D) {
                monthString = Months.JUIN.getMonth();
                j = result - 151.0D;
            } else if(result < 213.0D) {
                monthString = Months.JUILLET.getMonth();
                j = result - 181.0D;
            } else if(result < 244.0D) {
                monthString = Months.AOUT.getMonth();
                j = result - 212.0D;
            } else if(result < 274.0D) {
                monthString = Months.SEPTEMBRE.getMonth();
                j = result - 243.0D;
            } else if(result < 305.0D) {
                monthString = Months.OCTOBRE.getMonth();
                j = result - 273.0D;
            } else if(result < 335.0D) {
                monthString = Months.NOVEMBRE.getMonth();
                j = result - 304.0D;
            } else {
                monthString = Months.DECEMBRE.getMonth();
                j = result - 334.0D;
            }
        }

        if(j >= 32.0D) {
            j = 1.0D;
            monthString = Months.JANVIER.getMonth();
            ++refYear;
        }

        j = Math.floor(j);
        day = (int)j;
        year = (int)refYear;
        String dateString = String.format("%s %s %s", new Object[]{Integer.valueOf(day), monthString, Integer.valueOf(year)});
        return dateString;
    }
}
