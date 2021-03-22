package yuml.generator.kafka.streams;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

enum YumlColours {
    yellowgreen,
    yellow,
    whitesmoke,
    white,
    wheat,
    violet,
    turquoise,
    tomato,
    thistle,
    tan,
    steelblue,
    springgreen,
    snow,
    slategray,
    slateblue,
    skyblue,
    sienna,
    seashell,
    seagreen,
    sandybrown,
    salmon,
    saddlebrown,
    royalblue,
    rosybrown,
    red,
    purple,
    powderblue,
    plum,
    pink,
    peru,
    peachpuff,
    papayawhip,
    palevioletred,
    paleturquoise,
    palegreen,
    palegoldenrod,
    orchid,
    orangered,
    orange,
    olivedrab,
    oldlace,
    navajowhite,
    moccasin,
    mistyrose,
    mediumvioletred,
    mediumturquoise,
    mediumspringgreen,
    mediumslateblue,
    mediumseagreen,
    mediumpurple,
    mediumorchid,
    mediumblue,
    mediumaquamarine,
    maroon,
    magenta,
    linen,
    limegreen,
    lightyellow,
    lightsteelblue,
    lightslategray,
    lightskyblue,
    lightseagreen,
    lightsalmon,
    lightpink,
    lightgray,
    lightcyan,
    lightcoral,
    lightblue,
    lemonchiffon,
    lawngreen,
    lavenderblush,
    lavender,
    khaki,
    indianred,
    hotpink,
    honeydew,
    greenyellow,
    green,
    gray,
    goldenrod,
    gold,
    ghostwhite,
    gainsboro,
    forestgreen,
    floralwhite,
    firebrick,
    dodgerblue,
    deepskyblue,
    deeppink,
    darkviolet,
    darkslateblue,
    darkseagreen,
    darksalmon,
    darkorchid,
    darkorange,
    darkgoldenrod,
    cyan,
    crimson,
    cornsilk,
    coral,
    chocolate,
    chartreuse,
    cadetblue,
    burlywood,
    brown,
    blueviolet,
    blue,
    black,
    bisque,
    beige,
    azure,
    aquamarine,
    antiquewhite,
    aliceblue;

    public static YumlColours interface_colour = green;

    private static final Set<YumlColours> alreadyAssigned = new HashSet<>();
    private static final YumlColours default_color_when_exhausted = aliceblue;
    private static final YumlColours[] excludeColors = {black, white, interface_colour, default_color_when_exhausted, hotpink};
    private static final Set<YumlColours> excludedColors = Arrays.stream(excludeColors).collect(Collectors.toSet());

    public static YumlColours assignColour() {
        YumlColours result = null;
        for (YumlColours colour : YumlColours.values()) {
            if (!excludedColors.contains(colour) && !alreadyAssigned.contains(colour)) {
                alreadyAssigned.add(colour);
                result = colour;
                break;
            }
        }
        if (result == null) {
            result = default_color_when_exhausted;
        }

        return result;
    }
}
