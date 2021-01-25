package com.aternity.comets.kafka.yuml;

import org.apache.kafka.streams.TopologyDescription;
import org.apache.kafka.streams.processor.To;

import java.util.*;
import java.util.stream.Collectors;

public class YumlDescription {

    public static String describe(TopologyDescription topologyDescription) {
        StringBuilder sb = new StringBuilder();
        org.apache.kafka.streams.TopologyDescription.Subtopology[] sortedSubtopologies = topologyDescription.subtopologies().toArray(new org.apache.kafka.streams.TopologyDescription.Subtopology[0]);
        org.apache.kafka.streams.TopologyDescription.GlobalStore[] sortedGlobalStores = topologyDescription.globalStores().toArray(new org.apache.kafka.streams.TopologyDescription.GlobalStore[0]);
        int expectedId = 0;
        int subtopologiesIndex = sortedSubtopologies.length - 1;

        int globalStoresIndex;
        Set<YumlGenerator> yumlGenerators = new HashSet<>();
        org.apache.kafka.streams.TopologyDescription.Subtopology subtopology;
        for (globalStoresIndex = sortedGlobalStores.length - 1; subtopologiesIndex != -1 && globalStoresIndex != -1; ++expectedId) {
            subtopology = sortedSubtopologies[subtopologiesIndex];
            org.apache.kafka.streams.TopologyDescription.GlobalStore globalStore = sortedGlobalStores[globalStoresIndex];
            if (subtopology.id() == expectedId) {
                subtopology = sortedSubtopologies[subtopologiesIndex];
                yumlGenerators.add(new YumlSubTopology(subtopology));
                --subtopologiesIndex;
            } else {
                yumlGenerators.add(new YumlGlobalStore(globalStore));
                sb.append(globalStore);
                --globalStoresIndex;
            }
        }

        while (subtopologiesIndex != -1) {
            subtopology = sortedSubtopologies[subtopologiesIndex];
            yumlGenerators.add(new YumlSubTopology(subtopology));
            --subtopologiesIndex;
        }

        while (globalStoresIndex != -1) {
            org.apache.kafka.streams.TopologyDescription.GlobalStore globalStore = sortedGlobalStores[globalStoresIndex];
            yumlGenerators.add(new YumlGlobalStore(globalStore));
            --globalStoresIndex;
        }

        for (YumlGenerator yumlGenerator : yumlGenerators) {
            sb.append(yumlGenerator.draw());
        }

        return sb.toString();
    }

    private interface YumlGenerator {
        String title();

        String draw();
    }

    private static class YumlGlobalStore implements YumlGenerator {
        org.apache.kafka.streams.TopologyDescription.GlobalStore globalStore;

        public YumlGlobalStore(org.apache.kafka.streams.TopologyDescription.GlobalStore globalStore) {
            this.globalStore = globalStore;
        }

        @Override
        public String title() {
            return "Global store " + globalStore.id();
        }

        @Override
        public String draw() {
            return "store " + globalStore;
        }
    }

    private static abstract class YumlNode {

        protected TopologyDescription.Node node;
        protected YumlSubTopology subTopology;

        public YumlNode(TopologyDescription.Node node, YumlSubTopology subTopology) {
            this.node = node;
            this.subTopology = subTopology;
        }

        @Override
        public boolean equals(Object o) {
            return this.node == ((YumlNode) o).node;
        }


        public String body() {
            String result = "";

            result += body(streotype(), nodeName());

            return result;
        }

        public String streotype() {
            return this.getClass().getSimpleName().substring(4);
        }

        public String additionalContents() {
            return "";
        }

        public String body(String streotype, String name) {
            String result = "";

            result += "[<<" + streotype + ">>;";
            result += name;
            result += " {bg:" + subTopology.color + "}";
            result += contents();
            result += "]";

            return result;
        }

        public String contents() {
            return "";
        }

        public String name() {
            return node.name();
        }

        String nodeName() {
            return filterPrefix(name());
        }

        String filterPrefix(String string) {
            return string.replaceAll("dev-atzmon-", "");
        }

        protected String successors() {
            String result = "";
            for (TopologyDescription.Node successor : node.successors()) {
                result += body() + "-->" + subTopology.yumlNodes.get(successor).body() + "\n";
            }
            return result;
        }

        public String draw() {
            String result = "";
            result += successors();
            result += additionalContents();
            return result;
        }

    }

    private static class YumlProcessor extends YumlNode {

        public YumlProcessor(TopologyDescription.Node node, YumlSubTopology subTopology) {
            super(node, subTopology);
        }

        @Override
        public String draw() {
            String result = "";
            TopologyDescription.Processor processorNode = (TopologyDescription.Processor) node;

            result = successors();
            for (String store : processorNode.stores()) {
                result += body() + "--" + body("Store", filterPrefix(store)) + "\n";
            }

            return result;
        }


    }

    private static class YumlSource extends YumlNode {
        public List<String> topics = new ArrayList<>();

        public YumlSource(TopologyDescription.Node node, YumlSubTopology subTopology) {
            super(node, subTopology);
            TopologyDescription.Source sourceNode = (TopologyDescription.Source) node;
            if (sourceNode.topicSet() == null) {
                topics.add(sourceNode.topicPattern().toString());
            } else {
                for (String topic : sourceNode.topicSet()) {
                    topics.add(topic);
                }
            }
        }


