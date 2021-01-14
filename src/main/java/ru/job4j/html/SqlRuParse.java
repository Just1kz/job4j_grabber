package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        Elements row = doc.select(".postslisttopic");
        Elements row2 = doc.select(".altCol");
        for (Element td : row) {
            Element href = td.child(0);
            System.out.println(href.attr("href"));
            System.out.println(href.text());
        }
        List<String> list = new ArrayList<>();
        List<String> input = new ArrayList<>();
        List<LocalDateTime> output = new ArrayList<>();
        for (Element td : row2) {
            list.add(td.text());
        }
        for (int x = 1; x < list.size(); x = x + 2) {
                input.add(list.get(x));
        }

        for (String o : input) {
            System.out.println(o);
        }

        int year;
        String month;
        int day;
        int hours;
        int minutes;
        LocalDateTime rsl;
        LocalDate localDate = LocalDate.now();
        LocalTime localTime;
        for (String x : input) {
            String[] z = x.split(", ");
            String[] time = z[1].split(":");
            hours = Integer.parseInt(time[0]);
            minutes = Integer.parseInt(time[1]);
            localTime = LocalTime.of(hours, minutes);
            if (z[0].length() < 3) {
                String[] date = z[0].split(" ");
                year = 2000 + Integer.parseInt(date[2]);
                month = date[1];
                day = Integer.parseInt(date[0]);
                rsl = LocalDateTime.of(year, Objects.requireNonNull(createMonth(month)), day, hours, minutes);
            } else {
                String p = z[0];
                switch (p.toLowerCase()) {
                    case("сегодня"):
                        localDate = LocalDate.now();
                    case ("вчера"):
                        localDate = LocalDate.now().minusDays(1);
                    case ("позавчера"):
                        localDate = LocalDate.now().minusDays(2);
                    default:
                        break;
                }
                rsl = LocalDateTime.of(localDate, localTime);
            }
            output.add(rsl);
        }

        for (LocalDateTime zx : output) {
            System.out.println(zx);
        }
    }

    public static Month createMonth(String s) {
        if (s.equals("янв")) {
            return Month.JANUARY;
        }
        if (s.equals("фев")) {
            return Month.FEBRUARY;
        }
        if (s.equals("мар")) {
            return Month.MARCH;
        }
        if (s.equals("апр")) {
            return Month.APRIL;
        }
        if (s.equals("май")) {
            return Month.MAY;
        }
        if (s.equals("июн")) {
            return Month.JUNE;
        }
        if (s.equals("июл")) {
            return Month.JULY;
        }
        if (s.equals("авг")) {
            return Month.AUGUST;
        }
        if (s.equals("сен")) {
            return Month.SEPTEMBER;
        }
        if (s.equals("окт")) {
            return Month.OCTOBER;
        }
        if (s.equals("ноя")) {
            return Month.NOVEMBER;
        }
        if (s.equals("дек")) {
            return Month.DECEMBER;
        }
        return null;
    }
}
