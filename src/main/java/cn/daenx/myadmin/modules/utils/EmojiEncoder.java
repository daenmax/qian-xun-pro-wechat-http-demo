package cn.daenx.myadmin.modules.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

/**
 * Emoji编码工具类
 * 用于将字符串中的Emoji表情转换为Unicode转义序列
 *
 * @author DaenMax
 */
public class EmojiEncoder {
    /**
     * //使用方法
     * public static void main(String[] args) {
     *         // 简单用法
     *         String text = "你好👋，欢迎来到Java世界🎮！";
     *
     *         // 只编码Emoji
     *         String encoded = EmojiEncoder.encodeEmojisOnly(text);
     *         System.out.println("编码结果: " + encoded);
     *         // 输出: 你好\uD83D\uDC4B，欢迎来到Java世界\uD83C\uDFAE！
     *
     *         // 解码恢复
     *         String decoded = EmojiEncoder.decodeUnicodeEscape(encoded);
     *         System.out.println("解码结果: " + decoded);
     *
     *         // 检查是否包含Emoji
     *         boolean hasEmoji = EmojiEncoder.containsEmoji(text);
     *         System.out.println("是否包含Emoji: " + hasEmoji);
     *
     *         // 提取所有Emoji
     *         List<String> emojis = EmojiEncoder.extractEmojis(text);
     *         System.out.println("提取到的Emoji: " + emojis);
     *
     *     }
     */

    /**
     * 匹配Emoji字符的正则表达式
     * 包括：
     * 1. 基本Emoji：U+1F600到U+1F64F
     * 2. 补充Emoji：U+1F300到U+1F5FF
     * 3. 交通和地图符号：U+1F680到U+1F6FF
     * 4. 杂项符号：U+2600到U+26FF
     * 5. 装饰符号：U+2700到U+27BF
     * 6. 补充符号和象形文字：U+1F900到U+1F9FF
     * 7. 旗帜：U+1F1E6到U+1F1FF
     */
    private static final Pattern EMOJI_PATTERN = Pattern.compile(
            "[" +
                    "\\uD83C[\\uDF00-\\uDFFF]|" +                // 杂项符号和象形文字
                    "\\uD83D[\\uDC00-\\uDE4F\\uDE80-\\uDEFF]|" + // 表情、人物、动物等
                    "\\uD83E[\\uDD00-\\uDDFF]|" +                // 补充符号和象形文字
                    "[\\u2600-\\u26FF]|" +                       // 杂项符号
                    "[\\u2700-\\u27BF]|" +                       // 装饰符号
                    "\\uD83C[\\uDDE6-\\uDDFF]\\uD83C[\\uDDE6-\\uDDFF]|" + // 国旗
                    "[\\uD83C\\uDDE6-\\uD83C\\uDDFF][\\uD83C\\uDDE6-\\uD83C\\uDDFF]" + // 另一种国旗表示
                    "]"
    );

    /**
     * 简化的Emoji检测模式（性能更好）
     * 检测所有代理对（大多数Emoji都是代理对）
     */
    private static final Pattern SURROGATE_PAIR_PATTERN =
            Pattern.compile("[\\uD800-\\uDBFF][\\uDC00-\\uDFFF]");

    /**
     * 更精确的Emoji检测（支持最新Unicode标准）
     */
    private static final Pattern EMOJI_DETECT_PATTERN = Pattern.compile(
            "(?:[" +
                    "\\u2600-\\u26FF\\u2700-\\u27BF" +  // 杂项符号和装饰符号
                    "]" +
                    "|" +
                    "\\uD83C[\\uDF00-\\uDFFF]" +           // U+1F300 - U+1F3FF
                    "|" +
                    "\\uD83D[\\uDC00-\\uDE4F\\uDE80-\\uDEFF]" + // U+1F400 - U+1F64F, U+1F680 - U+1F6FF
                    "|" +
                    "\\uD83E[\\uDD00-\\uDDFF]" +           // U+1F900 - U+1F9FF
                    "|" +
                    "\\uD83C[\\uDDE6-\\uDDFF]\\uD83C[\\uDDE6-\\uDDFF]" + // 国旗
                    ")"
    );

    /**
     * 判断一个代码点是否是Emoji
     */
    private static boolean isEmojiCodePoint(int codePoint) {
        // Emoji范围定义
        return
                // 杂项符号
                (codePoint >= 0x2600 && codePoint <= 0x26FF) ||
                        // 装饰符号
                        (codePoint >= 0x2700 && codePoint <= 0x27BF) ||
                        // 交通和地图符号
                        (codePoint >= 0x1F300 && codePoint <= 0x1F5FF) ||
                        // 表情符号
                        (codePoint >= 0x1F600 && codePoint <= 0x1F64F) ||
                        // 杂项符号和象形文字
                        (codePoint >= 0x1F680 && codePoint <= 0x1F6FF) ||
                        // 补充符号和象形文字
                        (codePoint >= 0x1F900 && codePoint <= 0x1F9FF) ||
                        // 表情符号补充
                        (codePoint >= 0x1FA70 && codePoint <= 0x1FAFF) ||
                        // 标志符号
                        (codePoint >= 0x1F1E6 && codePoint <= 0x1F1FF);
    }

    /**
     * 判断一个字符是否是Emoji（通过代理对判断）
     */
    private static boolean isEmojiSurrogatePair(char high, char low) {
        // 检查是否在代理对范围内
        if (high >= 0xD800 && high <= 0xDBFF &&
                low >= 0xDC00 && low <= 0xDFFF) {

            // 计算代码点
            int codePoint = Character.toCodePoint(high, low);
            return isEmojiCodePoint(codePoint);
        }
        return false;
    }

