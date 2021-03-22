package yuml.generator.kafka.streams;

import org.apache.kafka.streams.TopologyDescription;

import java.util.*;

@SuppressWarnings("unused")
public class YumlDescription {

    public static String describe(TopologyDescription topologyDescription) {
        StringBuilder sb = new StringBuilder();
        TopologyDescription.Subtopology[] sortedSubtopologies = topologyDescription.subtopologies().toArray(new TopologyDescription.Subtopology[0]);
        TopologyDescription.GlobalStore[] sortedGlobalStores = topologyDescription.globalStores().toArray(new TopologyDescription.GlobalStore[0]);
        int expectedId = 0;
        int subtopologiesIndex = sortedSubtopologies.length - 1;

        int globalStoresIndex;
        Set<YumlGenerator> yumlGenerators = new HashSet<>();
        TopologyDescription.Subtopology subtopology;
        for (globalStoresIndex = sortedGlobalStores.length - 1; subtopologiesIndex != -1 && globalStoresIndex != -1; ++expectedId) {
            subtopology = sortedSubtopologies[subtopologiesIndex];
            TopologyDescription.GlobalStore globalStore = sortedGlobalStores[globalStoresIndex];
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
            TopologyDescription.GlobalStore globalStore = sortedGlobalStores[globalStoresIndex];
            yumlGenerators.add(new YumlGlobalStore(globalStore));
            --globalStoresIndex;
        }

        for (YumlGenerator yumlGenerator : yumlGenerators) {
            sb.append(yumlGenerator.draw());
        }

        return sb.toString();
    }
}
