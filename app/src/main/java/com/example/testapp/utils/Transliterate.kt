package com.example.testapp.utils

fun transliterate(text: String): String {
    val cyrillic = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"
    val latin = "a|b|v|g|d|e|e|zh|z|i|i|k|l|m|n|o|p|r|s|t|u|f|kh|ts|ch|sh|shch||y||e|iu|ia|A|B|V|G|D|E|E|Zh|Z|I|I|K|L|M|N|O|P|R|S|T|U|F|Kh|Ts|Ch|Sh|Shch||Y||E|Iu|Ia".split("|")

    val map = (cyrillic.indices).associate { cyrillic[it] to latin[it] }
    return text.map { map[it] ?: it }.joinToString("")
}