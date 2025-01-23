package com.example.demo.dto.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "command")
public class XmlCommandDto {
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    private String id;

    @JacksonXmlProperty(localName = "get")
    private XmlGet getConsumer;

    @JacksonXmlProperty(localName = "history")
    private XmlHistory historyConsumer;

    public XmlCommandDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public XmlGet getGetConsumer() {
        return getConsumer;
    }

    public void setGetConsumer(XmlGet getConsumer) {
        this.getConsumer = getConsumer;
    }

    public XmlHistory getHistoryConsumer() {
        return historyConsumer;
    }

    public void setHistoryConsumer(XmlHistory historyConsumer) {
        this.historyConsumer = historyConsumer;
    }

    public static class XmlGet {

        @JacksonXmlProperty(isAttribute = true)
        private String consumer;

        @JacksonXmlProperty
        private String currency;

        public XmlGet() {
        }

        public XmlGet(String consumer, String currency) {
            this.consumer = consumer;
            this.currency = currency;
        }

        public String getConsumer() {
            return consumer;
        }

        public void setConsumer(String consumer) {
            this.consumer = consumer;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }

    public static class XmlHistory {
        @JacksonXmlProperty(isAttribute = true)
        private String consumer;

        @JacksonXmlProperty(isAttribute = true)
        private String currency;

        @JacksonXmlProperty(isAttribute = true)
        private int period;

        public XmlHistory() {
        }

        public XmlHistory(String consumer, String currency, int period) {
            this.consumer = consumer;
            this.currency = currency;
            this.period = period;
        }

        public String getConsumer() {
            return consumer;
        }

        public void setConsumer(String consumer) {
            this.consumer = consumer;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public int getPeriod() {
            return period;
        }

        public void setPeriod(int period) {
            this.period = period;
        }
    }
}
