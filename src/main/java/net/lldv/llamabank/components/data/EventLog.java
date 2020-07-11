package net.lldv.llamabank.components.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EventLog {

    private final LogType logType;
    private final String target;
    private final double amount;
    private final String date;

}
