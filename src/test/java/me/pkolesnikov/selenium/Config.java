package me.pkolesnikov.selenium;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

import static java.text.MessageFormat.format;

final class Config {
    public final String user;
    public final String pass;

    public static Config from(Path path) throws IOException {
        final Properties cfg = new Properties();
        try (final FileInputStream source = new FileInputStream(path.toFile())) {
            cfg.load(source);
        } catch (FileNotFoundException e) {
            System.err.println(format("No config file found in '{0}'", path));
            System.err.println(format("Writing example config file to '{0}'", path));
            try (FileOutputStream out = new FileOutputStream(path.toFile())) {
                cfg.put("user", "user@example.com");
                cfg.put("pass", "super-secret-password");
                cfg.store(out, "example config");
            }

            throw e;
        }

        return new Config(cfg.getProperty("user"), cfg.getProperty("pass"));
    }

    private Config(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }
}
