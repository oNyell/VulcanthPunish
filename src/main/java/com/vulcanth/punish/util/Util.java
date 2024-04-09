package com.vulcanth.punish.util;

import java.util.concurrent.TimeUnit;

public class Util {
    public static String getOrdinalNumber(int number) {
        String[] suffixes = {"º", "º", "º", "º", "º", "º", "º", "º", "º", "º"};

        if (number >= 11 && number <= 13) {
            return number + "º";
        } else {
            int suffixIndex = number % 10;
            return number + suffixes[suffixIndex];
        }
    }

    public static String formatTime(long t) {
        final long years = TimeUnit.MILLISECONDS.toDays(t) / 365;
        t -= TimeUnit.DAYS.toMillis(years * 365);
        final long months = TimeUnit.MILLISECONDS.toDays(t) / 30;
        t -= TimeUnit.DAYS.toMillis(months * 30);
        final long weeks = TimeUnit.MILLISECONDS.toDays(t) / 7;
        t -= TimeUnit.DAYS.toMillis(weeks * 7);
        final long days = TimeUnit.MILLISECONDS.toDays(t);
        t -= TimeUnit.DAYS.toMillis(days);
        final long hours = TimeUnit.MILLISECONDS.toHours(t);
        t -= TimeUnit.HOURS.toMillis(hours);
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(t);
        t -= TimeUnit.MINUTES.toMillis(minutes);
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(t);

        StringBuilder result = new StringBuilder();

        if (years > 0) {
            result.append(years).append(years == 1 ? " ano" : " anos");
        }
        if (months > 0) {
            if (result.length() > 0) {
                result.append(" e ");
            }
            result.append(months).append(months == 1 ? " mês" : " meses");
        }
        if (weeks > 0) {
            if (result.length() > 0) {
                result.append(" e ");
            }
            result.append(weeks).append(weeks == 1 ? " semana" : " semanas");
        }
        if (days > 0) {
            if (result.length() > 0) {
                result.append(" e ");
            }
            result.append(days).append(days == 1 ? " dia" : " dias");
        }
        if (hours > 0) {
            if (result.length() > 0) {
                result.append(" e ");
            }
            result.append(hours).append(hours == 1 ? " hora" : " horas");
        }
        if (minutes > 0) {
            if (result.length() > 0) {
                result.append(" e ");
            }
            result.append(minutes).append(minutes == 1 ? " minuto" : " minutos");
        }
        if (seconds > 0) {
            if (result.length() > 0) {
                result.append(" e ");
            }
            result.append(seconds).append(seconds == 1 ? " segundo" : " segundos");
        }

        return result.toString();
    }

    public static String fromLongWithoutDiff(final long... logs) {
        StringBuilder result = new StringBuilder();

        for (int j = 0; j < logs.length; j++) {
            long log = logs[j];
            long length = log;
            final long years = TimeUnit.MILLISECONDS.toDays(length) / 365;
            length -= TimeUnit.DAYS.toMillis(years * 365);
            final long months = TimeUnit.MILLISECONDS.toDays(length) / 30;
            length -= TimeUnit.DAYS.toMillis(months * 30);
            final long weeks = TimeUnit.MILLISECONDS.toDays(length) / 7;
            length -= TimeUnit.DAYS.toMillis(weeks * 7);
            final long days = TimeUnit.MILLISECONDS.toDays(length);
            length -= TimeUnit.DAYS.toMillis(days);
            final long hours = TimeUnit.MILLISECONDS.toHours(length);
            length -= TimeUnit.HOURS.toMillis(hours);
            final long minutes = TimeUnit.MILLISECONDS.toMinutes(length);
            length -= TimeUnit.MINUTES.toMillis(minutes);
            final long seconds = TimeUnit.MILLISECONDS.toSeconds(length);

            if (j > 0) {
                result.append(", ");
            }

            if (years > 0) {
                result.append(years).append(years == 1 ? " ano" : " anos");
            }
            if (months > 0) {
                if (result.length() > 0) {
                    result.append(" e ");
                }
                result.append(months).append(months == 1 ? " mês" : " meses");
            }
            if (weeks > 0) {
                if (result.length() > 0) {
                    result.append(" e ");
                }
                result.append(weeks).append(weeks == 1 ? " semana" : " semanas");
            }
            if (days > 0) {
                if (result.length() > 0) {
                    result.append(" e ");
                }
                result.append(days).append(days == 1 ? " dia" : " dias");
            }
            if (hours > 0) {
                if (result.length() > 0) {
                    result.append(" e ");
                }
                result.append(hours).append(hours == 1 ? " hora" : " horas");
            }
            if (minutes > 0) {
                if (result.length() > 0) {
                    result.append(" e ");
                }
                result.append(minutes).append(minutes == 1 ? " minuto" : " minutos");
            }
            if (seconds > 0) {
                if (result.length() > 0) {
                    result.append(" e ");
                }
                result.append(seconds).append(seconds == 1 ? " segundo" : " segundos");
            }
        }

        return result.toString();
    }

