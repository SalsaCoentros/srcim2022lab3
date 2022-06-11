package Product;

import jade.core.behaviours.OneShotBehaviour;

public class DestroyProduct extends OneShotBehaviour {

    @Override
    public void action() {
        myAgent.doDelete();
    }
}
