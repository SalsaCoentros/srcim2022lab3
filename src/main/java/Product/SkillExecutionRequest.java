package Product;

import Resource.ResourceAgent;
import Utilities.Constants;
import jade.core.AID;
import jade.core.Agent;
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
        System.out.println(myAgent.getLocalName() + ": " + inform.getSender().getLocalName() + " has DONE the skill " + ((ProductAgent)myAgent).currentSkill);
        if (((ProductAgent)myAgent).currentSkill.equals(Constants.SK_DROP)) { //create the message to tell the resource that does the drop that it is free
            ((ProductAgent)myAgent).msgInformRes.clearAllReplyTo();
            ((ProductAgent)myAgent).msgInformRes.clearAllReceiver();
            ((ProductAgent) myAgent).msgInformRes.addReceiver(new AID(((ProductAgent)myAgent).currentSkillReservedFrom,false));
            ((ProductAgent) myAgent).msgInformRes.setContent("true");
        }

        if (inform.getOntology().equals(Constants.ONTOLOGY_QUALITY_CHECK)) {
            int quality = Integer.parseInt(inform.getContent()); //quality = 1, NOT OK

            if (quality == 1) { //initialize recovery sequence
                // TO DO

            } else { //initialize drop sequence
                //TO DO
            }
        }
    }
}

