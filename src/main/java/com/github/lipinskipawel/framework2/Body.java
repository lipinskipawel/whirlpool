package com.github.lipinskipawel.framework2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonTypeIdResolver(value = BodyResolver.class)
public abstract class Body {
    @JsonProperty("msg_id")
    public int msgId;
    @JsonProperty("in_reply_to")
    public int inReplyTo;
}
