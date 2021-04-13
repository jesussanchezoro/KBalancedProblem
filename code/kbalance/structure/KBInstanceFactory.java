package kbalance.structure;

import grafo.optilib.structure.InstanceFactory;

public class KBInstanceFactory extends InstanceFactory<KBInstance> {
    @Override
    public KBInstance readInstance(String s) {
        return new KBInstance(s);
    }
}
