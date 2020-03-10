package com.oldturok.turok.setting;

import com.oldturok.turok.util.Pair;

import java.util.StringTokenizer;
import java.util.HashMap;

public class SettingsRegister {
    public static final SettingsRegister ROOT = new SettingsRegister();

    public HashMap<String, SettingsRegister> registerHashMap = new HashMap<>();
    public HashMap<String, Setting> settingHashMap = new HashMap<>();

    public SettingsRegister subregister(String name) {
        if (registerHashMap.containsKey(name)) return registerHashMap.get(name);
        SettingsRegister register = new SettingsRegister();
        registerHashMap.put(name, register);
        return register;
    }

    private void put(String name, Setting setting) {
        settingHashMap.put(name, setting);
    }

    public static void register(String name, Setting setting) {
        Pair<String, SettingsRegister> pair = dig(name);
        pair.getValue().put(pair.getKey(), setting);
    }

    public Setting getSetting(String group) {
        return settingHashMap.get(group);
    }

    public static Setting get(String group) {
        Pair<String, SettingsRegister> pair = dig(group);
        return pair.getValue().getSetting(pair.getKey());
    }

    private static Pair<String, SettingsRegister> dig(String group) {
        SettingsRegister current = SettingsRegister.ROOT;
        StringTokenizer tokenizer = new StringTokenizer(group, ".");
        String previousToken = null;
        while (tokenizer.hasMoreTokens()) {
            if (previousToken == null) {
                previousToken = tokenizer.nextToken();
            } else {
                String token = tokenizer.nextToken();
                current = current.subregister(previousToken);
                previousToken = token;
            }
        }
        return new Pair<>(previousToken == null ? "" : previousToken, current);
    }

}