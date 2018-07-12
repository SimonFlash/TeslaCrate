package com.mcsimonflash.sponge.teslacrate.api.component;

public abstract class Referenceable<V> extends Component {

    protected Referenceable(String id) {
        super(id);
    }

    public abstract V getRefValue();

    public abstract Reference<? extends Referenceable<V>, V> createRef(String id);

}