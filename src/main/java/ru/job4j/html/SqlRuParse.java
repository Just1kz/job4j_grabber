package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.model.Post;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

public class SqlRuParse implements Parse {
    public static void main(String[] args) throws Exception {
        List<Post> rsl = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String url = "https://www.sql.ru/forum/job-offers/";
            SqlRuParse rslX = new SqlRuParse();
            rsl.addAll(rslX.list(url + i));
        }

        for (Post p : rsl) {
            System.out.println(p);
        }
    }

    public static LocalDateTime createDate(String input) {
        LocalDateTime rsl;
            String[] z = input.split(", ");
            String[] time = z[1].split(":");
            int hours = Integer.parseInt(time[0]);
            int minutes = Integer.parseInt(time[1]);
            LocalTime localTime = LocalTime.of(hours, minutes);
            String[] date = z[0].split(" ");
            if (date.length == 3) {
                int year = 2000 + Integer.parseInt(date[2]);
                String month = date[1];
                int day = Integer.parseInt(date[0]);
                rsl = LocalDateTime.of(year, Objects.requireNonNull(createMonth(month)), day, hours, minutes);
            } else {
                LocalDate localDate = LocalDate.now();
                if (z[0].equals("сегодня")) {
                    localDate = LocalDate.now();
                }
                if (z[0].equals("вчера")) {
                    localDate = LocalDate.now().minusDays(1);
                }
                rsl = LocalDateTime.of(localDate, localTime);
            }
        return rsl;
    }

    public static void detail() throws IOException {
        Document docX = Jsoup.connect("https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t").get();
        Elements rowX = docX.select(".msgTable");
        String body = rowX.first().select(".msgBody").get(1).html();
        String name = rowX.first().select(".messageHeader").text();
        String date = rowX.last().select(".msgFooter").text();
        date = date.substring(0, date.indexOf('[') - 1);
        System.out.println(body);
        System.out.println(name);
        System.out.println(date);
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

    //Метод list загружает список всех постов.
    @Override
    public List<Post> list(String link) throws Exception {
        List<Post> rsl = new ArrayList<>();
        Document doc = Jsoup.connect(link).get();
        Elements row = doc.select(".postslisttopic");
        for (Element td : row) {
            Element href = td.child(0);
            rsl.add(detail(href.attr("href")));
        }
        return rsl;
    }

    //Метод detail загружает детали одного поста.
    @Override
    public Post detail(String link) throws Exception {
        Document docX = Jsoup.connect(link).get();
        Elements rowX = docX.select(".msgTable");
        String body = rowX.first().select(".msgBody").get(1).html();
        body = body.replace("<br>", " ");
        body = body.replace("<li>", " ");
        body = body.replace("</li>", " ");
        body = body.replace("<ul>", " ");
        body = body.replace("</ul>", " ");
        body = body.replace("<b>", " ");
        body = body.replace("</b>", " ");
        if (body.contains("<table border")) {
            body = body.substring(0, body.indexOf("<table border"));
        }
        if (body.contains("<a href")) {
            body = body.substring(0, body.indexOf("<a href"));
        }
        String name = rowX.first().select(".messageHeader").text();
        String date = rowX.last().select(".msgFooter").text();
        date = date.substring(0, date.indexOf('[') - 1);
        LocalDateTime dateTime = createDate(date);
        return new Post(name, body, link, dateTime);
    }
}
