package Product;

import jade.core.behaviours.SimpleBehaviour;

public class newExecPlanStep extends SimpleBehaviour {

    String skill;
    public newExecPlanStep (String skill){
        this.skill = skill;
    }

    @Override
    public void action() {
        ((ProductAgent)myAgent).msgExecuteSkill.clearAllReplyTo();
        ((ProductAgent)myAgent).msgExecuteSkill.clearAllReceiver();
        ((ProductAgent)myAgent).cfp.clearAllReceiver();
        ((ProductAgent)myAgent).cfp.clearAllReplyTo();
        ((ProductAgent)myAgent).currentSkill = skill;
        ((ProductAgent)myAgent).skillReserved = false;
        ((ProductAgent)myAgent).skillDone = false;
        ((ProductAgent)myAgent).msgInformRes.clearAllReplyTo();
        ((ProductAgent)myAgent).msgInformRes.clearAllReceiver();
    }

    @Override
    public boolean done() {
        return true;
    }
}
