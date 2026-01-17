module stud_sluzba_desktop_client {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires spring.boot;
    requires spring.context;
    requires spring.boot.autoconfigure;
    requires spring.beans;
    requires javafx.fxml;
    requires spring.web;
    requires spring.core;
    requires static lombok;

    opens reports;
    opens org.raflab.studsluzbadesktopclient.models to jasperreports;

    exports org.raflab.studsluzbadesktopclient;
    exports org.raflab.studsluzbadesktopclient.controllers;
    exports org.raflab.studsluzbadesktopclient.services;
    exports org.raflab.studsluzbadesktopclient.models;

    opens org.raflab.studsluzbadesktopclient.services to spring.core, javafx.fxml;
    opens org.raflab.studsluzbadesktopclient.controllers to spring.core, javafx.fxml;

    opens org.raflab.studsluzbadesktopclient to javafx.fxml, spring.beans, spring.context, spring.core;

    opens org.raflab.studsluzbadesktopclient.config to
            spring.beans,
            spring.context,
            spring.core,
            spring.boot;

    exports org.raflab.studsluzbadesktopclient.coder;
    opens org.raflab.studsluzbadesktopclient.coder to javafx.fxml, spring.beans, spring.context, spring.core;

    // Reports
    requires java.sql;
    requires java.desktop;
    requires spring.webflux;
    requires java.net.http;
    requires reactor.core;

    // DTO
    requires studsluzba.common;
    requires java.prefs;
    requires reactor.netty.http;
    requires org.reactivestreams;
    requires java.naming;
    requires ch.qos.logback.classic;
    requires com.fasterxml.jackson.databind;
    requires jasperreports;
}