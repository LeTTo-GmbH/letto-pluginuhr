package at.letto.tools.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;


interface MsgInterface {
    @JsonCreator
    static MsgInterface valueOf(String value) {
        return MsgType.valueOf(value);
    }
}

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum MsgType implements MsgInterface {
    ERROR,
    WARNING,
    INFO,
    OK;
}
