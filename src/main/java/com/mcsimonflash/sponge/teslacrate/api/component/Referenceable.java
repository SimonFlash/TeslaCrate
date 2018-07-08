package com.mcsimonflash.sponge.teslacrate.api.component;

public abstract class Referenceable<V> extends Component {

    protected Referenceable(String name) {
        super(name);
    }

    public abstract V getRefValue();

    public abstract Reference<? extends Referenceable<V>, V> createRef(String name);

}