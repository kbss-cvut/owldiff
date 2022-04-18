package cz.cvut.kbss.owldiff.api.util;

import cz.cvut.kbss.owldiff.view.nodes.NodeModel;

public class WriteCallbackImpl implements NodeModel.WriteCallback {
    private String ret = null;
    @Override
    public void notify(String returnValue) {
        this.ret = returnValue;
    }

    public String getRet() {
        String tmp = this.ret;
        this.ret = null;
        return tmp;
    }
}
