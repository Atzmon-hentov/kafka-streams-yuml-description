package yuml.generator.kafka.streams;

import org.apache.kafka.streams.TopologyDescription;

abstract class YumlNode {

    protected TopologyDescription.Node node;
    protected YumlSubTopology subTopology;

    public YumlNode(TopologyDescription.Node node, YumlSubTopology subTopology) {
        this.node = node;
        this.subTopology = subTopology;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YumlNode)) return false;
        YumlNode yumlNode = (YumlNode) o;
        return node.equals(yumlNode.node);
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
        StringBuilder result = new StringBuilder();
        for (TopologyDescription.Node successor : node.successors()) {
            result.append(body()).append("-->").append(subTopology.yumlNodes.get(successor).body()).append("\n");
        }
        return result.toString();
    }

    public String draw() {
        String result = "";
        result += successors();
        result += additionalContents();
        return result;
    }

}
