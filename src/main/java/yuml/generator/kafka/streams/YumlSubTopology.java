package yuml.generator.kafka.streams;

import org.apache.kafka.streams.TopologyDescription;

import java.util.HashMap;
import java.util.Map;

class YumlSubTopology implements YumlGenerator {
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

        StringBuilder result = new StringBuilder();
        for (YumlNode yumlNode : yumlNodes.values()) {
            result.append(yumlNode.draw());
        }

        return result.toString();
    }
}
