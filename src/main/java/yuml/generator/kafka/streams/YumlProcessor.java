package yuml.generator.kafka.streams;

import org.apache.kafka.streams.TopologyDescription;

class YumlProcessor extends YumlNode {

    public YumlProcessor(TopologyDescription.Node node, YumlSubTopology subTopology) {
        super(node, subTopology);
    }

    @Override
    public String draw() {
        StringBuilder result;
        TopologyDescription.Processor processorNode = (TopologyDescription.Processor) node;

        result = new StringBuilder(successors());
        for (String store : processorNode.stores()) {
            result.append(body()).append("--").append(body("Store", filterPrefix(store))).append("\n");
        }

        return result.toString();
    }


}
