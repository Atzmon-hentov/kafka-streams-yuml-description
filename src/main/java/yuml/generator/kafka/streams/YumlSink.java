package yuml.generator.kafka.streams;

import org.apache.kafka.streams.TopologyDescription;

class YumlSink extends YumlNode {

    public YumlSink(TopologyDescription.Node node, YumlSubTopology subTopology) {
        super(node, subTopology);
    }

    @Override
    public String name() {
        TopologyDescription.Sink sinkNode = (TopologyDescription.Sink) node;
        return sinkNode.topic() != null ? sinkNode.topic() : sinkNode.name();
    }

    @Override
    public String streotype() {
        return "Topic";
    }

}
