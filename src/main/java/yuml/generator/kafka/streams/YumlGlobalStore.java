package yuml.generator.kafka.streams;

class YumlGlobalStore implements YumlGenerator {
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
