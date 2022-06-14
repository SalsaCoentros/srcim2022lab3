package Product;

import Resource.ResourceAgent;
import Utilities.Constants;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.StringTokenizer;


public class SkillExecutionRequest extends AchieveREInitiator{

    public SkillExecutionRequest(Agent a, ACLMessage msg){
        super(a, msg);
    }

    @Override
    protected void handleInform(ACLMessage inform){
        ((ProductAgent)myAgent).skillDone = true;
        if (((ProductAgent)myAgent).currentSkill.equals(Constants.SK_DROP)) { //create the message to tell the resource that does the drop that it is free
            ((ProductAgent)myAgent).msgInformRes.clearAllReplyTo();
            ((ProductAgent)myAgent).msgInformRes.clearAllReceiver();
            ((ProductAgent) myAgent).msgInformRes.addReceiver(new AID(((ProductAgent)myAgent).currentSkillReservedFrom,false));
            ((ProductAgent) myAgent).msgInformRes.setContent("true");
        }

        if (inform.getOntology().equals(Constants.ONTOLOGY_QUALITY_CHECK)) {
            int quality = Integer.parseInt(inform.getContent()); //quality = 1, NOT OK

            if (quality == 1 && !((ProductAgent)myAgent).qualityCheckDone) { //initialize recovery sequence
                boolean pickUp = false;
                boolean qualityCheck = false;
                SequentialBehaviour sb1 = new SequentialBehaviour();
                for (String s : ((ProductAgent)myAgent).executionPlan) {
                    if (pickUp && !qualityCheck) {
                        sb1.addSubBehaviour(new newExecPlanStep(s));
                        sb1.addSubBehaviour(new GetSkillfulAgent());
                        sb1.addSubBehaviour(new SkillNegotiation(myAgent, ((ProductAgent)myAgent).cfp));
                        sb1.addSubBehaviour(new GetSkillfulAgent());
                        sb1.addSubBehaviour(new getTransport(myAgent, ((ProductAgent)myAgent).cfp));
                        sb1.addSubBehaviour(new InformResourceClear(myAgent, ((ProductAgent)myAgent).msgInformRes));
                        sb1.addSubBehaviour(new SkillExecutionRequest(myAgent, ((ProductAgent)myAgent).msgExecuteSkill));
                    }

                    if (s.equals(Constants.SK_PICK_UP))
                        pickUp = true;
                    if (s.equals(Constants.SK_QUALITY_CHECK))
                        qualityCheck = true;
                }
                myAgent.addBehaviour(sb1);

            } else { //initialize drop sequence
                boolean qualityCheck = false;
                SequentialBehaviour sb2 = new SequentialBehaviour();
                for (String s : ((ProductAgent)myAgent).executionPlan) {
                    if (qualityCheck) {
                        sb2.addSubBehaviour(new newExecPlanStep(s));
                        sb2.addSubBehaviour(new GetSkillfulAgent());
                        sb2.addSubBehaviour(new SkillNegotiation(myAgent, ((ProductAgent)myAgent).cfp));
                        sb2.addSubBehaviour(new GetSkillfulAgent());
                        sb2.addSubBehaviour(new getTransport(myAgent, ((ProductAgent)myAgent).cfp));
                        sb2.addSubBehaviour(new InformResourceClear(myAgent, ((ProductAgent)myAgent).msgInformRes));
                        sb2.addSubBehaviour(new SkillExecutionRequest(myAgent, ((ProductAgent)myAgent).msgExecuteSkill));
                    }

                    if (s.equals(Constants.SK_QUALITY_CHECK))
                        qualityCheck = true;
                }
                sb2.addSubBehaviour(new InformResourceClear(myAgent, ((ProductAgent)myAgent).msgInformRes));
                sb2.addSubBehaviour(new DestroyProduct());
                myAgent.addBehaviour(sb2);
            }

            ((ProductAgent)myAgent).qualityCheckDone = true;
            System.out.print(myAgent.getLocalName() + ": " + inform.getSender().getLocalName() + " has DONE the skill " + ((ProductAgent)myAgent).currentSkill
                                + " with the result: ");
            switch (quality) {
                case 1 -> System.out.println("NOT OK");
                case 2 -> System.out.println("OK");
            }
        }
    }
}

