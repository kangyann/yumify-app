package com.yumify.lib;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatCurrency {

    private static final Locale locale = new Locale("id", "ID");
    public String Get(int currency) {
        return "Rp. " + NumberFormat.getInstance(locale).format(currency);
    }
}
