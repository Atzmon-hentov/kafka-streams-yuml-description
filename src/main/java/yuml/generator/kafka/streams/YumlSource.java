package yuml.generator.kafka.streams;

import org.apache.kafka.streams.TopologyDescription;

import java.util.ArrayList;
import java.util.List;

class YumlSource extends YumlNode {
    public List<String> topics = new ArrayList<>();

    public YumlSource(TopologyDescription.Node node, YumlSubTopology subTopology) {
        super(node, subTopology);
        TopologyDescription.Source sourceNode = (TopologyDescription.Source) node;
        if (sourceNode.topicSet() == null) {
            topics.add(sourceNode.topicPattern().toString());
        } else {
            topics.addAll(sourceNode.topicSet());
        }
    }


    @Override
    public String additionalContents() {
        StringBuilder result = new StringBuilder();

        for (String topic : topics) {
            result.append(body("Topic", filterPrefix(topic))).append("->").append(body()).append("\n");
        }

        return result.toString();
    }

}
