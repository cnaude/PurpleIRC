/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Utilities;

/**
 *
 * @author cnaude
 */
public class RegexGlobber {
    //http://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns
    public String createRegexFromGlob(String glob) {
        String out = "^";
        for (int i = 0; i < glob.length(); ++i) {
            final char c = glob.charAt(i);
            switch (c) {
                case '*':
                    out += ".*";
                    break;
                case '?':
                    out += '.';
                    break;
                case '.':
                    out += "\\.";
                    break;
                case '\\':
                    out += "\\\\";
                    break;
                default:
                    out += c;
            }
        }
        out += '$';
        return out;
    }
}
