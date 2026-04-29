package com.roomwise.professor.ui.grade;

import com.roomwise.professor.data.remote.dto.AulaDtos;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class GradeUiMapper {
    private GradeUiMapper() {}

    public static List<GradeDayUi> map(List<AulaDtos.AulaDto> aulas) {
        ZoneId zone = ZoneId.systemDefault();
        DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm", new Locale("pt", "BR"));
        Locale localePt = new Locale("pt", "BR");

        aulas.sort(Comparator.comparing(a -> parseOffset(a.dataHora)));
        Map<LocalDate, List<AulaDtos.AulaDto>> byDay = new LinkedHashMap<>();
        for (AulaDtos.AulaDto aula : aulas) {
            LocalDate key = parseOffset(aula.dataHora).atZoneSameInstant(zone).toLocalDate();
            byDay.computeIfAbsent(key, ignored -> new ArrayList<>()).add(aula);
        }

        List<GradeDayUi> out = new ArrayList<>();
        for (Map.Entry<LocalDate, List<AulaDtos.AulaDto>> entry : byDay.entrySet()) {
            String title = formatPtDate(entry.getKey(), localePt);
            StringBuilder content = new StringBuilder();
            List<AulaDtos.AulaDto> itens = entry.getValue();
            for (int i = 0; i < itens.size(); i++) {
                AulaDtos.AulaDto a = itens.get(i);
                OffsetDateTime ini = parseOffset(a.dataHora);
                String horario = ini.format(hourFormatter);
                if (a.dataHoraFim != null && !a.dataHoraFim.trim().isEmpty()) {
                    horario += " - " + parseOffset(a.dataHoraFim).format(hourFormatter);
                }
                String turma = a.turma != null ? fallback(a.turma.nomeTurma) : "-";
                String disciplina = (a.turma != null && a.turma.disciplina != null) ? fallback(a.turma.disciplina.nomeDiscp) : "-";
                String sala = a.sala != null ? fallback(a.sala.nomeSala) : "-";
                String local = (a.sala != null) ? a.sala.local : null;
                String categoria = (a.turma != null && a.turma.disciplina != null && a.turma.disciplina.categoria != null)
                        ? a.turma.disciplina.categoria.nome : null;

                content.append(horario).append('\n');
                content.append(turma).append(" · ").append(disciplina).append('\n');
                content.append(sala);
                if (local != null && !local.trim().isEmpty()) {
                    content.append(" - ").append(local.trim());
                }
                if (categoria != null && !categoria.trim().isEmpty()) {
                    content.append('\n').append(categoria.trim());
                }
                if (i < itens.size() - 1) {
                    content.append("\n\n");
                }
            }
            out.add(new GradeDayUi(title, content.toString()));
        }
        return out;
    }

    private static String fallback(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private static String formatPtDate(LocalDate date, Locale locale) {
        String dow = date.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        String month = date.getMonth().getDisplayName(TextStyle.FULL, locale);
        return capitalize(dow, locale) + ", " + date.getDayOfMonth() + " de " + month;
    }

    private static String capitalize(String text, Locale locale) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase(locale) + text.substring(1);
    }

    private static OffsetDateTime parseOffset(String iso) {
        try {
            return OffsetDateTime.parse(iso);
        } catch (Exception ignored) {
            try {
                return LocalDateTime.parse(iso, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        .atZone(ZoneId.systemDefault())
                        .toOffsetDateTime();
            } catch (Exception ignored2) {
                return Instant.parse(iso).atZone(ZoneId.systemDefault()).toOffsetDateTime();
            }
        }
    }
}