    /**
     * 主要方法：只编码字符串中的Emoji
     *
     * @param input 输入字符串
     * @return 返回Emoji被编码为Unicode转义序列的字符串
     */
    public static String encodeEmojisOnly(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        int length = input.length();

        for (int i = 0; i < length; i++) {
            char current = input.charAt(i);

            // 检查是否可能是代理对的开头
            if (Character.isHighSurrogate(current) && i + 1 < length) {
                char next = input.charAt(i + 1);

                // 如果是代理对，检查是否是Emoji
                if (Character.isLowSurrogate(next) && isEmojiSurrogatePair(current, next)) {
                    // 编码Emoji
                    result.append(String.format("\\u%04X\\u%04X", (int) current, (int) next));
                    i++; // 跳过下一个字符
                } else {
                    // 不是Emoji，直接添加
                    result.append(current);
                }
            }
            // 检查是否是基本平面的Emoji
            else if (isEmojiCodePoint(current)) {
                result.append(String.format("\\u%04X", (int) current));
            }
            // 普通字符，直接添加
            else {
                result.append(current);
            }
        }

        return result.toString();
    }

    /**
     * 使用正则表达式方法编码Emoji（更简洁）
     */
    public static String encodeEmojisWithRegex(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        Matcher matcher = EMOJI_DETECT_PATTERN.matcher(input);
        int lastIndex = 0;

        while (matcher.find()) {
            // 添加非Emoji部分
            result.append(input, lastIndex, matcher.start());

            // 编码Emoji部分
            String emoji = matcher.group();
            if (emoji.length() == 1) {
                // 基本平面Emoji
                result.append(String.format("\\u%04X", (int) emoji.charAt(0)));
            } else {
                // 代理对Emoji
                for (char c : emoji.toCharArray()) {
                    result.append(String.format("\\u%04X", (int) c));
                }
            }

            lastIndex = matcher.end();
        }

        // 添加剩余部分
        if (lastIndex < input.length()) {
            result.append(input.substring(lastIndex));
        }

        return result.toString();
    }

    /**
     * 解码Unicode转义序列（恢复Emoji）
     */
    public static String decodeUnicodeEscape(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return encoded;
        }

        StringBuilder result = new StringBuilder();
        String[] parts = encoded.split("\\\\u");

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == 0 && part.isEmpty()) {
                continue; // 忽略第一个空字符串
            }

            if (!part.isEmpty()) {
                try {
                    // 尝试解析为Unicode转义
                    int length = Math.min(4, part.length());
                    String hex = part.substring(0, length);
                    int codePoint = Integer.parseInt(hex, 16);

                    // 添加到结果
                    result.append(Character.toChars(codePoint));

                    // 如果有剩余字符，直接添加
                    if (length < part.length()) {
                        result.append(part.substring(length));
                    }
                } catch (NumberFormatException e) {
                    // 不是有效的Unicode转义，直接添加
                    result.append(part);
                }
            }
        }

        return result.toString();
    }

    /**
     * 检测字符串中是否包含Emoji
     */
    public static boolean containsEmoji(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        int length = input.length();
        for (int i = 0; i < length; i++) {
            char current = input.charAt(i);

            if (Character.isHighSurrogate(current) && i + 1 < length) {
                char next = input.charAt(i + 1);
                if (Character.isLowSurrogate(next) && isEmojiSurrogatePair(current, next)) {
                    return true;
                }
            } else if (isEmojiCodePoint(current)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 提取字符串中的所有Emoji
     */
    public static List<String> extractEmojis(String input) {
        List<String> emojis = new ArrayList<>();

        if (input == null || input.isEmpty()) {
            return emojis;
        }

        int length = input.length();
        for (int i = 0; i < length; i++) {
            char current = input.charAt(i);

            if (Character.isHighSurrogate(current) && i + 1 < length) {
                char next = input.charAt(i + 1);
                if (Character.isLowSurrogate(next) && isEmojiSurrogatePair(current, next)) {
                    emojis.add(String.valueOf(new char[]{current, next}));
                    i++; // 跳过下一个字符
                }
            } else if (isEmojiCodePoint(current)) {
                emojis.add(String.valueOf(current));
            }
        }

        return emojis;
    }

    /**
     * 获取Emoji的详细信息
     */
    public static String getEmojiInfo(String input) {
        if (input == null || input.isEmpty()) {
            return "Empty input";
        }

        StringBuilder info = new StringBuilder();
        List<String> emojis = extractEmojis(input);

        info.append("输入字符串: ").append(input).append("\n");
        info.append("包含Emoji数量: ").append(emojis.size()).append("\n");

        if (!emojis.isEmpty()) {
            info.append("Emoji列表:\n");
            for (String emoji : emojis) {
                info.append("  ").append(emoji).append(" -> ");

                if (emoji.length() == 1) {
                    int codePoint = emoji.charAt(0);
                    info.append(String.format("U+%04X", codePoint))
                            .append(" (\\u").append(String.format("%04X", codePoint)).append(")");
                } else {
                    char high = emoji.charAt(0);
                    char low = emoji.charAt(1);
                    int codePoint = Character.toCodePoint(high, low);
                    info.append(String.format("U+%05X", codePoint))
                            .append(" (\\u").append(String.format("%04X", (int) high))
                            .append("\\u").append(String.format("%04X", (int) low)).append(")");
                }
                info.append("\n");
            }
        }

        return info.toString();
    }
}
