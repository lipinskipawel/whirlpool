package com.github.lipinskipawel.framework;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Optional.empty;

public final class Broadcast {

    private final String type;
    private final Optional<Integer> message;
    private final List<Integer> messagesFromOtherNodes;
    private final List<String> visitedNodes;
    private final List<Integer> messages;
    private final Map<String, List<String>> topology;

    private Broadcast(Builder builder) {
        this.type = builder.type;
        this.message = builder.message;
        this.messagesFromOtherNodes = builder.messagesFromOtherNodes;
        this.visitedNodes = builder.visitedNodes;
        this.messages = builder.messages;
        this.topology = builder.topology;
    }

    public static Broadcast.Builder broadcast(String type) {
        return new Broadcast.Builder(type);
    }

    public String type() {
        return type;
    }

    public Optional<Integer> message() {
        return message;
    }

    public List<Integer> messagesFromOtherNodes() {
        return messagesFromOtherNodes;
    }

    public List<String> visitedNodes() {
        return visitedNodes;
    }

    public List<Integer> messages() {
        return messages;
    }

    public Map<String, List<String>> topology() {
        return topology;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Broadcast that = (Broadcast) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(message, that.message) &&
                Objects.equals(messagesFromOtherNodes, that.messagesFromOtherNodes) &&
                Objects.equals(visitedNodes, that.visitedNodes) &&
                Objects.equals(messages, that.messages) &&
                Objects.equals(topology, that.topology);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, message, messagesFromOtherNodes, visitedNodes, messages, topology);
    }

    @Override
    public String toString() {
        return "Broadcast{" +
                "type='" + type + '\'' +
                ", message=" + message +
                ", messagesFromOtherNodes=" + messagesFromOtherNodes +
                ", visitedNodes=" + visitedNodes +
                ", messages=" + messages +
                ", topology=" + topology +
                '}';
    }

    public static class Builder {
        private final String type;
        private Optional<Integer> message = empty();
        private List<Integer> messagesFromOtherNodes = emptyList();
        private List<String> visitedNodes = emptyList();
        private List<Integer> messages = emptyList();
        private Map<String, List<String>> topology = emptyMap();

        private Builder(String type) {
            this.type = type;
        }

        public Builder withMessage(Optional<Integer> message) {
            this.message = message;
            return this;
        }

        public Builder withMessagesFromOtherNodes(List<Integer> messagesFromOtherNodes) {
            this.messagesFromOtherNodes = messagesFromOtherNodes;
            return this;
        }

        public Builder withVisitedNodes(List<String> visitedNodes) {
            this.visitedNodes = visitedNodes;
            return this;
        }

        public Builder withMessages(List<Integer> messages) {
            this.messages = messages;
            return this;
        }

        public Builder withTopology(Map<String, List<String>> topology) {
            this.topology = topology;
            return this;
        }

        public Broadcast build() {
            return new Broadcast(this);
        }
    }
}