        @Override
        public String additionalContents() {
            String result = "";

            for (String topic : topics) {
                result += body("Topic", filterPrefix(topic)) + "->" + body() + "\n";
            }

            return result;
        }

    }

    private static class YumlSink extends YumlNode {

        public YumlSink(TopologyDescription.Node node, YumlSubTopology subTopology) {
            super(node, subTopology);
        }

        @Override
        public String name() {
            TopologyDescription.Sink sinkNode = (TopologyDescription.Sink)node;
            return sinkNode.topic() != null ? sinkNode.topic() : sinkNode.name();
        }

        @Override
        public String streotype() {
            return "Topic";
        }

    }

    private static class YumlSubTopology implements YumlGenerator {
        org.apache.kafka.streams.TopologyDescription.Subtopology subTopology;
        YumlColours color;

        public YumlSubTopology(org.apache.kafka.streams.TopologyDescription.Subtopology subTopology) {
            this.subTopology = subTopology;
            color = YumlColours.assignColour();
        }

        @Override
        public String title() {
            return "Subtopology " + subTopology.id();
        }

        protected Map<TopologyDescription.Node, YumlNode> yumlNodes = new HashMap<>();

        public String draw() {
            String result = "";

            for (TopologyDescription.Node node : subTopology.nodes()) {
                if (node instanceof TopologyDescription.Source) {
                    TopologyDescription.Source sourceNode = (TopologyDescription.Source) node;

                    YumlSource yumlNode = new YumlSource(sourceNode, this);
                    yumlNodes.put(sourceNode, yumlNode);
                } else if (node instanceof TopologyDescription.Processor) {
                    TopologyDescription.Processor processorNode = (TopologyDescription.Processor) node;
                    YumlProcessor yumlNode = new YumlProcessor(processorNode, this);
                    yumlNodes.put(processorNode, yumlNode);
                } else if (node instanceof TopologyDescription.Sink) {
                    TopologyDescription.Sink sinkNode = (TopologyDescription.Sink) node;
                    YumlSink yumlNode = new YumlSink(sinkNode, this);
                    yumlNodes.put(sinkNode, yumlNode);
                } else {
                    throw new RuntimeException("Unexpected node type '" + node.getClass().getCanonicalName());
                }
            }

            for (YumlNode yumlNode : yumlNodes.values()) {
                result += yumlNode.draw();
            }

            return result;
        }
    }
}


enum YumlColours {
    yellowgreen
    ,yellow
    ,whitesmoke
    ,white
    ,wheat
    ,violet
    ,turquoise
    ,tomato
    ,thistle
    ,tan
    ,steelblue
    ,springgreen
    ,snow
    ,slategray
    ,slateblue
    ,skyblue
    ,sienna
    ,seashell
    ,seagreen
    ,sandybrown
    ,salmon
    ,saddlebrown
    ,royalblue
    ,rosybrown
    ,red
    ,purple
    ,powderblue
    ,plum
    ,pink
    ,peru
    ,peachpuff
    ,papayawhip
    ,palevioletred
    ,paleturquoise
    ,palegreen
    ,palegoldenrod
    ,orchid
    ,orangered
    ,orange
    ,olivedrab
    ,oldlace
    ,navajowhite
    ,moccasin
    ,mistyrose
    ,mediumvioletred
    ,mediumturquoise
    ,mediumspringgreen
    ,mediumslateblue
    ,mediumseagreen
    ,mediumpurple
    ,mediumorchid
    ,mediumblue
    ,mediumaquamarine
    ,maroon
    ,magenta
    ,linen
    ,limegreen
    ,lightyellow
    ,lightsteelblue
    ,lightslategray
    ,lightskyblue
    ,lightseagreen
    ,lightsalmon
    ,lightpink
    ,lightgray
    ,lightcyan
    ,lightcoral
    ,lightblue
    ,lemonchiffon
    ,lawngreen
    ,lavenderblush
    ,lavender
    ,khaki
    ,indianred
    ,hotpink
    ,honeydew
    ,greenyellow
    ,green
    ,gray
    ,goldenrod
    ,gold
    ,ghostwhite
    ,gainsboro
    ,forestgreen
    ,floralwhite
    ,firebrick
    ,dodgerblue
    ,deepskyblue
    ,deeppink
    ,darkviolet
    ,darkslateblue
    ,darkseagreen
    ,darksalmon
    ,darkorchid
    ,darkorange
    ,darkgoldenrod
    ,cyan
    ,crimson
    ,cornsilk
    ,coral
    ,chocolate
    ,chartreuse
    ,cadetblue
    ,burlywood
    ,brown
    ,blueviolet
    ,blue
    ,black
    ,bisque
    ,beige
    ,azure
    ,aquamarine
    ,antiquewhite
    ,aliceblue;

    public static YumlColours interface_colour = green;

    private static Set<YumlColours> alreadyAssigned = new HashSet<>();
    private static YumlColours default_color_when_exhausted = aliceblue;
    private static YumlColours[] excludeColors = {black, white, interface_colour, default_color_when_exhausted, hotpink};
    private static Set<YumlColours> excludedColors = Arrays.stream(excludeColors).collect(Collectors.toSet());

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