    public static String fromLong(final Long log) {
        String time = "";
        final long totalLength = log;
        final long timeLeft = totalLength - System.currentTimeMillis();
        final long seconds = timeLeft / 1000L;
        time = fromLong(time, seconds);
        return time;
    }

    private static String fromLong(String restingTime, final long length) {
        final long years = TimeUnit.SECONDS.toDays(length) / 365;
        final long months = TimeUnit.SECONDS.toDays(length) / 30 - (years * 12);
        final long weeks = TimeUnit.SECONDS.toDays(length) / 7 - (years * 52 + months * 4);
        final int days = (int) TimeUnit.SECONDS.toDays(length) - (int) (years * 365 + months * 30 + weeks * 7);
        final long hours = TimeUnit.SECONDS.toHours(length) - TimeUnit.DAYS.toHours(TimeUnit.SECONDS.toDays(length));
        final long minutes = TimeUnit.SECONDS.toMinutes(length) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(length));
        final long seconds = TimeUnit.SECONDS.toSeconds(length) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(length));

        String totalYears = String.valueOf(years) + ((years == 1) ? " ano" : " anos");
        String totalMonths = String.valueOf(months) + ((months == 1) ? " mês" : " meses");
        String totalWeeks = String.valueOf(weeks) + ((weeks == 1) ? " semana" : " semanas");
        String totalDays = String.valueOf(days) + ((days == 1) ? " dia" : " dias");
        String totalHours = String.valueOf(hours) + ((hours == 1) ? " hora" : " horas");
        String totalMinutes = String.valueOf(minutes) + ((minutes == 1) ? " minuto" : " minutos");
        String totalSeconds = String.valueOf(seconds) + ((seconds == 1) ? " segundo" : " segundos");

        if (years == 0) {
            totalYears = "";
        }
        if (months == 0) {
            totalMonths = "";
        }
        if (weeks == 0) {
            totalWeeks = "";
        }
        if (days == 0) {
            totalDays = "";
        }
        if (hours == 0) {
            totalHours = "";
        }
        if (minutes == 0) {
            totalMinutes = "";
        }
        if (seconds == 0) {
            totalSeconds = "";
        }

        StringBuilder builder = new StringBuilder();
        if (!totalYears.isEmpty()) {
            builder.append(totalYears);
        }
        if (!totalMonths.isEmpty()) {
            if (builder.length() > 0) {
                builder.append(" e ");
            }
            builder.append(totalMonths);
        }
        if (!totalWeeks.isEmpty()) {
            if (builder.length() > 0) {
                builder.append(" e ");
            }
            builder.append(totalWeeks);
        }
        if (!totalDays.isEmpty()) {
            if (builder.length() > 0) {
                builder.append(" e ");
            }
            builder.append(totalDays);
        }
        if (!totalHours.isEmpty()) {
            if (builder.length() > 0) {
                builder.append(" e ");
            }
            builder.append(totalHours);
        }
        if (!totalMinutes.isEmpty()) {
            if (builder.length() > 0) {
                builder.append(" e ");
            }
            builder.append(totalMinutes);
        }
        if (!totalSeconds.isEmpty()) {
            if (builder.length() > 0) {
                builder.append(" e ");
            }
            builder.append(totalSeconds);
        }

        restingTime = builder.toString().trim();
        if (restingTime.isEmpty()) {
            restingTime = "0 segundos";
        }
        return restingTime;
    }
}
